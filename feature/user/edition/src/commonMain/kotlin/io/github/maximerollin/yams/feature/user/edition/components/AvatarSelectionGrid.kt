package io.github.maximerollin.yams.feature.user.edition.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.AddPhotoAlternate
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.ui.utils.AppAvatars
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import yams.feature.user.edition.generated.resources.*

@Composable
public fun AvatarSelectionGrid(
    selectedAvatar: Avatar,
    onAvatarSelected: (Avatar) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val visibleAvatarCount = 4
    val hasMore = AppAvatars.size > visibleAvatarCount

    val galleryLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single,
        onResult = { photo -> photo?.let { onAvatarSelected(Avatar.File(it)) } }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.edition_choose_avatar),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CameraButton(
                    onPhotoSelected = { onAvatarSelected(Avatar.File(it)) },
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { galleryLauncher.launch() }
                ) {
                    Icon(
                        imageVector = YamsIcons.AddPhotoAlternate,
                        contentDescription = stringResource(Res.string.edition_gallery_cd),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = modifier.size(24.dp)
                    )
                }
            }
        }

        Text(
            text = stringResource(Res.string.edition_avatar_help),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppAvatars.take(visibleAvatarCount).forEach { avatarDrawable ->
                AvatarGridItem(
                    drawable = avatarDrawable,
                    isSelected = selectedAvatar is Avatar.Drawable &&
                            selectedAvatar.drawable == avatarDrawable,
                    onClick = { onAvatarSelected(Avatar.Drawable(avatarDrawable)) },
                )
            }

            if (hasMore) {
                AvatarExpandButton(
                    isExpanded = isExpanded,
                    remainingCount = AppAvatars.size - visibleAvatarCount,
                    onClick = { isExpanded = !isExpanded }
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 300f
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 300f
                )
            ) + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppAvatars.drop(visibleAvatarCount).forEach { avatarDrawable ->
                        AvatarGridItem(
                            drawable = avatarDrawable,
                            isSelected = selectedAvatar is Avatar.Drawable &&
                                    selectedAvatar.drawable == avatarDrawable,
                            onClick = {
                                onAvatarSelected(Avatar.Drawable(avatarDrawable))
                                isExpanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}
