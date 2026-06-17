package io.github.maximerollin.yams.core.analytics

public interface AnalyticsTracker {
    public fun capture(
        event: String,
        properties: Map<String, Any?> = emptyMap(),
    )
}
