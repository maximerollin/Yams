package io.github.maximerollin.yams.data.game.di

import io.github.maximerollin.yams.core.database.di.databaseModule
import io.github.maximerollin.yams.data.game.DefaultGameRepository
import io.github.maximerollin.yams.data.game.GameRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

public val dataGameModule: Module = module {
    includes(databaseModule)
    factoryOf(::DefaultGameRepository) { bind<GameRepository>() }
}

