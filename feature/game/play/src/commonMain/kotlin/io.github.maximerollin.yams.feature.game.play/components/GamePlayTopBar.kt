package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.Home
import io.github.maximerollin.yams.core.designsystem.icon.Info
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors

@Composable
internal fun GamePlayTopBar(
    modifier: Modifier = Modifier,
    onNavigateHome: () -> Unit = {},
    onShowInformation: () -> Unit = {},
) {
    AppTopBar(
        modifier = modifier.statusBarsPadding(),
        isDividerVisible = false,
        start = {
            AppIconButton(
                icon = YamsIcons.Home,
                onClick = onNavigateHome,
                contentDescription = "Accueil",
            )
        },
        center = {
            Text(
                text = "Feuille de score",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center,
            )
        },
        end = {
            AppIconButton(
                icon = YamsIcons.Info,
                onClick = onShowInformation,
                contentDescription = "Informations",
            )
        },
    )
}

@Preview
@Composable
private fun GamePlayTopBarPreview() {
    YamsTheme {
        Scaffold(
            topBar = {
                GamePlayTopBar()
            },
        ) {}
    }
}
