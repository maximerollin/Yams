package io.github.maximerollin.yams.core.analytics

internal actual class PlatformAnalyticsTracker actual constructor() : AnalyticsTracker {
    override fun capture(
        event: String,
        properties: Map<String, Any?>,
    ) {
        val enrichedProperties = properties
            .withAnalyticsPlatform(IOS_ANALYTICS_PLATFORM)
            .withAnalyticsReleaseChannel(AnalyticsRuntimeConfig.releaseChannel)

        runCatching {
            IosAnalyticsDispatcher.capturePostHog(
                event = event.withAnalyticsPlatformPrefix(IOS_ANALYTICS_PLATFORM),
                properties = enrichedProperties.withoutNullValues(),
            )
        }

        runCatching {
            IosAnalyticsDispatcher.mirrorLogSnag(
                event = event,
                properties = enrichedProperties,
            )
        }
    }

    private fun Map<String, Any?>.withoutNullValues(): Map<String, Any> =
        mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

    private companion object {
        private const val IOS_ANALYTICS_PLATFORM = "ios"
    }
}
