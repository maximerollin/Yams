package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferenceDataStorePathTest {
    @Test
    fun dataStorePathIsAnAbsoluteFileSystemPath() {
        val path = preferenceDataStorePath()

        assertTrue(path.isAbsolute)
        assertFalse(path.toString().startsWith("file:"))
    }

    @Test
    fun dataStoreCanWriteAndReadPreferences() = runBlocking {
        val key = booleanPreferencesKey("ios_path_test")

        PreferencePreferences.dataStore.edit { preferences ->
            preferences[key] = true
        }

        assertEquals(true, PreferencePreferences.dataStore.data.first()[key])
    }
}
