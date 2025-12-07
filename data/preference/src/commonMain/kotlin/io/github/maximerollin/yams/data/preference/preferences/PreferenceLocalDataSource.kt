package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

internal interface PreferenceLocalDataSource {
    fun getLastInAppReviewShownDate(): Flow<Instant?>
    suspend fun setLastInAppReviewShownDate(date: Instant)
    fun getIsUserOrderRandomized(): Flow<Boolean>
    suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean)
    // TODO add game settings
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

    override fun getIsUserOrderRandomized(): Flow<Boolean> =
         dataStore.data.map { preferences ->
             preferences[IS_USER_ORDER_RANDOMIZED_KEY] ?: true
    }

    override suspend fun setIsUserOrderRandomized(isUserOrderRandomized: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_USER_ORDER_RANDOMIZED_KEY] = isUserOrderRandomized
        }
    }

    private companion object {
        val LAST_IN_APP_REVIEW_SHOWN_DATE_KEY = longPreferencesKey("last_in_app_review_shown_date")
        val GAME_SETTINGS_KEY = stringPreferencesKey("game_settings")
        val IS_USER_ORDER_RANDOMIZED_KEY = booleanPreferencesKey("is_user_order_randomized")
    }

}




