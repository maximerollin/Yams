package io.github.maximerollin.yams.data.game.di

import io.github.maximerollin.yams.core.database.di.databaseModule
import io.github.maximerollin.yams.core.file.di.fileModule
import io.github.maximerollin.yams.data.game.DefaultGameRepository
import io.github.maximerollin.yams.data.game.GameRepository
import io.github.maximerollin.yams.data.game.DefaultScoreEntryRepository
import io.github.maximerollin.yams.data.game.ScoreEntryRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

public val dataGameModule: Module = module {
    includes(databaseModule, fileModule)
    factoryOf(::DefaultGameRepository) { bind<GameRepository>() }
    factoryOf(::DefaultScoreEntryRepository) { bind<ScoreEntryRepository>() }
}
