package io.github.maximerollin.yams.feature.game.play

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GamePlayRoute(
    gameId: GameId,
    onNavigateHome: () -> Unit,
    onNavigateToResults: (gameId: GameId) -> Unit,
    viewModel: GamePlayViewModel = koinViewModel { parametersOf(gameId) }
) {
    GamePlayScreen()
}

@Composable
private fun GamePlayScreen() {
    Text("bonjour")
}

@Preview
@Composable
private fun GamePlayScreenPreview() {
    YamsTheme {
        GamePlayScreen()
    }
}