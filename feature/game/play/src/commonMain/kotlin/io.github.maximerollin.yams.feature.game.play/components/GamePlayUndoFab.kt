package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.maximerollin.yams.core.designsystem.icon.Undo
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons

@Composable
internal fun GamePlayUndoFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(),
    ) {
        Icon(
            imageVector = YamsIcons.Undo,
            contentDescription = "Annuler le dernier coup",
        )
    }
}
