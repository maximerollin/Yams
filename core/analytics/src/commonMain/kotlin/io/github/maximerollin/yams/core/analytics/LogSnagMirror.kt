package io.github.maximerollin.yams.core.analytics

/** Channel every mirrored LogSnag event is published to. */
internal const val LOGSNAG_CHANNEL: String = "analytics"

/**
 * Returns the LogSnag event name for the given raw (un-prefixed) [event] on the
 * supplied [releaseChannel], or `null` when LogSnag delivery is disabled for that
 * channel.
 *
 * The raw event never receives the `android` platform prefix that PostHog uses.
 */
internal fun logSnagEventName(event: String, releaseChannel: String): String? =
    when (releaseChannel) {
        CLOSED_RELEASE_CHANNEL -> "$CLOSED_EVENT_PREFIX$event"
        PRODUCTION_RELEASE_CHANNEL -> event
        else -> null
    }

/** Returns the single emoji used to identify [event] in LogSnag. */
internal fun logSnagEventIcon(event: String): String =
    LOGSNAG_EVENT_ICONS[event] ?: DEFAULT_LOGSNAG_EVENT_ICON

/**
 * Converts analytics properties to LogSnag string tags, dropping null values and
 * rendering each remaining value with its Kotlin string representation. Keys are
 * normalized to LogSnag's lowercase dash-separated tag format.
 */
internal fun Map<String, Any?>.toLogSnagTags(): Map<String, String> =
    mapNotNull { (key, value) ->
        val tagKey = key.toLogSnagTagKey()
        if (tagKey.isBlank()) {
            null
        } else {
            value?.let { tagKey to it.toString() }
        }
    }.toMap()

private fun String.toLogSnagTagKey(): String =
    lowercase()
        .replace(LOGSNAG_TAG_KEY_SEPARATOR_REGEX, "-")
        .trim('-')

/**
 * Returns the LogSnag insight title whose counter should be incremented for the
 * given raw [event] on the supplied [releaseChannel], or `null` when the event has
 * no associated counter or when delivery is disabled for that channel.
 *
 * The title carries the same `Test ` prefix as events on the `closed` channel so
 * alpha builds never increment the production counters.
 */
internal fun logSnagInsightTitle(event: String, releaseChannel: String): String? {
    val title = INSIGHT_TITLES_BY_EVENT[event] ?: return null
    return when (releaseChannel) {
        CLOSED_RELEASE_CHANNEL -> "$CLOSED_EVENT_PREFIX$title"
        PRODUCTION_RELEASE_CHANNEL -> title
        else -> null
    }
}

private const val CLOSED_RELEASE_CHANNEL = "closed"
private const val PRODUCTION_RELEASE_CHANNEL = "production"
private const val CLOSED_EVENT_PREFIX = "Test "
private val LOGSNAG_TAG_KEY_SEPARATOR_REGEX = Regex("[^a-z]+")

private val INSIGHT_TITLES_BY_EVENT: Map<String, String> = mapOf(
    "game created" to "Games created",
    "game completed" to "Games completed",
    "game abandoned" to "Games abandoned",
)

private const val DEFAULT_LOGSNAG_EVENT_ICON = "📌"

private val LOGSNAG_EVENT_ICONS: Map<String, String> = mapOf(
    "application installed" to "📲",
    "game created" to "🎲",
    "game completed" to "🏆",
    "game abandoned" to "🚪",
    "player created" to "👤",
    "player updated" to "✏️",
    "player deleted" to "🗑️",
    "review discovery acknowledged" to "👍",
    "review dialog opened" to "⭐",
    "review prompt shown" to "💬",
    "assistant ia clicked" to "🤖",
    "assistant ia scan started" to "📷",
    "dice assistant discovery acknowledged" to "🧠",
    "gameplay density discovery acknowledged" to "📐",
    "paywall screen viewed" to "👑",
    "purchase started" to "🛒",
    "purchase succeeded" to "🎉",
    "purchase failed" to "⚠️",
    "purchase restore started" to "🔄",
    "purchase restore completed" to "✅",
    "purchase restore failed" to "❌",
)
