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
            IosAnalyticsDispatcher.captureFirebase(
                event = event.toFirebaseEventName(),
                properties = enrichedProperties.toFirebaseStringProperties(),
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

    private fun Map<String, Any?>.toFirebaseStringProperties(): Map<String, String> =
        mapNotNull { (key, value) ->
            value?.let { key to it.toString() }
        }.toMap()

    private fun String.toFirebaseEventName(): String {
        val normalized = lowercase()
            .replace(FIREBASE_EVENT_NAME_SEPARATOR_REGEX, "_")
            .trim('_')
            .take(FIREBASE_EVENT_NAME_MAX_LENGTH)

        return when {
            normalized.isBlank() -> FALLBACK_FIREBASE_EVENT_NAME
            normalized.first().isLetter() -> normalized
            else -> "${FALLBACK_FIREBASE_EVENT_NAME}_$normalized"
                .take(FIREBASE_EVENT_NAME_MAX_LENGTH)
        }
    }

    private companion object {
        private const val IOS_ANALYTICS_PLATFORM = "ios"
        private const val FALLBACK_FIREBASE_EVENT_NAME = "app_event"
        private const val FIREBASE_EVENT_NAME_MAX_LENGTH = 40
        private val FIREBASE_EVENT_NAME_SEPARATOR_REGEX = Regex("[^a-z0-9_]+")
    }
}
