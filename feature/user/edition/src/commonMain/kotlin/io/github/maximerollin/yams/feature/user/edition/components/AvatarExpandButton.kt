package io.github.maximerollin.yams.feature.user.edition.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.AddReaction
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import org.jetbrains.compose.resources.stringResource
import yams.feature.user.edition.generated.resources.*

@Composable
public fun AvatarExpandButton(
    isExpanded: Boolean,
    remainingCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 400f
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(
                brush = if (isExpanded) {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    )
                },
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isExpanded) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter =
                        fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
                    initialContentExit =
                        fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.8f)
                )
            },
            label = "expandText"
        ) {
            Icon(
                modifier = modifier.padding(12.dp).fillMaxSize().align(Alignment.Center),
                imageVector = YamsIcons.AddReaction,
                contentDescription = stringResource(Res.string.edition_more_avatars_cd),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
