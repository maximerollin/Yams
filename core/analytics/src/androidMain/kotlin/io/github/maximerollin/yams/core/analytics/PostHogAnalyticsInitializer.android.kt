package io.github.maximerollin.yams.core.analytics

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

public object PostHogAnalyticsInitializer {
    internal var isConfigured: Boolean = false
        private set

    public fun setup(
        context: Context,
        apiKey: String,
        host: String,
    ) {
        if (apiKey.isBlank() || isConfigured) return

        val config = PostHogAndroidConfig(
            apiKey = apiKey,
            host = host,
            captureApplicationLifecycleEvents = false,
            captureDeepLinks = false,
            captureScreenViews = false,
        )
        PostHogAndroid.setup(context, config)
        isConfigured = true
        trackApplicationInstalled(context)
    }

    private fun trackApplicationInstalled(context: Context) {
        val appContext = context.applicationContext
        val preferences = appContext.getSharedPreferences(
            ANALYTICS_PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        if (preferences.getBoolean(APPLICATION_INSTALLED_TRACKED_KEY, false)) return

        val packageInfo = runCatching {
            appContext.packageManager.getPackageInfoCompat(appContext.packageName)
        }.getOrNull()

        if (packageInfo?.isFirstInstall() == true) {
            PlatformAnalyticsTracker().capture(
                event = "application installed",
                properties = mapOf(
                    "app_version" to packageInfo.versionName,
                    "app_build" to packageInfo.versionCodeCompat(),
                ),
            )
        }

        preferences.edit()
            .putBoolean(APPLICATION_INSTALLED_TRACKED_KEY, true)
            .apply()
    }

    private fun PackageInfo.isFirstInstall(): Boolean =
        firstInstallTime == lastUpdateTime

    private fun PackageManager.getPackageInfoCompat(packageName: String): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            getPackageInfo(packageName, 0)
        }

    private fun PackageInfo.versionCodeCompat(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }

    private const val ANALYTICS_PREFERENCES_NAME = "yams_analytics"
    private const val APPLICATION_INSTALLED_TRACKED_KEY = "application_installed_tracked"
}
