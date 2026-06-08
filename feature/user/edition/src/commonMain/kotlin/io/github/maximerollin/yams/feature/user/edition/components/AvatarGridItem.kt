package io.github.maximerollin.yams.feature.user.edition.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.Check
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yams.feature.user.edition.generated.resources.*

@Composable
public fun AvatarGridItem(
    drawable: DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 0.5f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .scale(scale)
            .alpha(alpha)
            .semantics {
                role = Role.RadioButton
                selected = isSelected
            }
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = YamsTheme.colors.gold,
                        shape = CircleShape
                    )
                } else {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                }
            )
            .padding(if (isSelected) 3.dp else 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = if (isSelected) {
                stringResource(Res.string.edition_selected_avatar_cd)
            } else {
                stringResource(Res.string.edition_select_avatar_cd)
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(if (isSelected) 58.dp else 60.dp)
                .clip(CircleShape)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = YamsTheme.colors.gold
                ) {
                    Icon(
                        imageVector = YamsIcons.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}
