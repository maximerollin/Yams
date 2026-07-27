package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.Home
import io.github.maximerollin.yams.core.designsystem.icon.Info
import io.github.maximerollin.yams.core.designsystem.icon.PencilSparkles
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*

@Composable
internal fun GamePlayTopBar(
    modifier: Modifier = Modifier,
    isAssistantPremiumEnabled: Boolean = false,
    onNavigateHome: () -> Unit = {},
    onShowAssistant: () -> Unit = {},
    onShowInformation: () -> Unit = {},
) {
    AppTopBar(
        modifier = modifier.statusBarsPadding(),
        isDividerVisible = false,
        start = {
            AppIconButton(
                icon = YamsIcons.Home,
                onClick = onNavigateHome,
                contentDescription = stringResource(Res.string.play_home_cd),
            )
        },
        center = {
            Text(
                text = stringResource(Res.string.play_title),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center,
            )
        },
        end = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppIconButton(
                    icon = YamsIcons.PencilSparkles,
                    onClick = onShowAssistant,
                    contentDescription = stringResource(Res.string.play_assistant_cd),
                    contentColor = if (isAssistantPremiumEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    },
                )
                AppIconButton(
                    icon = YamsIcons.Info,
                    onClick = onShowInformation,
                    contentDescription = stringResource(Res.string.play_info_cd),
                )
            }
        },
    )
}

@Preview
@Composable
private fun GamePlayTopBarLockedPreview() {
    YamsTheme {
        Scaffold(
            topBar = {
                GamePlayTopBar()
            },
        ) {}
    }
}

@Preview
@Composable
private fun GamePlayTopBarPremiumPreview() {
    YamsTheme {
        Scaffold(
            topBar = {
                GamePlayTopBar(isAssistantPremiumEnabled = true)
            },
        ) {}
    }
}
