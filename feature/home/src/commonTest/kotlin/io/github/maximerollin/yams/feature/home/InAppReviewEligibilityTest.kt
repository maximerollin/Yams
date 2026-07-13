package io.github.maximerollin.yams.feature.home

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class InAppReviewEligibilityTest {
    private val now = Instant.parse("2026-07-13T12:00:00Z")

    @Test
    fun requiresPendingReviewRequest() {
        assertFalse(
            shouldRequestInAppReview(
                isInAppReviewPending = false,
                numberOfFinishedGames = 3,
                lastInAppReviewShownDate = null,
                now = now,
            ),
        )
    }

    @Test
    fun requiresAtLeastThreeFinishedGames() {
        assertFalse(
            shouldRequestInAppReview(
                isInAppReviewPending = true,
                numberOfFinishedGames = 2,
                lastInAppReviewShownDate = null,
                now = now,
            ),
        )
    }

    @Test
    fun allowsFirstReviewRequestAfterThreshold() {
        assertTrue(
            shouldRequestInAppReview(
                isInAppReviewPending = true,
                numberOfFinishedGames = 3,
                lastInAppReviewShownDate = null,
                now = now,
            ),
        )
    }

    @Test
    fun blocksReviewRequestDuringCooldown() {
        assertFalse(
            shouldRequestInAppReview(
                isInAppReviewPending = true,
                numberOfFinishedGames = 12,
                lastInAppReviewShownDate = Instant.parse("2026-03-01T12:00:00Z"),
                now = now,
            ),
        )
    }

    @Test
    fun allowsReviewRequestAfterCooldown() {
        assertTrue(
            shouldRequestInAppReview(
                isInAppReviewPending = true,
                numberOfFinishedGames = 12,
                lastInAppReviewShownDate = Instant.parse("2025-12-01T12:00:00Z"),
                now = now,
            ),
        )
    }
}
