package io.github.maximerollin.yams.feature.user.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.Person
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.LoadingState
import io.github.maximerollin.yams.feature.user.common.UserAvatar
import io.github.maximerollin.yams.feature.user.common.UserCardUiState
import io.github.maximerollin.yams.feature.user.common.UserStatsUiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import yams.feature.user.users.generated.resources.*

@Composable
internal fun UsersRoute(
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (UserId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UsersScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToUserProfile = onNavigateToUserProfile,
        modifier = modifier,
    )
}

@Composable
private fun UsersScreen(
    uiState: UsersUiState,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (UserId) -> Unit,
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
                        contentDescription = stringResource(Res.string.users_back_cd),
                        onClick = onNavigateBack,
                    )
                },
                center = {
                    Text(
                        text = stringResource(Res.string.users_title),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = YamsTheme.colors.brown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            UsersUiState.Loading -> LoadingState(
                message = stringResource(Res.string.users_loading),
                modifier = Modifier.padding(innerPadding),
            )

            is UsersUiState.Success -> {
                if (uiState.users.isEmpty()) {
                    EmptyState(
                        title = stringResource(Res.string.users_empty_title),
                        message = stringResource(Res.string.users_empty_message),
                        icon = YamsIcons.Person,
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
                        items = uiState.users,
                        key = { it.user.id.value },
                    ) { user ->
                        UserCard(
                            user = user,
                            onClick = { onNavigateToUserProfile(user.user.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserCardUiState,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(
                    user = user.user,
                    size = 56.dp,
                )

                Text(
                    text = user.user.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                UserBestScore(
                    label = stringResource(Res.string.users_metric_highest_score),
                    value = if (user.stats.highestScore > 0) {
                        stringResource(Res.string.users_metric_score_points, user.stats.highestScore)
                    } else {
                        "-"
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                UserMetric(
                    label = stringResource(Res.string.users_metric_victories),
                    value = user.stats.victories.toString(),
                    modifier = Modifier.weight(1f),
                    leadingIcon = YamsIcons.Trophy,
                )
                UserMetric(
                    label = stringResource(Res.string.users_metric_games),
                    value = user.stats.gamesPlayed.toString(),
                    modifier = Modifier.weight(1f),
                )
                UserMetric(
                    label = stringResource(Res.string.users_metric_yams),
                    value = user.stats.totalYams.toString(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun UserMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = YamsTheme.colors.brown,
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun UserBestScore(
    label: String,
    value: String,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = YamsTheme.colors.gold.copy(alpha = 0.16f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = YamsTheme.colors.brown,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun UsersScreenPreview() {
    UsersStoreScreenshotContent()
}

@Composable
public fun UsersStoreScreenshotContent() {
    YamsTheme {
        UsersScreen(
            uiState = UsersUiState.Success(
                users = UserMocks.users.mapIndexed { index, user ->
                    UserCardUiState(
                        user = user,
                        stats = UserStatsUiState(
                            gamesPlayed = 12 + index,
                            victories = 5 - index.coerceAtMost(4),
                            totalYams = 9 + index,
                            averageScore = 214f - index * 8f,
                            highestScore = 286 - index * 13,
                        ),
                    )
                }
            ),
            onNavigateBack = {},
            onNavigateToUserProfile = {},
        )
    }
}
