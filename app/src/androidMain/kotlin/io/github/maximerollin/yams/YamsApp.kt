package io.github.maximerollin.yams

import android.app.Application
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import io.github.maximerollin.yams.core.analytics.PostHogAnalyticsInitializer
import io.github.maximerollin.yams.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class YamsApp : Application(), KoinStartup {
    override fun onCreate() {
        super.onCreate()

        // RevenueCat — only configured when an API key is provided in local.properties.
        YamsBuildConfig.REVENUECAT_PLAY_STORE_API_KEY.takeIf { it.isNotBlank() }?.let { apiKey ->
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(apiKey = apiKey)
        }

        // PostHog - only configured when an API key is provided in local.properties.
        YamsBuildConfig.POSTHOG_API_KEY.takeIf { it.isNotBlank() }?.let { apiKey ->
            PostHogAnalyticsInitializer.setup(
                context = this,
                apiKey = apiKey,
                host = YamsBuildConfig.POSTHOG_HOST,
                releaseChannel = YamsBuildConfig.ANALYTICS_RELEASE_CHANNEL,
            )
        }
    }

    override fun onKoinStartup() = KoinConfiguration {
        androidContext(this@YamsApp)
        modules(appModule)
    }
}
