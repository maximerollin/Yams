package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.icon.Tactic
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme

@Composable
public fun UserOrderToggleButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(26.dp)

    val indicator by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.primary
            false -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    val surfaceColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            false -> Color.Transparent
        }
    )
    Surface(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = surfaceColor,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = indicator,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = indicator,
                fontSize = 12.sp,
            )
        }
    }
}

@Preview
@Composable
private fun UserOrderToggleButtonPreview() {
    YamsTheme {
        UserOrderToggleButton(
            label = "Aléatoire",
            icon = YamsIcons.Tactic,
            selected = true,
            onClick = {},
        )
    }
}