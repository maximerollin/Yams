@file:Suppress("unused")

package io.github.maximerollin.yams

import io.github.maximerollin.yams.di.appModule
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
  
object AppInitializer {
    fun initialize(onKoinStart: KoinApplication.() -> Unit) {
        startKoin {
            onKoinStart()
            modules(appModule)
        }
    }
}


class StartDestinationInitializer : KoinComponent {
    private val appViewModel: AppViewModel by inject()

    fun initialize() {
        runBlocking {
            appViewModel.initializeApp()
        }
    }
}
