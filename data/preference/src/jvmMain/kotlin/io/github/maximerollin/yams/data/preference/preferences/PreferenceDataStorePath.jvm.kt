package io.github.maximerollin.yams.data.preference.preferences

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.resolve
import okio.Path
import okio.Path.Companion.toPath

internal actual fun preferenceDataStorePath(): Path = FileKit
    .databasesDir
    .resolve(PreferencePreferences.DATASTORE_FILENAME)
    .absolutePath()
    .toPath()
