package io.github.maximerollin.yams.feature.user.edition.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.ui.utils.AppAvatars
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import io.github.vinceglb.filekit.PlatformFile

@Composable
internal fun UserEditionAvatar(
    selectedAvatar: Avatar,
    onAvatarSelected: (Avatar) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Selected avatar preview card
        SelectedAvatarPreview(
            selectedAvatar = selectedAvatar
        )

        // Avatar selection grid with expandable list
        AvatarSelectionGrid(
            selectedAvatar = selectedAvatar,
            onAvatarSelected = onAvatarSelected
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun UserEditionAvatarPreview() {
    YamsTheme {
        UserEditionAvatar(
            selectedAvatar = Avatar.Drawable(AppAvatars[0]),
            onAvatarSelected = {}
        )
    }
}

@Composable
internal expect fun CameraButton(
    onPhotoSelected: (PlatformFile) -> Unit,
    modifier: Modifier = Modifier
)