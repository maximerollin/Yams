package io.github.maximerollin.yams

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import io.github.maximerollin.yams.di.appModule
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration
import platform.UIKit.UIViewController

@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController {
    configureRevenueCat()

    return ComposeUIViewController {
        KoinApplication(
            configuration = koinConfiguration {
                modules(appModule)
            },
        ) {
            val appViewModel = koinInject<AppViewModel>()
            var isAppInitialized by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                appViewModel.initializeApp()
                isAppInitialized = true
            }

            if (isAppInitialized) {
                App()
            }
        }
    }
}

private fun configureRevenueCat() {
    YamsBuildConfig.REVENUECAT_APP_STORE_API_KEY.takeIf { it.isNotBlank() }?.let { apiKey ->
        if (runCatching { Purchases.sharedInstance }.isSuccess) return

        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(apiKey = apiKey)
    }
}
