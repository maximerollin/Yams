package io.github.maximerollin.yams.feature.user.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.Person
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.GameHistoryCard
import io.github.maximerollin.yams.feature.user.common.LoadingState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import yams.feature.user.history.generated.resources.*

@Composable
internal fun UserHistoryRoute(
    userId: UserId,
    onNavigateBack: () -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserHistoryViewModel = koinViewModel { parametersOf(userId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserHistoryScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToGameResult = onNavigateToGameResult,
        modifier = modifier,
    )
}

@Composable
private fun UserHistoryScreen(
    uiState: UserHistoryUiState,
    onNavigateBack: () -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                modifier = Modifier.statusBarsPadding(),
                isDividerVisible = false,
                start = {
                    AppIconButton(
                        icon = YamsIcons.ChevronLeft,
                        contentDescription = stringResource(Res.string.history_back_cd),
                        onClick = onNavigateBack,
                    )
                },
                center = {
                    val title = when (uiState) {
                        is UserHistoryUiState.Success -> stringResource(
                            Res.string.history_title_with_user,
                            uiState.user.name,
                        )
                        else -> stringResource(Res.string.history_title)
                    }

                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = YamsTheme.colors.brown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            UserHistoryUiState.Loading -> LoadingState(
                message = stringResource(Res.string.history_loading),
                modifier = Modifier.padding(innerPadding),
            )

            UserHistoryUiState.NotFound -> EmptyState(
                title = stringResource(Res.string.history_not_found_title),
                message = stringResource(Res.string.history_not_found_message),
                icon = YamsIcons.Person,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp),
            )

            is UserHistoryUiState.Success -> {
                if (uiState.games.isEmpty()) {
                    EmptyState(
                        title = stringResource(Res.string.history_empty_title),
                        message = stringResource(Res.string.history_empty_message),
                        icon = YamsIcons.History,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(20.dp),
                    )
                    return@Scaffold
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = uiState.games,
                        key = { it.gameId.value },
                    ) { game ->
                        GameHistoryCard(
                            game = game,
                            onClick = { onNavigateToGameResult(game.gameId) },
                        )
                    }
                }
            }
        }
    }
}
