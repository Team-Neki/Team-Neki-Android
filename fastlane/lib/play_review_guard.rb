# frozen_string_literal: true

# Fastlane 2.237 does not expose changesInReviewBehavior. Its pinned Google
# client does; apply this only to the publishing client in the deploy process.
# A Console submission made during a build must not be cancelled by our commit.
module PlayReviewGuard
  def commit_edit(package_name, edit_id, **options, &block)
    super(package_name, edit_id, **options.merge(changes_in_review_behavior: "ERROR_IF_IN_REVIEW"), &block)
  end
end
