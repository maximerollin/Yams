package io.github.maximerollin.yams.core.database.converter

import androidx.room.TypeConverter
import io.github.maximerollin.yams.core.database.entity.CustomGameSettingsEntity
import io.github.maximerollin.yams.core.database.entity.SettingsScoringEntity

internal class CustomGameSettingsConverter {
    @TypeConverter
    fun fromCustomGameSettings(value: List<CustomGameSettingsEntity>?): String? {
        value ?: return null

        return buildString {
            append(value.size)
            append(COUNT_SEPARATOR)

            value.forEach { setting ->
                appendSegment(setting.title)
                appendSegment(setting.scoring.name)
                appendSegment(setting.value?.toString())
                appendSegment(setting.description)
                appendSegment(if (setting.isEnabled) TRUE_TOKEN else FALSE_TOKEN)
            }
        }
    }

    @TypeConverter
    fun toCustomGameSettings(value: String?): List<CustomGameSettingsEntity>? {
        value ?: return null

        return runCatching {
            val reader = SegmentReader(value)
            val count = reader.readCount()

            buildList(count) {
                repeat(count) {
                    val title = requireNotNull(reader.readSegment())
                    val scoringName = requireNotNull(reader.readSegment())
                    val rawValue = reader.readSegment()
                    val description = reader.readSegment()
                    val enabledToken = requireNotNull(reader.readSegment())

                    val scoring = SettingsScoringEntity.entries.firstOrNull { it.name == scoringName }
                        ?: error("Unknown scoring value: $scoringName")

                    val parsedValue = rawValue?.toIntOrNull()
                    if (rawValue != null && parsedValue == null) {
                        error("Invalid numeric value: $rawValue")
                    }

                    val isEnabled = when (enabledToken) {
                        TRUE_TOKEN -> true
                        FALSE_TOKEN -> false
                        else -> error("Invalid enabled value: $enabledToken")
                    }

                    add(
                        CustomGameSettingsEntity(
                            title = title,
                            scoring = scoring,
                            value = parsedValue,
                            description = description,
                            isEnabled = isEnabled,
                        )
                    )
                }
            }.also {
                reader.requireFullyConsumed()
            }
        }.getOrDefault(emptyList())
    }

    private fun StringBuilder.appendSegment(value: String?) {
        if (value == null) {
            append(NULL_LENGTH)
            append(SEGMENT_SEPARATOR)
            return
        }

        append(value.length)
        append(SEGMENT_SEPARATOR)
        append(value)
    }

    private class SegmentReader(private val raw: String) {
        private var cursor: Int = 0

        fun readCount(): Int {
            val delimiterIndex = raw.indexOf(COUNT_SEPARATOR)
            check(delimiterIndex >= 0) { "Invalid payload: missing count separator" }
            val count = raw.substring(0, delimiterIndex).toInt()
            check(count >= 0) { "Invalid payload: negative item count" }
            cursor = delimiterIndex + 1
            return count
        }

        fun readSegment(): String? {
            val delimiterIndex = raw.indexOf(SEGMENT_SEPARATOR, startIndex = cursor)
            check(delimiterIndex >= 0) { "Invalid payload: missing segment separator" }

            val lengthToken = raw.substring(cursor, delimiterIndex)
            val length = lengthToken.toInt()
            cursor = delimiterIndex + 1

            if (length == NULL_LENGTH) {
                return null
            }

            check(length >= 0) { "Invalid payload: negative segment length" }

            val endIndex = cursor + length
            check(endIndex <= raw.length) { "Invalid payload: segment overflow" }

            return raw.substring(cursor, endIndex).also {
                cursor = endIndex
            }
        }

        fun requireFullyConsumed() {
            check(cursor == raw.length) { "Invalid payload: trailing data" }
        }
    }

    private companion object {
        const val COUNT_SEPARATOR: Char = '|'
        const val SEGMENT_SEPARATOR: Char = ':'
        const val NULL_LENGTH: Int = -1
        const val TRUE_TOKEN: String = "1"
        const val FALSE_TOKEN: String = "0"
    }
}
