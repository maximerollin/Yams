package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo

/**
 * Yams Chip component
 */
@Composable
public fun YamsChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: IconInfo? = null,
    trailingIcon: IconInfo? = null,
    selectedColor: Color = YamsTheme.colors.gold,
    unselectedColor: Color = MaterialTheme.colorScheme.outline
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let { icon ->
                    YamsIcon(
                        iconInfo = icon,
                        modifier = Modifier.size(16.dp),
                        tint = if (selected) YamsTheme.colors.onGold else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                    )
                )
                
                trailingIcon?.let { icon ->
                    YamsIcon(
                        iconInfo = icon,
                        modifier = Modifier.size(16.dp),
                        tint = if (selected) YamsTheme.colors.onGold else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedColor,
            selectedLabelColor = YamsTheme.colors.onGold,
            selectedLeadingIconColor = YamsTheme.colors.onGold,
            selectedTrailingIconColor = YamsTheme.colors.onGold,
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onSurface
        ),
        border = if (!selected) {
            BorderStroke(1.dp, unselectedColor)
        } else null
    )
}
