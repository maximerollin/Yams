package io.github.maximerollin.yams.init

import android.content.Context
import androidx.startup.Initializer
import io.github.maximerollin.yams.AppViewModel
import kotlinx.coroutines.runBlocking
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
class AppInitializer : Initializer<Unit>, KoinComponent {
    private val appViewModel: AppViewModel by inject()

    override fun create(context: Context) {
        runBlocking {
            appViewModel.initializeApp()
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>?>?> = listOf(
        KoinInitializer::class.java,
    )
}