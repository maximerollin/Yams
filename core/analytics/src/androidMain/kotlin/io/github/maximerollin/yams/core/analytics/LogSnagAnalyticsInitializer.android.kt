package io.github.maximerollin.yams.core.analytics

import com.logsnag.kotlin.LogSnag

/**
 * Configures and holds the LogSnag client used to mirror analytics events. The
 * client is only created when both a token and a project are supplied; otherwise it
 * stays unavailable and [mirror] becomes a no-op.
 */
public object LogSnagAnalyticsInitializer {
    private var client: LogSnag? = null

    public fun setup(
        token: String,
        project: String,
    ) {
        if (token.isBlank() || project.isBlank() || client != null) return

        client = LogSnag(token = token, project = project)
    }

    /**
     * Forwards a raw analytics [event] and its [properties] to LogSnag. Failures and
     * unconfigured/unsupported channels are swallowed so the mirror never affects
     * PostHog or feature behavior.
     */
    internal fun mirror(
        event: String,
        properties: Map<String, Any?>,
    ) {
        val logSnag = client ?: return
        val releaseChannel = AnalyticsRuntimeConfig.releaseChannel
        val logSnagEvent = logSnagEventName(event, releaseChannel) ?: return

        runCatching {
            logSnag.track(
                channel = LOGSNAG_CHANNEL,
                event = logSnagEvent,
                tags = properties.toLogSnagTags(),
                notify = false,
            )
        }

        // Bump the matching insight counter (games created / completed / abandoned).
        logSnagInsightTitle(event, releaseChannel)?.let { title ->
            runCatching { logSnag.insightIncrement(title = title, value = 1) }
        }
    }
}
