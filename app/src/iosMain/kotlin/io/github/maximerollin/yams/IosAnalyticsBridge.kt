package io.github.maximerollin.yams

import io.github.maximerollin.yams.core.analytics.IosAnalyticsDispatcher

public object IosAnalyticsBridge {
    public fun configure(
        releaseChannel: String,
        postHogCapture: (String, Map<String, Any>) -> Unit,
        firebaseCapture: (String, Map<String, String>) -> Unit,
        logSnagCapture: (String, String, Map<String, String>, String?) -> Unit,
    ) {
        IosAnalyticsDispatcher.configure(
            releaseChannel = releaseChannel,
            postHogCapture = postHogCapture,
            firebaseCapture = firebaseCapture,
            logSnagCapture = logSnagCapture,
        )
    }

    public fun captureApplicationInstalled(
        appVersion: String?,
        appBuild: String?,
    ) {
        IosAnalyticsDispatcher.captureApplicationInstalled(
            appVersion = appVersion,
            appBuild = appBuild,
        )
    }
}
