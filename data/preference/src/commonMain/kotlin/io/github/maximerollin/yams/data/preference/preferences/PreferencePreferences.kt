package io.github.maximerollin.yams.data.preference.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.resolve
import okio.Path.Companion.toPath

internal object PreferencePreferences {
    val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(produceFile = {
            FileKit
                .databasesDir
                .resolve(DATASTORE_FILENAME)
                .absolutePath()
                .toPath()
        })
    private const val DATASTORE_FILENAME = "yams-preference.preferences_pb"
}