package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path

internal object PreferencePreferences {
    val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(produceFile = {
            preferenceDataStorePath()
        })

    internal const val DATASTORE_FILENAME = "yams-preference.preferences_pb"
}

internal expect fun preferenceDataStorePath(): Path
