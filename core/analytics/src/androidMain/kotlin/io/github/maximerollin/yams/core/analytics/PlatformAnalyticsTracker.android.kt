package io.github.maximerollin.yams.core.analytics

import com.posthog.PostHog

internal actual class PlatformAnalyticsTracker actual constructor() : AnalyticsTracker {
    override fun capture(
        event: String,
        properties: Map<String, Any?>,
    ) {
        runCatching {
            PostHog.capture(
                event = event.withAnalyticsPlatformPrefix(ANDROID_ANALYTICS_PLATFORM),
                properties = properties
                    .withAnalyticsPlatform(ANDROID_ANALYTICS_PLATFORM)
                    .withAnalyticsReleaseChannel(AnalyticsRuntimeConfig.releaseChannel)
                    .toPostHogProperties(),
            )
        }
    }

    private fun Map<String, Any?>.toPostHogProperties(): Map<String, Any> =
        mapNotNull { (key, value) ->
            value?.let { key to it }
        }.toMap()

    private companion object {
        private const val ANDROID_ANALYTICS_PLATFORM = "android"
    }
}
