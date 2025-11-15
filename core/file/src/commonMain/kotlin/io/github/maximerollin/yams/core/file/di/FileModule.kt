package io.github.maximerollin.yams.core.file.di

import io.github.maximerollin.yams.core.file.FileLocalDataSource
import io.github.maximerollin.yams.core.file.InternalFileLocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val fileModule: Module = module {
    singleOf(::InternalFileLocalDataSource) bind FileLocalDataSource::class
}
