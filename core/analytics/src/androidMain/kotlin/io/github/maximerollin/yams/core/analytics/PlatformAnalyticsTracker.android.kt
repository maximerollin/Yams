package io.github.maximerollin.yams.core.analytics

import com.posthog.PostHog

internal actual class PlatformAnalyticsTracker actual constructor() : AnalyticsTracker {
    override fun capture(
        event: String,
        properties: Map<String, Any?>,
    ) {
        val enrichedProperties = properties
            .withAnalyticsPlatform(ANDROID_ANALYTICS_PLATFORM)
            .withAnalyticsReleaseChannel(AnalyticsRuntimeConfig.releaseChannel)

        runCatching {
            PostHog.capture(
                event = event.withAnalyticsPlatformPrefix(ANDROID_ANALYTICS_PLATFORM),
                properties = enrichedProperties.toPostHogProperties(),
            )
        }

        runCatching {
            FirebaseAnalyticsInitializer.capture(event = event, properties = enrichedProperties)
        }

        // Mirror the raw (un-prefixed) event and equivalent properties to LogSnag.
        LogSnagAnalyticsInitializer.mirror(event = event, properties = enrichedProperties)
    }

    private fun Map<String, Any?>.toPostHogProperties(): Map<String, Any> =
        mapNotNull { (key, value) ->
            value?.let { key to it }
        }.toMap()

    private companion object {
        private const val ANDROID_ANALYTICS_PLATFORM = "android"
    }
}
