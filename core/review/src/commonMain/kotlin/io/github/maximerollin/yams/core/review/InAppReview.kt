package io.github.maximerollin.yams.core.review

public expect object InAppReview {
    public suspend fun requestReview()
}
