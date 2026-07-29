package io.github.maximerollin.yams.core.analytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IosAnalyticsDispatcherTest {
    @Test
    fun trackerForwardsEquivalentPostHogAndLogSnagEvents() {
        var postHogEvent: String? = null
        var postHogProperties: Map<String, Any>? = null
        var firebaseEvent: String? = null
        var firebaseProperties: Map<String, String>? = null
        var logSnagEvent: String? = null
        var logSnagIcon: String? = null
        var logSnagTags: Map<String, String>? = null
        var logSnagInsight: String? = "not-null"

        IosAnalyticsDispatcher.configure(
            releaseChannel = "production",
            postHogCapture = { event, properties ->
                postHogEvent = event
                postHogProperties = properties
            },
            firebaseCapture = { event, properties ->
                firebaseEvent = event
                firebaseProperties = properties
            },
            logSnagCapture = { event, icon, tags, insight ->
                logSnagEvent = event
                logSnagIcon = icon
                logSnagTags = tags
                logSnagInsight = insight
            },
        )

        PlatformAnalyticsTracker().capture(
            event = "player created",
            properties = mapOf("avatar_source" to "preset", "missing" to null),
        )

        assertEquals("ios player created", postHogEvent)
        assertEquals(
            mapOf(
                "avatar_source" to "preset",
                "platform" to "ios",
                "release_channel" to "production",
            ),
            postHogProperties,
        )
        assertEquals("player_created", firebaseEvent)
        assertEquals(
            mapOf(
                "avatar_source" to "preset",
                "platform" to "ios",
                "release_channel" to "production",
            ),
            firebaseProperties,
        )
        assertEquals("player created", logSnagEvent)
        assertEquals("👤", logSnagIcon)
        assertEquals(
            mapOf(
                "avatar-source" to "preset",
                "platform" to "ios",
                "release-channel" to "production",
            ),
            logSnagTags,
        )
        assertNull(logSnagInsight)
    }

    @Test
    fun applicationInstalledUsesIosEventMapping() {
        var capturedEvent: String? = null
        var capturedProperties: Map<String, Any>? = null

        IosAnalyticsDispatcher.configure(
            releaseChannel = "closed",
            postHogCapture = { event, properties ->
                capturedEvent = event
                capturedProperties = properties
            },
            firebaseCapture = { _, _ -> },
            logSnagCapture = { _, _, _, _ -> },
        )

        IosAnalyticsDispatcher.captureApplicationInstalled(
            appVersion = "1.0",
            appBuild = "1",
        )

        assertEquals("ios application installed", capturedEvent)
        assertEquals("ios", capturedProperties?.get("platform"))
        assertEquals("closed", capturedProperties?.get("release_channel"))
        assertEquals("1.0", capturedProperties?.get("app_version"))
        assertEquals("1", capturedProperties?.get("app_build"))
    }
}
