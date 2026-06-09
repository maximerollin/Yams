package io.github.maximerollin.yams.feature.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsDestructiveButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.Edit
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.MoreVert
import io.github.maximerollin.yams.core.designsystem.icon.Person
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.GameSummaryUiState
import io.github.maximerollin.yams.feature.user.common.GameHistoryCard
import io.github.maximerollin.yams.feature.user.common.LoadingState
import io.github.maximerollin.yams.feature.user.common.PlayerSummaryUiState
import io.github.maximerollin.yams.feature.user.common.ProfileStatRow
import io.github.maximerollin.yams.feature.user.common.UserAvatar
import io.github.maximerollin.yams.feature.user.common.UserStatsUiState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import yams.feature.user.profile.generated.resources.Res
import yams.feature.user.profile.generated.resources.profile_back_cd
import yams.feature.user.profile.generated.resources.profile_cancel
import yams.feature.user.profile.generated.resources.profile_delete
import yams.feature.user.profile.generated.resources.profile_delete_message
import yams.feature.user.profile.generated.resources.profile_delete_player
import yams.feature.user.profile.generated.resources.profile_delete_title
import yams.feature.user.profile.generated.resources.profile_edit
import yams.feature.user.profile.generated.resources.profile_loading
import yams.feature.user.profile.generated.resources.profile_no_games_message
import yams.feature.user.profile.generated.resources.profile_no_games_title
import yams.feature.user.profile.generated.resources.profile_not_found_message
import yams.feature.user.profile.generated.resources.profile_not_found_title
import yams.feature.user.profile.generated.resources.profile_options_cd
import yams.feature.user.profile.generated.resources.profile_recent_games
import yams.feature.user.profile.generated.resources.profile_title
import yams.feature.user.profile.generated.resources.profile_victories_many
import yams.feature.user.profile.generated.resources.profile_victories_one
import yams.feature.user.profile.generated.resources.profile_view_history

