package io.github.maximerollin.yams

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.maximerollin.yams.di.appModule
import io.github.vinceglb.filekit.FileKit
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

fun main() = application {
    FileKit.init("Yams")
    startKoin {
        modules(appModule)
    }

    val windowState = rememberWindowState(size = DpSize(width = 393.dp, height = 852.dp))

    Window(
        title = "Yams",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {
        window.apply {
            rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
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
}
