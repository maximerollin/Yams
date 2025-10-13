package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.util.IconInfo

/**
 * Yams IconButton component that uses IconInfo
 */
@Composable
public fun YamsIconButton(
    iconInfo: IconInfo?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = tint
        )
    ) {
        if (iconInfo == null) return@IconButton
        YamsIcon(
            iconInfo = iconInfo,
            tint = tint,
            size = iconSize
        )
    }
}
