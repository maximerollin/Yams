package io.github.maximerollin.yams.core.analytics

import android.content.Context
import com.logsnag.kotlin.LogSnag
import java.util.UUID

/**
 * Configures and holds the LogSnag client used to mirror analytics events. The
 * client is only created when both a token and a project are supplied; otherwise it
 * stays unavailable and [mirror] becomes a no-op.
 */
public object LogSnagAnalyticsInitializer {
    private var client: LogSnag? = null
    private var userId: String? = null

    public fun setup(
        context: Context,
        token: String,
        project: String,
    ) {
        if (token.isBlank() || project.isBlank() || client != null) return

        userId = context.applicationContext.logSnagUserId()
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
                userId = userId,
                tags = properties.toLogSnagTags(),
                notify = false,
            )
        }

        // Bump the matching insight counter (games created / completed / abandoned).
        logSnagInsightTitle(event, releaseChannel)?.let { title ->
            runCatching { logSnag.insightIncrement(title = title, value = 1) }
        }
    }

    private fun Context.logSnagUserId(): String {
        val preferences = getSharedPreferences(
            LOGSNAG_PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        preferences.getString(LOGSNAG_USER_ID_KEY, null)
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?.let { return it }

        val generatedUserId = "$ANDROID_USER_ID_PREFIX${UUID.randomUUID()}"
        preferences.edit()
            .putString(LOGSNAG_USER_ID_KEY, generatedUserId)
            .apply()
        return generatedUserId
    }

    private const val LOGSNAG_PREFERENCES_NAME = "yams_logsnag"
    private const val LOGSNAG_USER_ID_KEY = "logsnag_user_id"
    private const val ANDROID_USER_ID_PREFIX = "android-"
}
