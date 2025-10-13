package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.maximerollin.yams.core.designsystem.util.IconInfo

/**
 * Yams Bottom Navigation Bar component
 */
@Composable
public fun YamsBottomBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}

/**
 * Yams Bottom Navigation Bar Item
 */
@Composable
public fun RowScope.YamsBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    iconInfo: IconInfo,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            YamsIcon(
                iconInfo = iconInfo,
                tint = if (selected) selectedColor else unselectedColor
            )
        },
        label = {
            Text(
                text = label,
                color = if (selected) selectedColor else unselectedColor,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = modifier,
        enabled = enabled,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = selectedColor,
            selectedTextColor = selectedColor,
            unselectedIconColor = unselectedColor,
            unselectedTextColor = unselectedColor,
            indicatorColor = selectedColor.copy(alpha = 0.12f)
        )
    )
}
