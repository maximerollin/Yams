package io.github.maximerollin.yams.feature.game.creation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.model.User

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
                text = "Delete ${user.name}"
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete ${user.name}"
            )
        },
        icon = {
            Icon(
                imageVector = YamsIcons.Delete,
                contentDescription = "Supprimer ${user.name}"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissDialog) {
                Text(text = "Cancel")
            }

        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(text = "Delete")
            }
        }
    )
}