package io.github.maximerollin.yams.feature.user.edition.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.AddAPhoto
import io.github.maximerollin.yams.core.designsystem.icon.AddPhotoAlternate
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.stringResource
import yams.feature.user.edition.generated.resources.*

@Composable
internal actual fun CameraButton(
    onAvatarSelected: (Avatar) -> Unit,
    modifier: Modifier
) {
    if (LocalInspectionMode.current) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = YamsIcons.AddAPhoto,
                contentDescription = stringResource(Res.string.edition_camera_cd),
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(24.dp)
            )
        }
        return
    }

    val cameraLauncher = rememberCameraPickerLauncher {
        it?.let { photo -> onAvatarSelected(Avatar.File(photo)) }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { cameraLauncher.launch() }
    ) {
        Icon(
            imageVector = YamsIcons.AddAPhoto,
            contentDescription = stringResource(Res.string.edition_camera_cd),
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier.size(24.dp)
        )
    }
}

@Composable
internal actual fun GalleryButton(
    onAvatarSelected: (Avatar) -> Unit,
    modifier: Modifier
) {
    if (LocalInspectionMode.current) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = YamsIcons.AddPhotoAlternate,
                contentDescription = stringResource(Res.string.edition_gallery_cd),
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(24.dp)
            )
        }
        return
    }

    val galleryLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
        mode = FileKitMode.Single,
        onResult = { it?.let { photo -> onAvatarSelected(Avatar.File(photo)) } },
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
