package io.github.maximerollin.yams

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import io.github.maximerollin.yams.di.appModule
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
    LaunchedEffect(Unit) {
        startKoin {
            modules(appModule)
        }
    }

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
