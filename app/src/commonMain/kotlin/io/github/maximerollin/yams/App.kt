package io.github.maximerollin.yams

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.navigation.AppNavHost
import io.github.maximerollin.yams.navigation.PredictiveBackGestureAnimations
import io.github.vinceglb.filekit.coil.addPlatformFileSupport

@Composable
fun App(predictiveBackGestureAnimations: PredictiveBackGestureAnimations? = null) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { addPlatformFileSupport()}
            .build()
    }

    YamsTheme {
        AppNavHost(predictiveBackGestureAnimations)
    }
}
