package io.github.maximerollin.yams.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
public fun YamsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else YamsLightColors
    val customColors = if (darkTheme) YamsDarkCustomColors else YamsLightCustomColors

    CompositionLocalProvider(LocalYamsColor provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = InterTypography(),
            shapes = YamsShapes,
            content = content
        )
    }
}
