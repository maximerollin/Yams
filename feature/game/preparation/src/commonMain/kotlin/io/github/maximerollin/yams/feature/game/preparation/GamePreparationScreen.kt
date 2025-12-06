package io.github.maximerollin.yams.feature.game.preparation

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GamePreparationRoute(
    usersIds: Set<UserId>,
    onNavigateBack: () -> Unit,
    onNavigateGame: (GameId) -> Unit,
    viewModel: GamePreparationViewModel = koinViewModel { parametersOf(usersIds) }
) {
    GamePreparationScreen(

    )
}

@Composable
private fun GamePreparationScreen(
    uiState: GamePreparationUiState,
    onNavigateBack: () -> Unit,
    onCreateGame: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

}

@Preview
@Composable
private fun GamePreparationScreenPreview() {
    YamsTheme {
        GamePreparationScreen(
            uiState = GamePreparationUiState(),
            onNavigateBack = {},
            onCreateGame = {},
        )
    }
}


