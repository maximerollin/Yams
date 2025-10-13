package io.github.maximerollin.yams.core.database.di

import io.github.maximerollin.yams.core.database.AppDatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual val databaseNativeModule: Module = module {
    factoryOf(::AppDatabaseBuilderFactory)
}

