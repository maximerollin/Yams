package io.github.maximerollin.yams.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

public object FirebaseAnalyticsInitializer {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    public fun setup(
        context: Context,
        analyticsCollectionEnabled: Boolean,
    ) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext).apply {
            setAnalyticsCollectionEnabled(analyticsCollectionEnabled)
        }
    }

    internal fun capture(
        event: String,
        properties: Map<String, Any?>,
    ) {
        firebaseAnalytics?.logEvent(event.toFirebaseEventName(), properties.toFirebaseBundle())
    }

    private fun Map<String, Any?>.toFirebaseBundle(): Bundle? {
        val parameters = mapNotNull { (key, value) ->
            value?.let { key to it.toString() }
        }
        if (parameters.isEmpty()) return null

        return Bundle(parameters.size).apply {
            parameters.forEach { (key, value) ->
                putString(key, value)
            }
        }
    }

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

    private const val FALLBACK_FIREBASE_EVENT_NAME = "app_event"
    private const val FIREBASE_EVENT_NAME_MAX_LENGTH = 40
    private val FIREBASE_EVENT_NAME_SEPARATOR_REGEX = Regex("[^a-z0-9_]+")
}
