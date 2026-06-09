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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationBottomBar
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationDeleteUserDialog
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationEmptyUser
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationTopBar
import io.github.maximerollin.yams.feature.game.creation.components.GameCreationUserCard
import io.github.maximerollin.yams.feature.user.edition.UserEditionAction
import io.github.maximerollin.yams.feature.user.edition.UserEditionBottomSheet
import io.github.maximerollin.yams.feature.user.edition.UserEditionUiState
import io.github.maximerollin.yams.feature.user.edition.UserEditionViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun GameCreationRoute(
    onNavigateHome: () -> Unit,
    onNavigateWelcome: () -> Unit,
    onNavigateGamePreparation: (Set<UserId>) -> Unit,
    viewModel: GameCreationViewModel = koinViewModel(),
    userEditionViewModel: UserEditionViewModel = koinViewModel()
) {

    val newGameUiState by viewModel.gameCreationUiState.collectAsStateWithLifecycle()
    val userUiState by viewModel.usersUiState.collectAsStateWithLifecycle()
    val userEditionUiState by userEditionViewModel.uiState.collectAsStateWithLifecycle()
    val gamesNumber by viewModel.gamesNumber.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current

    val savedNewUserId = userEditionUiState.savedNewUserId
    LaunchedEffect(savedNewUserId) {
        if (savedNewUserId != null) {
            viewModel.selectUser(savedNewUserId)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    GameCreationScreen(
        gameCreationUiState = newGameUiState,
        userUiState = userUiState,
        userEditionUiState = userEditionUiState,
        onUserEditionAction = userEditionViewModel::onAction,
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
    userEditionUiState: UserEditionUiState,
    onUserEditionAction: (UserEditionAction) -> Unit,
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

    var showBottomSheet by remember { mutableStateOf(false) }
    var editUserSelected by remember { mutableStateOf<User?>(null) }
    var deleteUserSelected by remember { mutableStateOf<User?>(null) }

    fun openBottomSheet() {
        onUserEditionAction(UserEditionAction.ResetEdition)
        showBottomSheet = true
    }

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GameCreationTopBar(
                onNavigateHome = onNavigateHome,
                onCreateUser = ::openBottomSheet,
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
                            onCreateUser = { userName ->
                                openBottomSheet()
                                userName?.let {
                                    onUserEditionAction(UserEditionAction.EditName(it))
                                }
                            }
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
                                Box {
                                    GameCreationUserCard(
                                        user = user,
                                        isSelected = gameCreationUiState.selectedUsersIds.contains(
                                            user.id
                                        ),
                                        onClick = { onSelectUser(user.id) },
                                        onLongClick = {
                                            onUserEditionAction(UserEditionAction.StartEdition(it))
                                            editUserSelected = user
                                            showBottomSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showBottomSheet) {
                UserEditionBottomSheet(
                    uiState = userEditionUiState,
                    onAction = onUserEditionAction,
                    isEdit = editUserSelected != null,
                    onDeleteUser = {
                        deleteUserSelected = editUserSelected
                        editUserSelected = null
                        showBottomSheet = false
                    },
                    onDismissRequest = {
                        editUserSelected = null
                        showBottomSheet = false
                    }
                )
            }

            val deleteUser = deleteUserSelected
            if (deleteUser != null) {
                GameCreationDeleteUserDialog(
                    user = deleteUser,
                    onDismissDialog = { deleteUserSelected = null },
                    onDelete = {
                        onDeleteUser(deleteUser.id)
                        deleteUserSelected = null
                    },
                )
            }
        }
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun GameCreationScreenPreview() {
    GameCreationEmptyStoreScreenshotContent()
}

@Composable
public fun GameCreationEmptyStoreScreenshotContent() {
    YamsTheme {
        GameCreationScreen(
            gameCreationUiState = GameCreationUiState(),
            userUiState = UserUiState.Success(emptyList()),
            userEditionUiState = UserEditionUiState(),
            onUserEditionAction = {},
            onNavigateHome = {},
            onSelectUser = {},
            onCreateGame = {},
            onDeleteUser = {},
        )
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun GameCreationScreenWithUsersPreview() {
    GameCreationSelectedStoreScreenshotContent()
}

@Composable
public fun GameCreationSelectedStoreScreenshotContent() {
    YamsTheme {
        GameCreationScreen(
            gameCreationUiState = GameCreationUiState(
                selectedUsersIds = UserMocks.users.subList(0, 3).map { it.id }.toSet()
            ),
            userUiState = UserUiState.Success(UserMocks.users),
            userEditionUiState = UserEditionUiState(),
            onUserEditionAction = {},
            onNavigateHome = {},
            onSelectUser = {},
            onCreateGame = {},
            onDeleteUser = {},
        )
    }
}
