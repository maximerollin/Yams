package io.github.maximerollin.yams.feature.game.play.components

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GamePlayDensityDiscoveryOverlayTest {
    @Test
    fun discoveryIsHiddenWhilePreferenceIsLoading() {
        assertFalse(
            shouldShowGamePlayDensityDiscovery(
                hasSeenDiscovery = null,
                hasPlayers = true,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenWhenGameHasNoPlayers() {
        assertFalse(
            shouldShowGamePlayDensityDiscovery(
                hasSeenDiscovery = false,
                hasPlayers = false,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenAfterItHasBeenSeen() {
        assertFalse(
            shouldShowGamePlayDensityDiscovery(
                hasSeenDiscovery = true,
                hasPlayers = true,
            ),
        )
    }

    @Test
    fun discoveryIsVisibleForFirstUsableGame() {
        assertTrue(
            shouldShowGamePlayDensityDiscovery(
                hasSeenDiscovery = false,
                hasPlayers = true,
            ),
        )
    }
}
