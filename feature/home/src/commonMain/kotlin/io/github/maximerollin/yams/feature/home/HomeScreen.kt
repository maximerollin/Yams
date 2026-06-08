package io.github.maximerollin.yams.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.icon.RocketLaunch
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.GameHistoryCard
import io.github.maximerollin.yams.feature.user.common.HomeStatsCard
import io.github.maximerollin.yams.feature.user.common.LoadingState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import yams.feature.home.generated.resources.*
import yams.core.ui.generated.resources.app_icon
import yams.core.ui.generated.resources.Res as CoreUiRes

private const val AppName = "Yamigo"

@Composable
internal fun HomeRoute(
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToUsers: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToGameCreation = onNavigateToGameCreation,
        onNavigateToGameResult = onNavigateToGameResult,
        onNavigateToUsers = onNavigateToUsers,
        modifier = modifier,
    )
}

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onNavigateToGameCreation: () -> Unit,
    onNavigateToGameResult: (GameId) -> Unit,
    onNavigateToUsers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                modifier = Modifier.statusBarsPadding(),
                isDividerVisible = false,
                center = {
                    HomeBrandTitle()
                },
            )
        },
        bottomBar = {
            HomeBottomBar(onNavigateToGameCreation = onNavigateToGameCreation)
        },
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> LoadingState(
                message = stringResource(yams.feature.home.generated.resources.Res.string.home_loading),
                modifier = Modifier.padding(innerPadding),
            )

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp,
                        top = 16.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        HomeStatsCard(
                            stats = uiState.stats,
                            onClick = onNavigateToUsers,
                        )
                    }

                    item {
                        Text(
                            text = stringResource(yams.feature.home.generated.resources.Res.string.home_recent_games_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = YamsTheme.colors.brown,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    if (uiState.recentGames.isEmpty()) {
                        item {
                            EmptyState(
                                title = stringResource(yams.feature.home.generated.resources.Res.string.home_no_finished_games_title),
                                message = stringResource(yams.feature.home.generated.resources.Res.string.home_no_finished_games_message),
                            )
                        }
                    } else {
                        items(
                            items = uiState.recentGames,
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
}

@Composable
private fun HomeBrandTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(CoreUiRes.drawable.app_icon),
            contentDescription = stringResource(
                yams.feature.home.generated.resources.Res.string.home_logo_cd,
                AppName,
            ),
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape),
        )
        Text(
            text = AppName,
            modifier = Modifier.padding(start = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            color = YamsTheme.colors.brown,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun HomeBottomBar(
    onNavigateToGameCreation: () -> Unit,
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            YamsPrimaryButton(
                onClick = onNavigateToGameCreation,
                text = stringResource(yams.feature.home.generated.resources.Res.string.home_new_game),
                icon = IconInfo(
                    vector = YamsIcons.RocketLaunch,
                    contentDescription = stringResource(
                        yams.feature.home.generated.resources.Res.string.home_create_game_cd,
                    ),
                ),
            )
        }
    }
}
