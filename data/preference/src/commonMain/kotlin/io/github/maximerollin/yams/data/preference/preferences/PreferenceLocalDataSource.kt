package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.data.preference.GamePlayUiDensity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Instant

internal interface PreferenceLocalDataSource {
    fun getLastInAppReviewShownDate(): Flow<Instant?>
    suspend fun setLastInAppReviewShownDate(date: Instant)
    fun getIsInAppReviewPending(): Flow<Boolean>
    suspend fun setIsInAppReviewPending(isPending: Boolean)
    fun getIsUserOrderRandomized(): Flow<Boolean>
    suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean)
    fun getGameSettings(): Flow<GameSettings?>
    suspend fun setGameSettings(settings: GameSettings)
    fun getIsHapticFeedbackEnabled(): Flow<Boolean>
    suspend fun setIsHapticFeedbackEnabled(isEnabled: Boolean)
    fun getGamePlayUiDensity(): Flow<GamePlayUiDensity>
    suspend fun setGamePlayUiDensity(density: GamePlayUiDensity)
    fun getHasSeenGamePlayDensityDiscovery(): Flow<Boolean>
    suspend fun setHasSeenGamePlayDensityDiscovery()
    fun getYamsPlusStatus(): Flow<Boolean>
    suspend fun setYamsPlusStatus(isSubscribed: Boolean)
}

internal class PreferencePreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) : PreferenceLocalDataSource {
    override fun getLastInAppReviewShownDate(): Flow<Instant?> {
        return dataStore.data.map { preferences ->
            preferences[LAST_IN_APP_REVIEW_SHOWN_DATE_KEY]?.let { Instant.fromEpochMilliseconds(it) }
        }
    }

    override suspend fun setLastInAppReviewShownDate(date: Instant) {
        dataStore.edit { preferences ->
            preferences[LAST_IN_APP_REVIEW_SHOWN_DATE_KEY] = date.toEpochMilliseconds()
        }
    }

    override fun getIsInAppReviewPending(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_IN_APP_REVIEW_PENDING_KEY] ?: false
        }

    override suspend fun setIsInAppReviewPending(isPending: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_IN_APP_REVIEW_PENDING_KEY] = isPending
        }
    }

    override fun getIsUserOrderRandomized(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_USER_ORDER_RANDOMIZED_KEY] ?: true
        }

    override suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_ORDER_RANDOMIZED_KEY] = isUserOrderRandomized
        }
    }

    override fun getGameSettings(): Flow<GameSettings?> {
        return dataStore.data.map { preferences ->
            preferences[GAME_SETTINGS_KEY]?.let {
                Json.decodeFromString(it)
            }
        }
    }

    override suspend fun setGameSettings(settings: GameSettings) {
        dataStore.edit { preferences ->
            preferences[GAME_SETTINGS_KEY] = Json.encodeToString(settings)
        }
    }

    override fun getIsHapticFeedbackEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_HAPTIC_FEEDBACK_ENABLED_KEY] ?: true
        }

    override suspend fun setIsHapticFeedbackEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_HAPTIC_FEEDBACK_ENABLED_KEY] = isEnabled
        }
    }

    override fun getGamePlayUiDensity(): Flow<GamePlayUiDensity> =
        dataStore.data.map { preferences ->
            preferences[GAME_PLAY_UI_DENSITY_KEY]
                ?.let { value ->
                    runCatching { GamePlayUiDensity.valueOf(value) }.getOrNull()
                }
                ?: GamePlayUiDensity.NORMAL
        }

    override suspend fun setGamePlayUiDensity(density: GamePlayUiDensity) {
        dataStore.edit { preferences ->
            preferences[GAME_PLAY_UI_DENSITY_KEY] = density.name
        }
    }

    override fun getHasSeenGamePlayDensityDiscovery(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAS_SEEN_GAME_PLAY_DENSITY_DISCOVERY_KEY] ?: false
        }

    override suspend fun setHasSeenGamePlayDensityDiscovery() {
        dataStore.edit { preferences ->
            preferences[HAS_SEEN_GAME_PLAY_DENSITY_DISCOVERY_KEY] = true
        }
    }

    override fun getYamsPlusStatus(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[YAMS_PLUS_SUBSCRIPTION_STATUS_KEY] ?: false
        }

    override suspend fun setYamsPlusStatus(isSubscribed: Boolean) {
        dataStore.edit { preferences ->
            preferences[YAMS_PLUS_SUBSCRIPTION_STATUS_KEY] = isSubscribed
        }
    }

    private companion object {
        val LAST_IN_APP_REVIEW_SHOWN_DATE_KEY = longPreferencesKey("last_in_app_review_shown_date")
        val IS_IN_APP_REVIEW_PENDING_KEY = booleanPreferencesKey("is_in_app_review_pending")
        val GAME_SETTINGS_KEY = stringPreferencesKey("game_settings")
        val IS_USER_ORDER_RANDOMIZED_KEY = booleanPreferencesKey("is_user_order_randomized")
        val IS_HAPTIC_FEEDBACK_ENABLED_KEY = booleanPreferencesKey("is_haptic_feedback_enabled")
        val GAME_PLAY_UI_DENSITY_KEY = stringPreferencesKey("game_play_ui_density")
        val HAS_SEEN_GAME_PLAY_DENSITY_DISCOVERY_KEY =
            booleanPreferencesKey("has_seen_game_play_density_discovery")
        val YAMS_PLUS_SUBSCRIPTION_STATUS_KEY = booleanPreferencesKey("yams_plus_subscription_status")
    }

}
