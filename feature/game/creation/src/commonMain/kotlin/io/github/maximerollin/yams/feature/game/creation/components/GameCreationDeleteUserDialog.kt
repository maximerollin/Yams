package io.github.maximerollin.yams.feature.game.creation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.model.User
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.creation.generated.resources.*

@Composable
public fun GameCreationDeleteUserDialog(
    user: User,
    onDismissDialog: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = {
            Text(
                text = stringResource(Res.string.creation_delete_title, user.name)
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.creation_delete_message, user.name)
            )
        },
        icon = {
            Icon(
                imageVector = YamsIcons.Delete,
                contentDescription = stringResource(Res.string.creation_delete_title, user.name)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text(text = stringResource(Res.string.creation_cancel))
            }

        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(text = stringResource(Res.string.creation_delete))
            }
        }
    )
}
