package io.github.maximerollin.yams.di

import io.github.maximerollin.yams.AppViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(named("IO")) { Dispatchers.IO }
    single(named("Default")) { Dispatchers.Default }
    single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named("Default"))) }
    singleOf(::AppViewModel)
}
