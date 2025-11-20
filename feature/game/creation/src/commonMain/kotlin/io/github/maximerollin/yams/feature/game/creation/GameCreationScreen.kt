package io.github.maximerollin.yams.feature.game.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationBottomBar
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationEmptyUser
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationTopBar
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationUserCard
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GameCreationRoute(
    onNavigateHome: () -> Unit,
    onNavigateWelcome: () -> Unit,
    onNavigateGamePreparation: (Set<UserId>) -> Unit,
    viewModel: GameCreationViewModel = koinViewModel(),
) {

    val newGameUiState by viewModel.gameCreationUiState.collectAsStateWithLifecycle()
    val userUiState by viewModel.usersUiState.collectAsStateWithLifecycle()
    val gamesNumber by viewModel.gamesNumber.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current

    GameCreationScreen(
        gameCreationUiState = newGameUiState,
        userUiState = userUiState,
        onNavigateHome = {
            when {
                gamesNumber > 0 -> onNavigateHome()
                else -> onNavigateWelcome()
            }
        },
        onSelectUser = {
            viewModel.toggleUser(it)
            if (it !in newGameUiState.selectedUsersIds) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        },
        onCreateGame = { onNavigateGamePreparation(newGameUiState.selectedUsersIds) },
        onDeleteUser = viewModel::deleteUser
    )
}

@Composable
internal fun GameCreationScreen(
    gameCreationUiState: GameCreationUiState,
    userUiState: UserUiState,
    onNavigateHome: () -> Unit,
    onSelectUser: (UserId) -> Unit,
    onCreateGame: () -> Unit,
    onDeleteUser: (UserId) -> Unit,
) {

    val selectedUsers = gameCreationUiState.selectedUsersIds.mapNotNull { userId ->
        when (userUiState) {
            is UserUiState.Loading -> null
            is UserUiState.Success -> userUiState.users.find { it.id == userId }
        }
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GameCreationTopBar(
                isDividerVisible = true,
                onNavigateHome = onNavigateHome,
                onCreateUser = {},
            )
        },
        bottomBar = {
            GameCreationBottomBar(
                selectedUsers = selectedUsers,
                onCreateGame = onCreateGame,
                modifier = Modifier.imePadding(),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (userUiState) {
                is UserUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UserUiState.Success -> {
                    if (userUiState.users.isEmpty()) {
                        GameCreationEmptyUser(
                            onCreateUser = {}
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(userUiState.users, key = { it.id.value }) { user ->
                                GameCreationUserCard(
                                    user = user,
                                    isSelected = gameCreationUiState.selectedUsersIds.contains(user.id),
                                    onClick = { onSelectUser(user.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GameCreationScreenPreview() {
    YamsTheme {
        GameCreationScreen(
            gameCreationUiState = GameCreationUiState(),
            userUiState = UserUiState.Success(emptyList()),
            onNavigateHome = {},
            onSelectUser = {},
            onCreateGame = {},
            onDeleteUser = {},
        )
    }
}

@Preview
@Composable
private fun GameCreationScreenWithUsersPreview() {
    YamsTheme {
        GameCreationScreen(
            gameCreationUiState = GameCreationUiState(
                selectedUsersIds = UserMocks.users.subList(0, 3).map { it.id }.toSet()
            ),
            userUiState = UserUiState.Success(UserMocks.users),
            onNavigateHome = {},
            onSelectUser = {},
            onCreateGame = {},
            onDeleteUser = {},
        )
    }
}


