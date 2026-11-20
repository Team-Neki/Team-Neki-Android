# frozen_string_literal: true

require "json"
require "fileutils"
require "net/http"
require "open3"
require "pathname"
require "rubygems"
require "time"

module PlayRelease
  ROOT = File.expand_path("../..", __dir__)
  BUILD_CONST = "build-logic/src/main/java/com/neki/android/buildlogic/const/BuildConst.kt"
  PACKAGE = "com.neki.android"
  SUBMITTED = %w[IN_REVIEW APPROVED_NOT_PUBLISHED PUBLISHED].freeze
  STATES = {
    "DRAFT" => "임시 저장",
    "NOT_SENT_FOR_REVIEW" => "검토 미제출",
    "IN_REVIEW" => "검토 중",
    "APPROVED_NOT_PUBLISHED" => "승인 완료 · 게시 대기",
    "NOT_APPROVED" => "검토 거절",
    "PUBLISHED" => "게시됨"
  }.freeze
  START = "<!-- neki-play-status:start -->"
  FINISH = "<!-- neki-play-status:end -->"
  MARKER = /<!-- neki-play-release (\{[^\n]+\}) -->/

  def self.capture(*args, stdin: nil)
    out, err, result = Open3.capture3(*args, stdin_data: stdin)
    raise "#{args.first} failed: #{err.strip}" unless result.success?

    out
  end

  def self.version_from(text)
    text.match(/\bVERSION_NAME\s*=\s*"((?:0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*))"/)&.[](1) ||
      raise("VERSION_NAME must be a stable major.minor.patch version")
  end

  def self.candidate_name(version, tree)
    raise "Invalid source tree" unless tree.match?(/\A[0-9a-f]{40}\z/)
    name = "#{version} tree:#{tree[0, 32]}"
    raise "Play release name exceeds 50 characters" if name.length > 50

    name
  end

  class Config
    attr_reader :version, :notes

    def initialize(root = ROOT)
      @version = PlayRelease.version_from(File.read(File.join(root, BUILD_CONST)))
      directory = File.join(root, "release-notes", version)
      @notes = Dir.glob(File.join(directory, "*.txt")).sort.to_h do |path|
        raise "Release notes must stay inside their version directory" unless File.realpath(path).start_with?(File.realpath(directory) + File::SEPARATOR)
        language = File.basename(path, ".txt")
        text = File.read(path, encoding: "UTF-8").strip
        raise "Invalid release note locale: #{language}" unless language.match?(/\A[a-z]{2,3}(?:-[A-Za-z0-9]{2,8})*\z/)
        raise "Release notes must contain 1..500 characters: #{path}" unless (1..500).cover?(text.length)
        raise "Remove release note placeholders: #{path}" if text.match?(/TODO|출시 노트를 여기에|PLACEHOLDER/i)
        raise "Reserved release status marker in notes" if text.include?("<!-- neki-")

        [language, text]
      end
      raise "Missing release-notes/#{version}/ko-KR.txt" unless notes.key?("ko-KR")
    end

    def tag
      "v#{version}"
    end

    def release_name(sha)
      "#{version} (#{sha[0, 12]})"
    end

    def validate_pr!(title:, base_version:)
      raise "Release PR title must be [release] #{tag}" unless title == "[release] #{tag}"
      raise "Release version must be newer than #{base_version}" unless Gem::Version.new(version) > Gem::Version.new(base_version)
    end

    def write_metadata(directory)
      notes.each do |language, text|
        path = File.join(directory, language, "changelogs")
        FileUtils.mkdir_p(path)
        File.write(File.join(path, "default.txt"), text)
      end
    end
  end

  class GitHub
    def initialize(repo = ENV.fetch("GITHUB_REPOSITORY"))
      raise "Invalid GitHub repository" unless repo.match?(%r{\A[\w.-]+/[\w.-]+\z})

      @repo = repo
    end

    def api(path, method: "GET", data: nil, paginate: false, missing: false)
      args = ["gh", "api", "--method", method, "repos/#{@repo}/#{path}"]
      args += ["--input", "-"] if data
      args += ["--paginate", "--slurp"] if paginate
      out, err, result = Open3.capture3(*args, stdin_data: data && JSON.generate(data))
      return nil if missing && !result.success? && err.include?("(HTTP 404)")
      raise "GitHub #{method} #{path} failed: #{err.strip}" unless result.success?
      return nil if out.strip.empty?

      parsed = JSON.parse(out)
      paginate ? parsed.flatten(1) : parsed
    end

    def release(tag)
      api("releases/tags/#{tag}", missing: true)
    end

    def releases
      api("releases?per_page=100", paginate: true)
    end

    def check_tag!(tag, sha)
      ref = api("git/ref/tags/#{tag}", missing: true)
      if ref
        object = ref.fetch("object")
        object = api("git/tags/#{object.fetch('sha')}").fetch("object") while object.fetch("type") == "tag"
        raise "#{tag} already points to a different commit" unless object.fetch("sha") == sha
      end
      !ref.nil?
    end

    def ensure_tag!(tag, sha)
      api("git/refs", method: "POST", data: { ref: "refs/tags/#{tag}", sha: sha }) unless check_tag!(tag, sha)
    end

    def generated_notes(tag, sha)
      api("releases/generate-notes", method: "POST", data: { tag_name: tag, target_commitish: sha }).fetch("body")
    end

    def create_release(tag, sha, body)
      api("releases", method: "POST", data: { tag_name: tag, target_commitish: sha, name: tag, body: body, draft: false, prerelease: false })
    end

    def update_release(release, body)
      api("releases/#{release.fetch('id')}", method: "PATCH", data: { body: body })
    end
  end

  # GET only: status checks never open edits or invalidate an in-flight upload.
  class Play
    def initialize(key_path = ENV.fetch("PLAY_STORE_JSON_KEY_PATH"))
      require "googleauth"
      @credentials = File.open(key_path) do |file|
        Google::Auth::ServiceAccountCredentials.make_creds(json_key_io: file, scope: "https://www.googleapis.com/auth/androidpublisher")
      end
    end

    def releases(track = "production")
      token = @credentials.fetch_access_token!.fetch("access_token")
      uri = URI("https://androidpublisher.googleapis.com/androidpublisher/v3/applications/#{PACKAGE}/tracks/#{track}/releases")
      request = Net::HTTP::Get.new(uri)
      request["Authorization"] = "Bearer #{token}"
      response = Net::HTTP.start(uri.host, uri.port, use_ssl: true, open_timeout: 15, read_timeout: 60) { |http| http.request(request) }
      raise "Play release status query failed (HTTP #{response.code})" unless response.is_a?(Net::HTTPSuccess)

      JSON.parse(response.body).fetch("releases", [])
    end
  end

  def self.state(release)
    release.fetch("releaseLifecycleState").delete_prefix("RELEASE_LIFECYCLE_STATE_")
  end

  def self.version_code(release)
    codes = release.fetch("activeArtifacts").map { |artifact| Integer(artifact.fetch("versionCode")) }
    raise "Expected exactly one positive versionCode" unless codes.length == 1 && codes.first.positive?

    codes.first
  end

  def self.manifest(body)
    matches = body.to_s.scan(MARKER)
    return nil if matches.empty?
    raise "Multiple Play release manifests" unless matches.length == 1

    data = JSON.parse(matches.first.first)
    raise "Unsupported Play release manifest" unless data["schema"] == 1
    raise "Invalid release SHA" unless data.fetch("sha").match?(/\A[0-9a-f]{40}\z/)
    raise "Invalid versionCode" unless data.fetch("version_code").is_a?(Integer) && data.fetch("version_code").positive?
    raise "Invalid release state" unless STATES.key?(data.fetch("state"))

    data
  end

  def self.status_block(data)
    <<~TEXT.strip
      #{START}
      ## Google Play 프로덕션 상태

      - 상태: **#{STATES.fetch(data.fetch('state'))}**
      - 버전: #{data.fetch('version')} (versionCode #{data.fetch('version_code')})
      - 배포 커밋: `#{data.fetch('sha')}`
      - 상태 변경 확인: #{data.fetch('updated_at')}

      '게시됨'은 Play의 트랙 제공 상태입니다. 단계적 출시 비율·중지 여부는 Play Console에서 확인하세요.
      <!-- neki-play-release #{JSON.generate(data)} -->
      #{FINISH}
    TEXT
  end

  def self.replace_status(body, data)
    pattern = /#{Regexp.escape(START)}.*?#{Regexp.escape(FINISH)}/m
    raise "Missing or duplicate managed release status block" unless body.scan(pattern).length == 1

    body.sub(pattern) { status_block(data) }
  end

  class Production
    def initialize(config:, sha:, tree:, github:, play:, sleeper: -> { sleep 15 }, clock: -> { Time.now.utc.iso8601 })
      raise "Invalid release SHA" unless sha.match?(/\A[0-9a-f]{40}\z/)

      @candidate_name = PlayRelease.candidate_name(config.version, tree)
      @config, @sha, @tree, @github, @play, @sleeper, @clock = config, sha, tree, github, play, sleeper, clock
    end

    # upload receives an existing versionCode for a retry, or nil for a new build.
    def run(&upload)
      tagged = @github.check_tag!(@config.tag, @sha)
      existing_github = @github.release(@config.tag)
      if existing_github
        data = PlayRelease.manifest(existing_github.fetch("body"))
        raise "Existing GitHub release has a different source" unless data && data["sha"] == @sha && data["version"] == @config.version
        @github.ensure_tag!(@config.tag, @sha)
        return data
      end

      releases = @play.releases
      name = @config.release_name(@sha)
      names = [name, @candidate_name]
      same_version = releases.select { |release| release.fetch("releaseName").start_with?("#{@config.version} (", "#{@config.version} tree:") }
      raise "Version already uploaded from a different source" if same_version.any? { |release| !names.include?(release.fetch("releaseName")) }
      existing = releases.find { |release| names.include?(release.fetch("releaseName")) }
      raise "Tagged release is not visible in Play; refusing to upload it again" if tagged && existing.nil?
      if existing && PlayRelease.state(existing) == "NOT_APPROVED"
        raise "This version was rejected; prepare a corrected release PR"
      end
      submitted = existing && SUBMITTED.include?(PlayRelease.state(existing))
      unless submitted
        raise "Another production release is in review" if releases.any? { |release| PlayRelease.state(release) == "IN_REVIEW" && release != existing }
        candidate = unless existing
          @play.releases("internal")
            .select { |release| release.fetch("releaseName") == @candidate_name && PlayRelease.state(release) == "PUBLISHED" }
            .max_by { |release| PlayRelease.version_code(release) }
        end
        source = existing || candidate
        source_track = existing ? "production" : (candidate && "internal")
        code = upload.call(source && PlayRelease.version_code(source), source_track)
        12.times do
          existing = @play.releases.find { |release| names.include?(release.fetch("releaseName")) && PlayRelease.version_code(release) == code }
          break if existing && SUBMITTED.include?(PlayRelease.state(existing))
          raise "Production release was rejected" if existing && PlayRelease.state(existing) == "NOT_APPROVED"
          @sleeper.call
        end
        raise "Upload finished but production review submission is not confirmed; no tag created" unless existing && SUBMITTED.include?(PlayRelease.state(existing))
      end

      data = {
        "schema" => 1, "version" => @config.version, "version_code" => PlayRelease.version_code(existing),
        "sha" => @sha, "artifact_tree" => @tree, "release_name" => existing.fetch("releaseName"),
        "state" => PlayRelease.state(existing), "updated_at" => @clock.call
      }
      @github.ensure_tag!(@config.tag, @sha)
      technical = @github.generated_notes(@config.tag, @sha)
      body = "## 업데이트 내용\n\n#{@config.notes.fetch('ko-KR')}\n\n#{technical}\n\n#{PlayRelease.status_block(data)}\n"
      @github.create_release(@config.tag, @sha, body)
      data
    end
  end

  def self.sync_statuses(github:, play:, clock: -> { Time.now.utc.iso8601 })
    managed = github.releases.filter_map do |release|
      next if release["draft"] || release["prerelease"]
      data = manifest(release["body"])
      next unless data && !%w[PUBLISHED NOT_APPROVED].include?(data.fetch("state"))

      [release, data]
    end
    return 0 if managed.empty?

    summaries = play.releases
    errors = []
    managed.each do |release, data|
      summary = summaries.find do |item|
        item["releaseName"] == data.fetch("release_name") && version_code(item) == data.fetch("version_code")
      end
      if summary.nil? || !STATES.key?(state(summary))
        errors << "#{release.fetch('tag_name')}: no known Play lifecycle state"
        next
      end
      next if state(summary) == data.fetch("state")

      updated = data.merge("state" => state(summary), "updated_at" => clock.call)
      github.update_release(release, replace_status(release.fetch("body"), updated))
    end
    raise errors.join("\n") unless errors.empty?

    managed.length
  end
end
