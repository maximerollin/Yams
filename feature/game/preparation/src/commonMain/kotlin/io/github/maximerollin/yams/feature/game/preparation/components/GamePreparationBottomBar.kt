package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme

@Composable
public fun GamePreparationBottomBar(
    createGameLoading: Boolean,
    isUserOrderRandomized: Boolean,
    onCreateGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.navigationBarsPadding(),
    ) {
        YamsPrimaryButton(
            onClick = onCreateGame,
            loading = createGameLoading,
            text = when (createGameLoading) {
                false -> "Lancer la partie"
                true -> "Lancement ..."
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun GamePreparationBottomBarPreview() {
    YamsTheme {
        Scaffold(
            bottomBar = {
                GamePreparationBottomBar(
                    createGameLoading = false,
                    isUserOrderRandomized = false,
                    onCreateGame = {}
                )
            }
        ) { }

    }
}