package io.github.maximerollin.yams.feature.home

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeInAppReviewDiscoveryOverlayTest {
    @Test
    fun discoveryIsHiddenWhilePreferenceIsLoading() {
        assertFalse(
            shouldShowHomeInAppReviewDiscovery(
                hasSeenDiscovery = null,
                canRequestInAppReview = true,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenWhenReviewButtonIsUnavailable() {
        assertFalse(
            shouldShowHomeInAppReviewDiscovery(
                hasSeenDiscovery = false,
                canRequestInAppReview = false,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenAfterItHasBeenSeen() {
        assertFalse(
            shouldShowHomeInAppReviewDiscovery(
                hasSeenDiscovery = true,
                canRequestInAppReview = true,
            ),
        )
    }

    @Test
    fun discoveryIsVisibleWhenReviewButtonIsAvailable() {
        assertTrue(
            shouldShowHomeInAppReviewDiscovery(
                hasSeenDiscovery = false,
                canRequestInAppReview = true,
            ),
        )
    }
}
