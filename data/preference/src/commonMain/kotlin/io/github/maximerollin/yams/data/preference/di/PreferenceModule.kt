package io.github.maximerollin.yams.data.preference.di

import io.github.maximerollin.yams.data.preference.DefaultPreferenceRepository
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import io.github.maximerollin.yams.data.preference.preferences.PreferenceLocalDataSource
import io.github.maximerollin.yams.data.preference.preferences.PreferencePreferences
import io.github.maximerollin.yams.data.preference.preferences.PreferencePreferencesDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val dataPreferenceModule: Module = module {
    // Preference
    factory<PreferenceLocalDataSource> { PreferencePreferencesDataSource(PreferencePreferences.dataStore) }

    // Repository
    singleOf(::DefaultPreferenceRepository) bind PreferenceRepository::class
}