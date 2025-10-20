package io.github.maximerollin.yams.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
public fun YamsTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalYamsColor provides YamsCustomColors) {
        MaterialTheme(
            colorScheme = YamsColorScheme,
            typography = InterTypography(),
            shapes = YamsShapes,
            content = content
        )
    }
}
