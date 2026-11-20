# frozen_string_literal: true

require "minitest/autorun"
require "tmpdir"
require_relative "../lib/play_release"
require_relative "../lib/play_review_guard"

class PlayReleaseTest < Minitest::Test
  SHA = "a" * 40
  OTHER_SHA = "b" * 40

  class FakeGitHub
    attr_accessor :existing, :tagged, :all_releases, :tag_error
    attr_reader :events, :created_body, :updates

    def initialize
      @events, @updates, @all_releases = [], [], []
    end

    def release(_tag)
      existing
    end

    def check_tag!(_tag, _sha)
      raise tag_error if tag_error
      !!tagged
    end

    def ensure_tag!(tag, sha)
      @events << [:tag, tag, sha]
    end

    def generated_notes(_tag, _sha)
      "## What's Changed\n- Fixed archive filter (#337)"
    end

    def create_release(tag, sha, body)
      @events << [:release, tag, sha]
      @created_body = body
    end

    def releases
      all_releases
    end

    def update_release(release, body)
      @updates << [release, body]
    end
  end

  class FakePlay
    attr_reader :calls

    def initialize(*responses)
      @responses, @calls = responses, 0
    end

    def releases
      response = @responses[[@calls, @responses.length - 1].min]
      @calls += 1
      raise response if response.is_a?(Exception)
      response
    end
  end

  def setup
    @root = Dir.mktmpdir
    path = File.join(@root, PlayRelease::BUILD_CONST)
    FileUtils.mkdir_p(File.dirname(path))
    File.write(path, 'const val VERSION_NAME = "1.4.6"')
    @notes_path = File.join(@root, "release-notes/1.4.6/ko-KR.txt")
    FileUtils.mkdir_p(File.dirname(@notes_path))
    File.write(@notes_path, "즐겨찾기 필터를 개선했어요.\n")
    @config = PlayRelease::Config.new(@root)
    @github = FakeGitHub.new
    @clock = -> { "2026-11-20T03:00:00Z" }
  end

  def teardown
    FileUtils.remove_entry(@root)
  end

  def summary(state = "IN_REVIEW", name: @config.release_name(SHA), code: 44)
    {
      "releaseName" => name, "track" => "production",
      "activeArtifacts" => [{ "versionCode" => code }],
      "releaseLifecycleState" => "RELEASE_LIFECYCLE_STATE_#{state}"
    }
  end

  def runner(play)
    PlayRelease::Production.new(config: @config, sha: SHA, github: @github, play: play, sleeper: -> {}, clock: @clock)
  end

  def no_upload
    ->(_code) { flunk "Must not upload another binary" }
  end

  def data(state = "IN_REVIEW")
    {
      "schema" => 1, "version" => "1.4.6", "version_code" => 44, "sha" => SHA,
      "release_name" => @config.release_name(SHA), "state" => state, "updated_at" => @clock.call
    }
  end

  def tracked_release(state = "IN_REVIEW")
    { "id" => 12, "tag_name" => "v1.4.6", "body" => "User notes stay intact\n#{PlayRelease.status_block(data(state))}\nDeveloper notes" }
  end

  def test_version_title_and_increase_must_match
    @config.validate_pr!(title: "[release] v1.4.6", base_version: "1.4.5")
    assert_raises(RuntimeError) { @config.validate_pr!(title: "[release] v1.4.7", base_version: "1.4.5") }
    assert_raises(RuntimeError) { @config.validate_pr!(title: "[release] v1.4.6", base_version: "1.4.6") }
    assert_raises(RuntimeError) { PlayRelease.version_from('VERSION_NAME = "1.4.6-rc1"') }
  end

  def test_missing_blank_oversize_and_placeholder_notes_fail
    ["", "a" * 501, "TODO: notes"].each do |text|
      File.write(@notes_path, text)
      assert_raises(RuntimeError) { PlayRelease::Config.new(@root) }
    end
    File.delete(@notes_path)
    assert_raises(RuntimeError) { PlayRelease::Config.new(@root) }
  end

  def test_release_notes_cannot_read_a_symlink_outside_the_version_directory
    File.delete(@notes_path)
    target = File.join(@root, "private.txt")
    File.write(target, "Not release notes")
    File.symlink(target, @notes_path)
    assert_raises(RuntimeError) { PlayRelease::Config.new(@root) }
  end

  def test_notes_at_limit_are_not_extended_by_metadata_writer
    File.write(@notes_path, "한" * 500)
    config = PlayRelease::Config.new(@root)
    path = File.join(@root, "metadata")
    config.write_metadata(path)
    assert_equal 500, File.read(File.join(path, "ko-KR/changelogs/default.txt")).length
  end

  def test_commit_guard_prevents_implicitly_cancelling_another_review
    base = Class.new do
      def commit_edit(package, edit, **options)
        [package, edit, options]
      end
    end
    guarded = Class.new(base) { prepend PlayReviewGuard }
    result = guarded.new.commit_edit("com.neki.android", "edit1", changes_not_sent_for_review: false)
    assert_equal "ERROR_IF_IN_REVIEW", result.last[:changes_in_review_behavior]
    assert_equal false, result.last[:changes_not_sent_for_review]
  end

  def test_metadata_contains_actual_notes_for_play
    path = File.join(@root, "metadata")
    @config.write_metadata(path)
    assert_equal "즐겨찾기 필터를 개선했어요.", File.read(File.join(path, "ko-KR/changelogs/default.txt"))
  end

  def test_new_release_uploads_once_then_creates_tag_and_release
    uploads = []
    play = FakePlay.new([], [summary])
    result = runner(play).run { |code| uploads << code; 44 }
    assert_equal [nil], uploads
    assert_equal "IN_REVIEW", result["state"]
    assert_equal [[:tag, "v1.4.6", SHA], [:release, "v1.4.6", SHA]], @github.events
    assert_includes @github.created_body, "즐겨찾기 필터를 개선했어요."
    assert_includes @github.created_body, "#337"
    assert_equal 44, PlayRelease.manifest(@github.created_body)["version_code"]
  end

  def test_submitted_release_retry_reuses_uploaded_binary
    runner(FakePlay.new([summary])).run(&no_upload)
    assert_equal 2, @github.events.length
  end

  def test_not_sent_release_retry_passes_existing_version_code
    codes = []
    result = runner(FakePlay.new([summary("NOT_SENT_FOR_REVIEW")], [summary])).run { |code| codes << code; code }
    assert_equal [44], codes
    assert_equal "IN_REVIEW", result["state"]
  end

  def test_already_created_release_is_idempotent
    @github.existing = tracked_release
    @github.tagged = true
    play = FakePlay.new(RuntimeError.new("Should not query Play"))
    assert_equal 44, runner(play).run(&no_upload)["version_code"]
    assert_equal 0, play.calls
    assert_nil @github.created_body
  end

  def test_existing_github_release_from_other_source_blocks_upload
    @github.existing = tracked_release.merge("body" => PlayRelease.status_block(data.merge("sha" => OTHER_SHA)))
    assert_raises(RuntimeError) { runner(FakePlay.new([])).run(&no_upload) }
  end

  def test_tag_collision_is_checked_before_upload
    @github.tag_error = "tag points to another SHA"
    assert_raises(RuntimeError) { runner(FakePlay.new([])).run(&no_upload) }
    assert_empty @github.events
  end

  def test_tagged_but_missing_play_release_does_not_reupload
    @github.tagged = true
    assert_raises(RuntimeError) { runner(FakePlay.new([])).run(&no_upload) }
  end

  def test_other_commit_with_same_version_blocks_upload
    releases = [summary(name: @config.release_name(OTHER_SHA))]
    assert_raises(RuntimeError) { runner(FakePlay.new(releases)).run(&no_upload) }
  end

  def test_other_review_is_not_cancelled_by_a_new_release
    releases = [summary(name: "1.4.5 (previous)")]
    assert_raises(RuntimeError) { runner(FakePlay.new(releases)).run(&no_upload) }
    assert_empty @github.events
  end

  def test_rejected_release_requires_corrected_pr
    assert_raises(RuntimeError) { runner(FakePlay.new([summary("NOT_APPROVED")])).run(&no_upload) }
    assert_empty @github.events
  end

  def test_no_tag_if_upload_succeeds_but_submission_is_unconfirmed
    play = FakePlay.new([], [summary("NOT_SENT_FOR_REVIEW")])
    assert_raises(RuntimeError) { runner(play).run { 44 } }
    assert_empty @github.events
  end

  def test_play_error_does_not_become_a_successful_release
    assert_raises(RuntimeError) { runner(FakePlay.new(RuntimeError.new("HTTP 403"))).run(&no_upload) }
    assert_empty @github.events
  end

  def test_ambiguous_artifacts_are_rejected
    release = summary.merge("activeArtifacts" => [{ "versionCode" => 44 }, { "versionCode" => 45 }])
    assert_raises(RuntimeError) { PlayRelease.version_code(release) }
  end

  def test_status_changes_preserve_notes_and_update_only_managed_block
    release = tracked_release
    @github.all_releases = [release]
    PlayRelease.sync_statuses(github: @github, play: FakePlay.new([summary("PUBLISHED")]), clock: @clock)
    body = @github.updates.first.last
    assert_includes body, "User notes stay intact"
    assert_includes body, "Developer notes"
    assert_includes body, "**게시됨**"
    assert_equal "PUBLISHED", PlayRelease.manifest(body)["state"]
  end

  def test_pending_status_variants_are_reported_exactly
    %w[APPROVED_NOT_PUBLISHED NOT_APPROVED NOT_SENT_FOR_REVIEW DRAFT].each do |state|
      @github = FakeGitHub.new
      @github.all_releases = [tracked_release]
      PlayRelease.sync_statuses(github: @github, play: FakePlay.new([summary(state)]), clock: @clock)
      assert_equal state, PlayRelease.manifest(@github.updates.first.last)["state"]
    end
  end

  def test_unchanged_status_does_not_rewrite_release
    @github.all_releases = [tracked_release]
    PlayRelease.sync_statuses(github: @github, play: FakePlay.new([summary]), clock: @clock)
    assert_empty @github.updates
  end

  def test_legacy_and_terminal_releases_are_not_polled
    @github.all_releases = [{ "body" => "legacy release" }, tracked_release("PUBLISHED"), tracked_release("NOT_APPROVED")]
    play = FakePlay.new(RuntimeError.new("Should not query Play"))
    assert_equal 0, PlayRelease.sync_statuses(github: @github, play: play)
    assert_equal 0, play.calls
  end

  def test_missing_play_release_is_not_marked_published
    @github.all_releases = [tracked_release]
    assert_raises(RuntimeError) { PlayRelease.sync_statuses(github: @github, play: FakePlay.new([])) }
    assert_empty @github.updates
  end

  def test_unknown_state_is_not_marked_published
    @github.all_releases = [tracked_release]
    assert_raises(RuntimeError) { PlayRelease.sync_statuses(github: @github, play: FakePlay.new([summary("NEW_STATE")])) }
    assert_empty @github.updates
  end
end
