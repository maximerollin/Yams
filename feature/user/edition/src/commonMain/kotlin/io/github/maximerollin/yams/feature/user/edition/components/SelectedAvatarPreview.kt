package io.github.maximerollin.yams.feature.user.edition.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.icon.Check
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import org.jetbrains.compose.resources.painterResource

@Composable
public fun SelectedAvatarPreview(
    selectedAvatar: Avatar,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            YamsTheme.colors.gold.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedContent(
                    targetState = selectedAvatar,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) +
                                scaleIn(initialScale = 0.8f, animationSpec = tween(300)))
                            .togetherWith(
                                fadeOut(animationSpec = tween(200)) +
                                        scaleOut(targetScale = 0.8f, animationSpec = tween(200))
                            )
                    },
                    label = "avatar"
                ) { avatar ->
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(
                                width = 4.dp,
                                color = YamsTheme.colors.gold,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(112.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp
                        ) {
                            when (avatar) {
                                is Avatar.Drawable -> {
                                    Image(
                                        painter = painterResource(avatar.drawable),
                                        contentDescription = "Avatar sélectionné",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(112.dp)
                                    )
                                }

                                is Avatar.File -> {
                                    AsyncImage(
                                        model = avatar.file,
                                        contentDescription = "Avatar sélectionné",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(112.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = YamsIcons.Check,
                        contentDescription = null,
                        tint = YamsTheme.colors.gold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Votre avatar actuel",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}