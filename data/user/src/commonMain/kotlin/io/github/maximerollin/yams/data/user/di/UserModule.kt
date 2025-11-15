package io.github.maximerollin.yams.data.user.di

import io.github.maximerollin.yams.core.database.di.databaseModule
import io.github.maximerollin.yams.core.file.di.fileModule
import io.github.maximerollin.yams.data.user.DefaultUserRepository
import io.github.maximerollin.yams.data.user.UserRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val dataUserModule: Module = module {
    includes(databaseModule, fileModule)
    singleOf(::DefaultUserRepository) bind UserRepository::class
}