@Composable
internal fun UserProfileRoute(
    userId: UserId,
    onNavigateBack: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToUserEdition: (UserId) -> Unit,
    onNavigateToUserHistory: (UserId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = koinViewModel { parametersOf(userId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationTarget by viewModel.navigationTarget.collectAsStateWithLifecycle()

    LaunchedEffect(navigationTarget) {
        when (navigationTarget) {
            UserProfileNavigationTarget.USERS -> {
                onNavigateToUsers()
                viewModel.onNavigationHandled()
            }

            null -> Unit
        }
    }

    UserProfileScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToUserEdition = onNavigateToUserEdition,
        onNavigateToUserHistory = onNavigateToUserHistory,
        onNavigateToGameResult = onNavigateToGameResult,
        onDeleteUser = viewModel::deleteUser,
        modifier = modifier,
    )
}

@Composable
private fun UserProfileScreen(
    uiState: UserProfileUiState,
    onNavigateBack: () -> Unit,
    onNavigateToUserEdition: (UserId) -> Unit,
    onNavigateToUserHistory: (UserId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onDeleteUser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isOptionsSheetVisible by remember { mutableStateOf(false) }
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                modifier = Modifier.statusBarsPadding(),
                isDividerVisible = false,
                start = {
                    AppIconButton(
                        icon = YamsIcons.ChevronLeft,
                        contentDescription = stringResource(Res.string.profile_back_cd),
                        onClick = onNavigateBack,
                    )
                },
                center = {
                    Text(
                        text = stringResource(Res.string.profile_title),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = YamsTheme.colors.brown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                end = {
                    if (uiState is UserProfileUiState.Success) {
                        AppIconButton(
                            icon = YamsIcons.MoreVert,
                            contentDescription = stringResource(Res.string.profile_options_cd),
                            onClick = { isOptionsSheetVisible = true },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            UserProfileUiState.Loading -> LoadingState(
                message = stringResource(Res.string.profile_loading),
                modifier = Modifier.padding(innerPadding),
            )

            UserProfileUiState.NotFound -> EmptyState(
                title = stringResource(Res.string.profile_not_found_title),
                message = stringResource(Res.string.profile_not_found_message),
                icon = YamsIcons.Person,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp),
            )

            is UserProfileUiState.Success -> UserProfileContent(
                uiState = uiState,
                onNavigateToUserHistory = onNavigateToUserHistory,
                onNavigateToGameResult = onNavigateToGameResult,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    val successState = uiState as? UserProfileUiState.Success
    if (isOptionsSheetVisible && successState != null) {
        ProfileOptionsBottomSheet(
            userName = successState.user.name,
            onEdit = {
                isOptionsSheetVisible = false
                onNavigateToUserEdition(successState.user.id)
            },
            onDelete = {
                isOptionsSheetVisible = false
                isDeleteDialogVisible = true
            },
            onDismiss = { isOptionsSheetVisible = false },
        )
    }

    if (isDeleteDialogVisible && successState != null) {
        DeleteUserDialog(
            userName = successState.user.name,
            onDismiss = { isDeleteDialogVisible = false },
            onConfirm = {
                isDeleteDialogVisible = false
                onDeleteUser()
            },
        )
    }
}

@Composable
private fun UserProfileContent(
    uiState: UserProfileUiState.Success,
    onNavigateToUserHistory: (UserId) -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                UserAvatar(
                    user = uiState.user,
                    size = 96.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            if (uiState.stats.victories > 1) {
                                Res.string.profile_victories_many
                            } else {
                                Res.string.profile_victories_one
                            },
                            uiState.stats.victories,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                ProfileStatRow(stats = uiState.stats)
            }
        }

        YamsSecondaryButton(
            onClick = { onNavigateToUserHistory(uiState.user.id) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = YamsIcons.History,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(Res.string.profile_view_history),
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold,
            )
        }

        Text(
            text = stringResource(Res.string.profile_recent_games),
            style = MaterialTheme.typography.titleMedium,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
        )

        if (uiState.recentGames.isEmpty()) {
            EmptyState(
                title = stringResource(Res.string.profile_no_games_title),
                message = stringResource(Res.string.profile_no_games_message),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.recentGames.forEach { game ->
                    GameHistoryCard(
                        game = game,
                        onClick = { onNavigateToGameResult(game.gameId) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileOptionsBottomSheet(
    userName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    fun dismissThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion {
                action()
            }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
            YamsPrimaryButton(
                onClick = { dismissThen(onEdit) },
                text = stringResource(Res.string.profile_edit),
                icon = IconInfo(
                    vector = YamsIcons.Edit,
                    contentDescription = stringResource(Res.string.profile_edit),
                ),
            )
            YamsDestructiveButton(
                onClick = { dismissThen(onDelete) },
                text = stringResource(Res.string.profile_delete_player),
                icon = IconInfo(
                    vector = YamsIcons.Delete,
                    contentDescription = stringResource(Res.string.profile_delete_player),
                ),
            )
        }
    }
}

@Composable
private fun DeleteUserDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = YamsIcons.Delete,
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(Res.string.profile_delete_title, userName))
        },
        text = {
            Text(text = stringResource(Res.string.profile_delete_message))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.profile_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.profile_delete))
            }
        },
    )
}

@OptIn(ExperimentalTime::class)
@YamsStoreScreenshotPreviews
@Composable
private fun UserProfileScreenPreview() {
    UserProfileStoreScreenshotContent()
}

@OptIn(ExperimentalTime::class)
@Composable
public fun UserProfileStoreScreenshotContent() {
    YamsTheme {
        UserProfileScreen(
            uiState = UserProfileUiState.Success(
                user = UserMocks.users.first(),
                stats = UserStatsUiState(
                    gamesPlayed = 24,
                    victories = 11,
                    totalYams = 31,
                    averageScore = 223.5f,
                    highestScore = 286,
                ),
                recentGames = previewProfileGames(),
            ),
            onNavigateBack = {},
            onNavigateToUserEdition = {},
            onNavigateToUserHistory = {},
            onNavigateToGameResult = {},
            onDeleteUser = {},
        )
    }
}

@OptIn(ExperimentalTime::class)
private fun previewProfileGames(): List<GameSummaryUiState> {
    val players = UserMocks.users.take(4).mapIndexed { index, user ->
        PlayerSummaryUiState(
            userId = user.id,
            name = user.name,
            avatar = user.avatar,
            rank = index + 1,
            score = listOf(286, 249, 218, 207)[index],
            yams = listOf(3, 2, 1, 1)[index],
            isWinner = index == 0,
        )
    }

    return listOf(
        GameSummaryUiState(
            gameId = GameId("preview-profile-game-1"),
            gameNumber = 24,
            finishedAt = Clock.System.now(),
            photo = null,
            players = players,
            topPlayers = players.take(3),
            totalPlayers = players.size,
            totalYams = players.sumOf { it.yams },
            highestScore = players.maxOf { it.score },
        ),
        GameSummaryUiState(
            gameId = GameId("preview-profile-game-2"),
            gameNumber = 23,
            finishedAt = Clock.System.now(),
            photo = null,
            players = players.drop(1) + players.first(),
            topPlayers = players.drop(1).take(3),
            totalPlayers = players.size,
            totalYams = 4,
            highestScore = 263,
        ),
    )
}
