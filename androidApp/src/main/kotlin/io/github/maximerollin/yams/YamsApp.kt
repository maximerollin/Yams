package io.github.maximerollin.yams

import android.app.Application
import android.content.pm.ApplicationInfo
import android.provider.Settings
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import io.github.maximerollin.yams.core.analytics.FirebaseAnalyticsInitializer
import io.github.maximerollin.yams.core.analytics.LogSnagAnalyticsInitializer
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

        val isFirebaseTestLab = isRunningInFirebaseTestLab()
        FirebaseAnalyticsInitializer.setup(
            context = this,
            analyticsCollectionEnabled = !isDebuggable && !isFirebaseTestLab,
        )
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!isDebuggable && !isFirebaseTestLab)

        // RevenueCat — only configured when an API key is provided in local.properties.
        YamsBuildConfig.REVENUECAT_PLAY_STORE_API_KEY.takeIf { it.isNotBlank() }?.let { apiKey ->
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(apiKey = apiKey)
        }

        // LogSnag - must be initialised before PostHog so that events fired during
        // PostHog setup (e.g. "application installed") are already mirrored correctly.
        LogSnagAnalyticsInitializer.setup(
            context = this,
            token = YamsBuildConfig.LOGSNAG_TOKEN,
            project = YamsBuildConfig.LOGSNAG_PROJECT,
        )

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

    private fun isRunningInFirebaseTestLab(): Boolean =
        Settings.System.getString(contentResolver, FIREBASE_TEST_LAB_SETTING) == "true"

    private val isDebuggable: Boolean
        get() = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private companion object {
        const val FIREBASE_TEST_LAB_SETTING = "firebase.test.lab"
    }
}
