# frozen_string_literal: true

require "fileutils"
require_relative "lib/play_release"

case ARGV.fetch(0)
when "validate"
  config = PlayRelease::Config.new
  title = ENV.fetch("RELEASE_PR_TITLE")
  base_sha = ENV.fetch("RELEASE_BASE_SHA")
  raise "Invalid base SHA" unless base_sha.match?(/\A[0-9a-f]{40}\z/)
  base = PlayRelease.capture("git", "show", "#{base_sha}:#{PlayRelease::BUILD_CONST}")
  config.validate_pr!(title: title, base_version: PlayRelease.version_from(base))
  puts "Release #{config.tag}: version and #{config.notes.keys.join(', ')} notes validated"
when "status"
  count = PlayRelease.sync_statuses(github: PlayRelease::GitHub.new, play: PlayRelease::Play.new)
  puts "Checked #{count} pending production release(s)"
else
  abort "Usage: ruby fastlane/release_cli.rb validate|status"
end
