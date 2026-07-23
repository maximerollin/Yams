package io.github.maximerollin.yams.feature.game.play.components

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GamePlayDiceAssistantDiscoveryOverlayTest {
    @Test
    fun discoveryIsHiddenWhilePreferenceIsLoading() {
        assertFalse(
            shouldShowDiceAssistantDiscovery(
                hasSeenDiscovery = null,
                hasPlayers = true,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenWhenGameHasNoPlayers() {
        assertFalse(
            shouldShowDiceAssistantDiscovery(
                hasSeenDiscovery = false,
                hasPlayers = false,
            ),
        )
    }

    @Test
    fun discoveryIsHiddenAfterItHasBeenSeen() {
        assertFalse(
            shouldShowDiceAssistantDiscovery(
                hasSeenDiscovery = true,
                hasPlayers = true,
            ),
        )
    }

    @Test
    fun discoveryIsVisibleForFirstUsableGame() {
        assertTrue(
            shouldShowDiceAssistantDiscovery(
                hasSeenDiscovery = false,
                hasPlayers = true,
            ),
        )
    }
}
