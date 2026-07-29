package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.YamsTextButton
import io.github.maximerollin.yams.core.designsystem.icon.PencilSparkles
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.Res
import yams.feature.game.play.generated.resources.play_assistant_cd
import yams.feature.game.play.generated.resources.play_assistant_discovery_acknowledge
import yams.feature.game.play.generated.resources.play_assistant_discovery_body
import yams.feature.game.play.generated.resources.play_assistant_discovery_title

internal fun shouldShowDiceAssistantDiscovery(
    hasSeenDiscovery: Boolean?,
    hasPlayers: Boolean,
): Boolean = hasSeenDiscovery == false && hasPlayers

@Composable
internal fun GamePlayDiceAssistantDiscoveryOverlay(
    isPremiumEnabled: Boolean,
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit,
    onShowAssistant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                .clickable(onClick = onDismiss),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 8.dp, end = 64.dp)
                    .size(64.dp)
                    .border(
                        width = 3.dp,
                        color = YamsTheme.colors.gold,
                        shape = CircleShape,
                    )
                    .padding(8.dp),
            ) {
                AppIconButton(
                    icon = YamsIcons.PencilSparkles,
                    onClick = onShowAssistant,
                    contentDescription = stringResource(Res.string.play_assistant_cd),
                    contentColor = if (isPremiumEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    },
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 84.dp, end = 16.dp, start = 16.dp)
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {})
                    },
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.play_assistant_discovery_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(Res.string.play_assistant_discovery_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    YamsTextButton(
                        onClick = onAcknowledge,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.play_assistant_discovery_acknowledge,
                            ),
                        )
                    }
                }
            }
        }
    }
}
