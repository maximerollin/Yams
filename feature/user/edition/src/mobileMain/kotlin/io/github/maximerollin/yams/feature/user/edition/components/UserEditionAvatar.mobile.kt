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
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.AddAPhoto
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher

@Composable
internal actual fun CameraButton(
    onPhotoSelected: (PlatformFile) -> Unit,
    modifier: Modifier
) {
    val cameraLauncher = rememberCameraPickerLauncher { it?.let(onPhotoSelected) }

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
            contentDescription = "Appareil photo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier.size(24.dp)
        )
    }
}
