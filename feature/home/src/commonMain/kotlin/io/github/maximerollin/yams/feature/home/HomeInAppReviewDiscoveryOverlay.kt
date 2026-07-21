package io.github.maximerollin.yams.feature.home

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
import io.github.maximerollin.yams.core.designsystem.icon.Star
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.resources.stringResource
import yams.feature.home.generated.resources.Res
import yams.feature.home.generated.resources.home_rate_app_cd
import yams.feature.home.generated.resources.home_rate_app_discovery_acknowledge
import yams.feature.home.generated.resources.home_rate_app_discovery_body
import yams.feature.home.generated.resources.home_rate_app_discovery_title

internal fun shouldShowHomeInAppReviewDiscovery(
    hasSeenDiscovery: Boolean?,
    canRequestInAppReview: Boolean,
): Boolean = hasSeenDiscovery == false && canRequestInAppReview

@Composable
internal fun HomeInAppReviewDiscoveryOverlay(
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit,
    onShowReview: () -> Unit,
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
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 8.dp)
                    .size(64.dp)
                    .border(
                        width = 3.dp,
                        color = YamsTheme.colors.gold,
                        shape = CircleShape,
                    )
                    .padding(8.dp),
            ) {
                AppIconButton(
                    icon = YamsIcons.Star,
                    onClick = onShowReview,
                    contentDescription = stringResource(Res.string.home_rate_app_cd),
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 84.dp, start = 16.dp, end = 16.dp)
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
                        text = stringResource(Res.string.home_rate_app_discovery_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(Res.string.home_rate_app_discovery_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    YamsTextButton(
                        onClick = onAcknowledge,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(text = stringResource(Res.string.home_rate_app_discovery_acknowledge))
                    }
                }
            }
        }
    }
}
