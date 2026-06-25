package io.github.maximerollin.yams.core.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class LogSnagMirrorTest {
    @Test
    fun closedChannelPrefixesEventWithTest() {
        assertEquals("Test game created", logSnagEventName("game created", "closed"))
    }

    @Test
    fun productionChannelKeepsRawEventName() {
        assertEquals("game created", logSnagEventName("game created", "production"))
    }

    @Test
    fun unsupportedChannelsDisableDelivery() {
        listOf("local", "internal", "unknown").forEach { channel ->
            assertNull(
                logSnagEventName("game created", channel),
                "expected LogSnag delivery to be disabled for channel '$channel'",
            )
        }
    }

    @Test
    fun eventNamesNeverReceiveAndroidPrefix() {
        assertFalse(logSnagEventName("game created", "production")!!.startsWith("android"))
        assertFalse(logSnagEventName("game created", "closed")!!.contains("android"))
    }

    @Test
    fun countedEventsMapToInsightTitlesOnProduction() {
        assertEquals("Games created", logSnagInsightTitle("game created", "production"))
        assertEquals("Games completed", logSnagInsightTitle("game completed", "production"))
        assertEquals("Games abandoned", logSnagInsightTitle("game abandoned", "production"))
    }

    @Test
    fun insightTitlesCarryTestPrefixOnClosedChannel() {
        assertEquals("Test Games created", logSnagInsightTitle("game created", "closed"))
    }

    @Test
    fun insightTitlesAreNullForUncountedEvents() {
        assertNull(logSnagInsightTitle("player created", "production"))
        assertNull(logSnagInsightTitle("paywall screen viewed", "production"))
    }

    @Test
    fun insightTitlesAreNullForUnsupportedChannels() {
        listOf("local", "internal", "unknown").forEach { channel ->
            assertNull(logSnagInsightTitle("game created", channel))
        }
    }

    @Test
    fun nullPropertiesAreRemovedAndValuesBecomeStringTags() {
        val tags = mapOf(
            "rule_set" to "yams",
            "player_count_bucket" to 3,
            "platform" to "android",
            "missing" to null,
        ).toLogSnagTags()

        assertEquals(
            mapOf(
                "rule-set" to "yams",
                "player-count-bucket" to "3",
                "platform" to "android",
            ),
            tags,
        )
    }

    @Test
    fun tagKeysAreNormalizedToLogSnagFormat() {
        val tags = mapOf(
            "release_channel" to "closed",
            "Average Score Per Turn Bucket" to "10-20",
            "app.build" to 42,
            "\$current_url" to "https://example.com",
            "___" to "dropped",
        ).toLogSnagTags()

        assertEquals(
            mapOf(
                "release-channel" to "closed",
                "average-score-per-turn-bucket" to "10-20",
                "app-build" to "42",
                "current-url" to "https://example.com",
            ),
            tags,
        )
    }
}
