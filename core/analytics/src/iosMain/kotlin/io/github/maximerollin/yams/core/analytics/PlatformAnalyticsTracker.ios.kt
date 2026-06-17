package io.github.maximerollin.yams.core.analytics

internal actual class PlatformAnalyticsTracker actual constructor() : AnalyticsTracker {
    override fun capture(
        event: String,
        properties: Map<String, Any?>,
    ) = Unit
}
