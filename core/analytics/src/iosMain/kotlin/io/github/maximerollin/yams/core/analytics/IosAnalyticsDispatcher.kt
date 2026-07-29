package io.github.maximerollin.yams.core.analytics

public object IosAnalyticsDispatcher {
    private var postHogCapture: ((String, Map<String, Any>) -> Unit)? = null
    private var firebaseCapture: ((String, Map<String, String>) -> Unit)? = null
    private var logSnagCapture: ((String, String, Map<String, String>, String?) -> Unit)? = null

    public fun configure(
        releaseChannel: String,
        postHogCapture: (String, Map<String, Any>) -> Unit,
        firebaseCapture: (String, Map<String, String>) -> Unit,
        logSnagCapture: (String, String, Map<String, String>, String?) -> Unit,
    ) {
        AnalyticsRuntimeConfig.releaseChannel = releaseChannel.trim().ifBlank { DEFAULT_RELEASE_CHANNEL }
        this.postHogCapture = postHogCapture
        this.firebaseCapture = firebaseCapture
        this.logSnagCapture = logSnagCapture
    }

    public fun captureApplicationInstalled(
        appVersion: String?,
        appBuild: String?,
    ) {
        PlatformAnalyticsTracker().capture(
            event = "application installed",
            properties = mapOf(
                "app_version" to appVersion,
                "app_build" to appBuild,
            ),
        )
    }

    internal fun capturePostHog(
        event: String,
        properties: Map<String, Any>,
    ) {
        postHogCapture?.invoke(event, properties)
    }

    internal fun captureFirebase(
        event: String,
        properties: Map<String, String>,
    ) {
        firebaseCapture?.invoke(event, properties)
    }

    internal fun mirrorLogSnag(
        event: String,
        properties: Map<String, Any?>,
    ) {
        val mirroredEvent = logSnagEventName(event, AnalyticsRuntimeConfig.releaseChannel) ?: return
        logSnagCapture?.invoke(
            mirroredEvent,
            logSnagEventIcon(event),
            properties.toLogSnagTags(),
            logSnagInsightTitle(event, AnalyticsRuntimeConfig.releaseChannel),
        )
    }

    private const val DEFAULT_RELEASE_CHANNEL = "local"
}
