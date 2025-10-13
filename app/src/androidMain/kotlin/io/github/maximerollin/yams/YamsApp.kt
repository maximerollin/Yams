package io.github.maximerollin.yams

import android.app.Application
import io.github.maximerollin.yams.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class YamsApp : Application(), KoinStartup {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onKoinStartup() = KoinConfiguration {
        androidContext(this@YamsApp)
        modules(appModule)
    }
}
