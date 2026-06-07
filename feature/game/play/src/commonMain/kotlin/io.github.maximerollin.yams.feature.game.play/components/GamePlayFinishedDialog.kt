package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.icon.Check
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.Undo
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamePlayFinishedDialog(
    onGoToResults: () -> Unit,
    onUndoLastMove: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = {},
    ) {
        GamePlayFinishedDialogContent(
            onGoToResults = onGoToResults,
            onUndoLastMove = onUndoLastMove,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun GamePlayFinishedDialogContent(
    onGoToResults: () -> Unit,
    onUndoLastMove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 16.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Surface(
                modifier = Modifier.size(68.dp),
                shape = RoundedCornerShape(24.dp),
                color = YamsTheme.colors.gold.copy(alpha = 0.18f),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = YamsIcons.Trophy,
                        contentDescription = "Partie terminée",
                        tint = YamsTheme.colors.brown,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Partie terminée",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Toutes les cases sont remplies. Vérifie le dernier score avant d'ouvrir les résultats.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GamePlayFinishedDialogStatusRow(
                    icon = YamsIcons.Check,
                    title = "Grille complète",
                    description = "La partie peut être clôturée.",
                )
                GamePlayFinishedDialogStatusRow(
                    icon = YamsIcons.Undo,
                    title = "Dernier coup modifiable",
                    description = "Tu peux encore revenir en arrière si besoin.",
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = "Les résultats seront plus faciles à relire si le dernier score est corrigé maintenant.",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                YamsPrimaryButton(
                    onClick = onGoToResults,
                    text = "Voir les résultats",
                )
                YamsSecondaryButton(
                    onClick = onUndoLastMove,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Annuler le dernier coup")
                }
            }
        }
    }
}

@Composable
private fun GamePlayFinishedDialogStatusRow(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(14.dp),
            color = YamsTheme.colors.gold.copy(alpha = 0.14f),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = YamsTheme.colors.brown,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun GamePlayFinishedDialogContentPreview() {
    YamsTheme {
        GamePlayFinishedDialogContent(
            onGoToResults = {},
            onUndoLastMove = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
