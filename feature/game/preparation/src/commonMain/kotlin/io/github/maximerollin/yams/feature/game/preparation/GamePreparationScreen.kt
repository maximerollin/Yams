package io.github.maximerollin.yams.feature.game.preparation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationBottomBar
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationTopBar
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationUserOrder
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GamePreparationRoute(
    usersIds: Set<UserId>,
    onNavigateBack: () -> Unit,
    onNavigateGame: (GameId) -> Unit,
    viewModel: GamePreparationViewModel = koinViewModel { parametersOf(usersIds) }
) {
    val gamePreparationUiState by viewModel.gamePreparationUiState.collectAsStateWithLifecycle()
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()

    GamePreparationScreen(
        gamePreparationUiState = gamePreparationUiState,
        usersState = usersState,
        onNavigateBack = onNavigateBack,
        onCreateGame = {},
        onToggleIsUserOrderRandomized = viewModel::onToggleIsUserOrderRandomized,
        onOrderUser = { from, to -> viewModel.orderUser(from, to) }
    )
}

@Composable
private fun GamePreparationScreen(
    gamePreparationUiState: GamePreparationUiState,
    usersState: List<User>,
    onNavigateBack: () -> Unit,
    onCreateGame: () -> Unit,
    onToggleIsUserOrderRandomized: (Boolean) -> Unit,
    onOrderUser: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GamePreparationTopBar(
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            GamePreparationBottomBar(
                onCreateGame = onCreateGame,
                createGameLoading = gamePreparationUiState.createGameLoading,
                isUserOrderRandomized = gamePreparationUiState.isUserOrderRandomized,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            GamePreparationUserOrder(
                users = usersState,
                isUserOrderRandomized = gamePreparationUiState.isUserOrderRandomized,
                onToggleIsUserOrderRandomized = onToggleIsUserOrderRandomized,
                onOrderUser = onOrderUser,
            )
        }
    }
}

@Preview
@Composable
private fun GamePreparationScreenRandomPreview() {
    YamsTheme {
        GamePreparationScreen(
            gamePreparationUiState = GamePreparationUiState(isUserOrderRandomized = true),
            usersState = UserMocks.users.subList(0, 3),
            onNavigateBack = {},
            onCreateGame = {},
            onToggleIsUserOrderRandomized = {},
            onOrderUser = { _, _ -> {} }
        )
    }
}

@Preview
@Composable
private fun GamePreparationScreenManualPreview() {
    YamsTheme {
        GamePreparationScreen(
            gamePreparationUiState = GamePreparationUiState(isUserOrderRandomized = false),
            usersState = UserMocks.users.subList(0, 3),
            onNavigateBack = {},
            onCreateGame = {},
            onToggleIsUserOrderRandomized = {},
            onOrderUser = { _, _ -> {} }
        )
    }
}


