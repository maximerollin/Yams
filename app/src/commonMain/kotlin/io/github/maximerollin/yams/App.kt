package io.github.maximerollin.yams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.navigation.AppNavHost
import io.github.maximerollin.yams.navigation.PredictiveBackGestureAnimations
import io.github.vinceglb.filekit.coil.addPlatformFileSupport

private val AppContentMaxWidth = 540.dp
private var isAppImageLoaderConfigured = false

@Composable
fun App(predictiveBackGestureAnimations: PredictiveBackGestureAnimations? = null) {
    if (!isAppImageLoaderConfigured) {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { addPlatformFileSupport() }
                .build()
        }
        isAppImageLoaderConfigured = true
    }

    YamsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter,
        ) {
            AppNavHost(
                predictiveBackGestureAnimations,
                modifier = Modifier
                    .widthIn(max = AppContentMaxWidth)
                    .fillMaxSize(),
            )
        }
    }
}
