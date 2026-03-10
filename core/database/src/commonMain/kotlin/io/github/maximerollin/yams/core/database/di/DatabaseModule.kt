package io.github.maximerollin.yams.core.database.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.maximerollin.yams.core.database.AppDatabase
import io.github.maximerollin.yams.core.database.AppDatabaseBuilderFactory
import io.github.maximerollin.yams.core.database.DefaultTransactionRunner
import io.github.maximerollin.yams.core.database.GameLocalDataSource
import io.github.maximerollin.yams.core.database.GamePlayStateLocalDataSource
import io.github.maximerollin.yams.core.database.GameResultLocalDataSource
import io.github.maximerollin.yams.core.database.PlayerLocalDataSource
import io.github.maximerollin.yams.core.database.PlayerResultLocalDataSource
import io.github.maximerollin.yams.core.database.RoomGameLocalDataSource
import io.github.maximerollin.yams.core.database.RoomGamePlayStateLocalDataSource
import io.github.maximerollin.yams.core.database.RoomGameResultLocalDataSource
import io.github.maximerollin.yams.core.database.RoomPlayerLocalDataSource
import io.github.maximerollin.yams.core.database.RoomPlayerResultLocalDataSource
import io.github.maximerollin.yams.core.database.RoomScoreEntryLocalDataSource
import io.github.maximerollin.yams.core.database.RoomUserLocalDataSource
import io.github.maximerollin.yams.core.database.ScoreEntryLocalDataSource
import io.github.maximerollin.yams.core.database.TransactionRunner
import io.github.maximerollin.yams.core.database.UserLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

public val databaseModule: Module = module {
    includes(databaseNativeModule)
    single {
        get<AppDatabaseBuilderFactory>()
            .createDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    // Dao
    factory { get<AppDatabase>().userDao() }
    factory { get<AppDatabase>().gameDao() }
    factory { get<AppDatabase>().gameResultDao() }
    factory { get<AppDatabase>().playerDao() }
    factory { get<AppDatabase>().playerResultDao() }
    factory { get<AppDatabase>().scoreEntryDao() }

    // DataSources
    factory<UserLocalDataSource> { RoomUserLocalDataSource(get()) }
    factory<GameLocalDataSource> { RoomGameLocalDataSource(get()) }
    factory<GameResultLocalDataSource> { RoomGameResultLocalDataSource(get()) }
    factory<PlayerLocalDataSource> { RoomPlayerLocalDataSource(get()) }
    factory<PlayerResultLocalDataSource> { RoomPlayerResultLocalDataSource(get()) }
    factory<ScoreEntryLocalDataSource> { RoomScoreEntryLocalDataSource(get()) }
    factory<GamePlayStateLocalDataSource> { RoomGamePlayStateLocalDataSource(get()) }


    // Transaction
    factory<TransactionRunner> { DefaultTransactionRunner(get()) }
}

internal expect val databaseNativeModule: Module
