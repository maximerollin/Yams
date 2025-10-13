package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.util.IconInfo

/**
 * Yams Icon component that uses IconInfo
 */
@Composable
public fun YamsIcon(
    iconInfo: IconInfo,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 24.dp
) {
    Icon(
        imageVector = iconInfo.vector,
        contentDescription = iconInfo.contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}
