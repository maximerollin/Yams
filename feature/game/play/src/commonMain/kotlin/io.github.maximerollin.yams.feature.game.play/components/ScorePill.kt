package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.feature.game.play.ScoreSelectionOption

@Composable
internal fun ScorePill(
    value: String,
    suffix: String?,
    emphasize: Boolean = false,
    onClick: (() -> Unit)? = null,
    isMenuExpanded: Boolean = false,
    menuOptions: List<ScoreSelectionOption> = emptyList(),
    onDismissMenu: () -> Unit = {},
    onMenuItemClick: (ScoreSelectionOption) -> Unit = {},
    fixedWidth: Dp? = null,
    isCompactUi: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val horizontalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 10.dp else 12.dp,
        label = "scorePillHorizontalPadding",
    )
    val verticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 7.dp else 10.dp,
        label = "scorePillVerticalPadding",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = when {
                emphasize -> YamsTheme.colors.gold.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.background
            },
            border = BorderStroke(
                width = 1.dp,
                color = when {
                    emphasize -> YamsTheme.colors.gold.copy(alpha = 0.35f)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                }
            ),
            modifier = (
                if (fixedWidth != null) {
                    Modifier.width(fixedWidth)
                } else {
                    Modifier.widthIn(min = 68.dp)
                }
            )
                .heightIn(min = if (isCompactUi) 36.dp else Dp.Unspecified)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                ),
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = when {
                        emphasize -> YamsTheme.colors.brown
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
                if (suffix != null) {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            emphasize -> YamsTheme.colors.brown
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }

        ScoreSelectionMenu(
            expanded = isMenuExpanded,
            options = menuOptions,
            onDismissRequest = onDismissMenu,
            onScoreSelected = onMenuItemClick,
        )
    }
}
