package io.github.maximerollin.yams.feature.user.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsDestructiveButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.icon.AwardStar
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.Delete
import io.github.maximerollin.yams.core.designsystem.icon.Edit
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.Lock
import io.github.maximerollin.yams.core.designsystem.icon.MoreVert
import io.github.maximerollin.yams.core.designsystem.icon.Person
import io.github.maximerollin.yams.core.designsystem.icon.Strategy
import io.github.maximerollin.yams.core.designsystem.icon.Tactic
import io.github.maximerollin.yams.core.designsystem.icon.Target
import io.github.maximerollin.yams.core.designsystem.icon.Timer
import io.github.maximerollin.yams.core.designsystem.icon.Timeline
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.common.EmptyState
import io.github.maximerollin.yams.feature.user.common.LoadingState
import io.github.maximerollin.yams.feature.user.common.UserAvatar
import io.github.maximerollin.yams.feature.user.common.UserStatsUiState
import io.github.maximerollin.yams.feature.user.common.formatOneDecimal
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.ceil
import kotlin.math.roundToInt
import yams.feature.user.profile.generated.resources.Res
import yams.feature.user.profile.generated.resources.profile_average_rank
import yams.feature.user.profile.generated.resources.profile_back_cd
import yams.feature.user.profile.generated.resources.profile_basic_stats_title
import yams.feature.user.profile.generated.resources.profile_cancel
import yams.feature.user.profile.generated.resources.profile_delete
import yams.feature.user.profile.generated.resources.profile_delete_message
import yams.feature.user.profile.generated.resources.profile_delete_player
import yams.feature.user.profile.generated.resources.profile_delete_title
import yams.feature.user.profile.generated.resources.profile_detailed_stats_locked_message
import yams.feature.user.profile.generated.resources.profile_detailed_stats_locked_title
import yams.feature.user.profile.generated.resources.profile_detailed_stats_subtitle
import yams.feature.user.profile.generated.resources.profile_detailed_stats_title
import yams.feature.user.profile.generated.resources.profile_edit
import yams.feature.user.profile.generated.resources.profile_loading
import yams.feature.user.profile.generated.resources.profile_no_score_trend
import yams.feature.user.profile.generated.resources.profile_not_found_message
import yams.feature.user.profile.generated.resources.profile_not_found_title
import yams.feature.user.profile.generated.resources.profile_options_cd
import yams.feature.user.profile.generated.resources.profile_percent_value
import yams.feature.user.profile.generated.resources.profile_points_value
import yams.feature.user.profile.generated.resources.profile_premium_analysis_best_streak
import yams.feature.user.profile.generated.resources.profile_premium_analysis_consistency
import yams.feature.user.profile.generated.resources.profile_premium_analysis_current_streak
import yams.feature.user.profile.generated.resources.profile_premium_analysis_podiums
import yams.feature.user.profile.generated.resources.profile_premium_analysis_recent_average
import yams.feature.user.profile.generated.resources.profile_premium_analysis_recent_win_rate
import yams.feature.user.profile.generated.resources.profile_premium_analysis_subtitle
import yams.feature.user.profile.generated.resources.profile_premium_analysis_title
import yams.feature.user.profile.generated.resources.profile_premium_analysis_turns
import yams.feature.user.profile.generated.resources.profile_premium_analysis_yams_games
import yams.feature.user.profile.generated.resources.profile_score_average
import yams.feature.user.profile.generated.resources.profile_score_point_value
import yams.feature.user.profile.generated.resources.profile_score_per_turn
import yams.feature.user.profile.generated.resources.profile_score_record
import yams.feature.user.profile.generated.resources.profile_stats_games_played
import yams.feature.user.profile.generated.resources.profile_title
import yams.feature.user.profile.generated.resources.profile_turns_value
import yams.feature.user.profile.generated.resources.profile_unlock_plus
import yams.feature.user.profile.generated.resources.profile_view_history
import yams.feature.user.profile.generated.resources.profile_victories
import yams.feature.user.profile.generated.resources.profile_win_rate
import yams.feature.user.profile.generated.resources.profile_wins_short_value
import yams.feature.user.profile.generated.resources.profile_yams_per_game

@Composable
internal fun UserProfileRoute(
    userId: UserId,
    onNavigateBack: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToUserEdition: (UserId) -> Unit,
    onNavigateToUserHistory: (UserId) -> Unit,
    onNavigateToPaywall: () -> Unit,
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
        onNavigateToPaywall = onNavigateToPaywall,
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
    onNavigateToPaywall: () -> Unit,
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
                onNavigateToPaywall = onNavigateToPaywall,
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
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ProfileHeaderCard(uiState = uiState)
        ProfileBasicStatsCard(stats = uiState.stats)

        if (uiState.isPremium) {
            ProfilePremiumStatsContent(
                stats = uiState.stats,
                detailedStats = uiState.detailedStats,
                scoreTrend = uiState.scoreTrend,
            )
        } else {
            ProfileLockedPremiumStatsContent(
                stats = uiState.stats,
                detailedStats = uiState.detailedStats,
                scoreTrend = uiState.scoreTrend,
                onUnlockPremium = onNavigateToPaywall,
            )
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
    }
}

@Composable
private fun ProfileHeaderCard(
    uiState: UserProfileUiState.Success,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
            Text(
                text = uiState.user.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ProfileBasicStatsCard(
    stats: UserStatsUiState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.profile_basic_stats_title),
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileMetricTile(
                    icon = YamsIcons.History,
                    label = stringResource(Res.string.profile_stats_games_played),
                    value = stats.gamesPlayed.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProfileMetricTile(
                    icon = YamsIcons.Trophy,
                    label = stringResource(Res.string.profile_victories),
                    value = stats.victories.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProfileMetricTile(
                    icon = YamsIcons.Target,
                    label = stringResource(Res.string.profile_win_rate),
                    value = "${stats.winRatePercent()}%",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ProfilePremiumStatsContent(
    stats: UserStatsUiState,
    detailedStats: UserProfileDetailedStatsUiState,
    scoreTrend: List<UserProfileScoreTrendPoint>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ProfileDetailedStatsCard(
            stats = stats,
            detailedStats = detailedStats,
            scoreTrend = scoreTrend,
        )
        ProfilePremiumAnalysisCard(detailedStats = detailedStats)
    }
}

@Composable
private fun ProfileDetailedStatsCard(
    stats: UserStatsUiState,
    detailedStats: UserProfileDetailedStatsUiState,
    scoreTrend: List<UserProfileScoreTrendPoint>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileSectionTitle(
                icon = YamsIcons.Timeline,
                title = stringResource(Res.string.profile_detailed_stats_title),
                subtitle = stringResource(Res.string.profile_detailed_stats_subtitle),
            )
            ScoreTrendChart(
                points = scoreTrend,
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileStatValue(
                        label = stringResource(Res.string.profile_score_average),
                        value = stringResource(
                            Res.string.profile_points_value,
                            stats.averageScore.formatOneDecimal(),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    ProfileStatValue(
                        label = stringResource(Res.string.profile_score_record),
                        value = stringResource(
                            Res.string.profile_points_value,
                            stats.highestScore.toString(),
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileStatValue(
                        label = stringResource(Res.string.profile_yams_per_game),
                        value = detailedStats.averageYamsPerGame.formatOneDecimal(),
                        modifier = Modifier.weight(1f),
                    )
                    ProfileStatValue(
                        label = stringResource(Res.string.profile_average_rank),
                        value = detailedStats.averageRank.formatOneDecimal(),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfilePremiumAnalysisCard(
    detailedStats: UserProfileDetailedStatsUiState,
    modifier: Modifier = Modifier,
) {
    val metrics = listOf(
        ProfileInsightMetric(
            icon = YamsIcons.Timeline,
            label = stringResource(Res.string.profile_premium_analysis_recent_average),
            value = stringResource(
                Res.string.profile_points_value,
                detailedStats.recentAverageScore.formatOneDecimal(),
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.Trophy,
            label = stringResource(Res.string.profile_premium_analysis_recent_win_rate),
            value = stringResource(
                Res.string.profile_percent_value,
                detailedStats.recentWinRatePercent,
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.AwardStar,
            label = stringResource(Res.string.profile_premium_analysis_current_streak),
            value = stringResource(
                Res.string.profile_wins_short_value,
                detailedStats.currentWinStreak,
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.Strategy,
            label = stringResource(Res.string.profile_premium_analysis_best_streak),
            value = stringResource(
                Res.string.profile_wins_short_value,
                detailedStats.bestWinStreak,
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.Target,
            label = stringResource(Res.string.profile_premium_analysis_yams_games),
            value = stringResource(
                Res.string.profile_percent_value,
                detailedStats.yamsGameRatePercent,
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.Tactic,
            label = stringResource(Res.string.profile_premium_analysis_podiums),
            value = stringResource(
                Res.string.profile_percent_value,
                detailedStats.podiumRatePercent,
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.Timer,
            label = stringResource(Res.string.profile_premium_analysis_turns),
            value = stringResource(
                Res.string.profile_turns_value,
                detailedStats.averageTurnsPerGame.formatOneDecimal(),
            ),
        ),
        ProfileInsightMetric(
            icon = YamsIcons.History,
            label = stringResource(Res.string.profile_premium_analysis_consistency),
            value = stringResource(
                Res.string.profile_points_value,
                detailedStats.averageScoreDeviation.formatOneDecimal(),
            ),
        ),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileSectionTitle(
                icon = YamsIcons.Strategy,
                title = stringResource(Res.string.profile_premium_analysis_title),
                subtitle = stringResource(Res.string.profile_premium_analysis_subtitle),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                metrics.chunked(2).forEach { rowMetrics ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowMetrics.forEach { metric ->
                            ProfileInsightMetricTile(
                                metric = metric,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowMetrics.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileLockedPremiumStatsContent(
    stats: UserStatsUiState,
    detailedStats: UserProfileDetailedStatsUiState,
    scoreTrend: List<UserProfileScoreTrendPoint>,
    onUnlockPremium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        ProfilePremiumStatsContent(
            stats = stats,
            detailedStats = detailedStats,
            scoreTrend = scoreTrend,
            modifier = Modifier.blur(10.dp),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.48f),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    ProfileSectionTitle(
                        icon = YamsIcons.Lock,
                        title = stringResource(Res.string.profile_detailed_stats_locked_title),
                        subtitle = stringResource(Res.string.profile_detailed_stats_locked_message),
                    )
                    YamsPrimaryButton(
                        onClick = onUnlockPremium,
                        text = stringResource(Res.string.profile_unlock_plus),
                        icon = IconInfo(
                            vector = YamsIcons.Lock,
                            contentDescription = stringResource(Res.string.profile_unlock_plus),
                        ),
                    )
                }
            }
        }
    }
}

private data class ProfileInsightMetric(
    val icon: ImageVector,
    val label: String,
    val value: String,
)

@Composable
private fun ProfileInsightMetricTile(
    metric: ProfileInsightMetric,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = metric.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = YamsTheme.colors.brown,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = metric.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = YamsTheme.colors.brown,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileMetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = YamsTheme.colors.brown,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ProfileStatValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ScoreTrendChart(
    points: List<UserProfileScoreTrendPoint>,
    modifier: Modifier = Modifier,
) {
    val chartPoints = points.takeLast(7)
    if (chartPoints.size < 2) {
        EmptyScoreTrend(modifier = modifier)
        return
    }

    var selectedPointIndex by remember(chartPoints) { mutableStateOf(chartPoints.lastIndex) }
    val selectedPoint = chartPoints[selectedPointIndex]
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = lineColor.copy(alpha = 0.12f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.62f)
    val pointColor = MaterialTheme.colorScheme.surface
    val maxValue = chartPoints.maxOf { it.averagePointsPerTurn }.coerceAtLeast(1f)
    val roundedMaxValue = ceil(maxValue / 5f) * 5f
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.profile_score_per_turn),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(
                    Res.string.profile_score_point_value,
                    selectedPoint.label,
                    selectedPoint.averagePointsPerTurn.formatOneDecimal(),
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(122.dp)
                    .pointerInput(chartPoints) {
                        detectTapGestures { tapOffset ->
                            val chartWidth = size.width.toFloat()
                            if (chartWidth > 0f) {
                                val stepX = chartWidth / chartPoints.lastIndex.coerceAtLeast(1)
                                selectedPointIndex = (tapOffset.x / stepX)
                                    .roundToInt()
                                    .coerceIn(0, chartPoints.lastIndex)
                            }
                        }
                    },
            ) {
                val gridCount = 3
                repeat(gridCount + 1) { index ->
                    val y = size.height * index / gridCount
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                val stepX = size.width / (chartPoints.lastIndex).coerceAtLeast(1)
                val offsets = chartPoints.mapIndexed { index, point ->
                    val x = stepX * index
                    val y = size.height -
                        (point.averagePointsPerTurn.coerceAtLeast(0f) / roundedMaxValue) *
                        size.height
                    Offset(x, y)
                }

                val areaPath = Path().apply {
                    moveTo(offsets.first().x, size.height)
                    offsets.forEach { lineTo(it.x, it.y) }
                    lineTo(offsets.last().x, size.height)
                    close()
                }
                drawPath(path = areaPath, color = fillColor)

                offsets.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = lineColor,
                        start = start,
                        end = end,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }

                offsets.forEachIndexed { index, offset ->
                    val isSelected = index == selectedPointIndex
                    drawCircle(
                        color = if (isSelected) lineColor else pointColor,
                        radius = if (isSelected) 6.dp.toPx() else 5.dp.toPx(),
                        center = offset,
                    )
                    drawCircle(
                        color = lineColor,
                        radius = if (isSelected) 6.dp.toPx() else 5.dp.toPx(),
                        center = offset,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                chartPoints.forEach { point ->
                    Text(
                        text = point.label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyScoreTrend(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(Res.string.profile_no_score_trend),
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun UserStatsUiState.winRatePercent(): Int =
    if (gamesPlayed > 0) {
        (victories.toFloat() / gamesPlayed * 100f).roundToInt()
    } else {
        0
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

@YamsStoreScreenshotPreviews
@Composable
private fun UserProfileScreenPreview() {
    UserProfileStoreScreenshotContent()
}

@Preview(
    name = "Profil - Yams+",
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun UserProfilePremiumScreenPreview() {
    UserProfilePreviewContent(isPremium = true)
}

@Preview(
    name = "Profil - gratuit",
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun UserProfileFreeScreenPreview() {
    UserProfilePreviewContent(isPremium = false)
}

@Preview(
    name = "Stats Yams+ - complet",
    widthDp = 393,
)
@Composable
private fun ProfilePremiumStatsContentPreview() {
    YamsTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            ProfilePremiumStatsContent(
                stats = previewProfileStats(),
                detailedStats = previewDetailedStats(),
                scoreTrend = previewScoreTrend(),
            )
        }
    }
}

@Preview(
    name = "Stats Yams+ - verrouille",
    widthDp = 393,
)
@Composable
private fun ProfileLockedPremiumStatsContentPreview() {
    YamsTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            ProfileLockedPremiumStatsContent(
                stats = previewProfileStats(),
                detailedStats = previewDetailedStats(),
                scoreTrend = previewScoreTrend(),
                onUnlockPremium = {},
            )
        }
    }
}

@Preview(
    name = "Carte dernieres parties",
    widthDp = 393,
)
@Composable
private fun ProfileDetailedStatsCardPreview() {
    YamsTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            ProfileDetailedStatsCard(
                stats = previewProfileStats(),
                detailedStats = previewDetailedStats(),
                scoreTrend = previewScoreTrend(),
            )
        }
    }
}

@Preview(
    name = "Carte analyse Yams+",
    widthDp = 393,
)
@Composable
private fun ProfilePremiumAnalysisCardPreview() {
    YamsTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
        ) {
            ProfilePremiumAnalysisCard(detailedStats = previewDetailedStats())
        }
    }
}

@Composable
public fun UserProfileStoreScreenshotContent() {
    UserProfilePreviewContent(isPremium = true)
}

@Composable
private fun UserProfilePreviewContent(isPremium: Boolean) {
    YamsTheme {
        UserProfileScreen(
            uiState = UserProfileUiState.Success(
                user = UserMocks.users.first(),
                stats = previewProfileStats(),
                detailedStats = previewDetailedStats(),
                scoreTrend = previewScoreTrend(),
                isPremium = isPremium,
            ),
            onNavigateBack = {},
            onNavigateToUserEdition = {},
            onNavigateToUserHistory = {},
            onNavigateToPaywall = {},
            onDeleteUser = {},
        )
    }
}

private fun previewProfileStats(): UserStatsUiState =
    UserStatsUiState(
        gamesPlayed = 24,
        victories = 11,
        totalYams = 31,
        averageScore = 223.5f,
        highestScore = 286,
    )

private fun previewDetailedStats(): UserProfileDetailedStatsUiState =
    UserProfileDetailedStatsUiState(
        averagePointsPerTurn = 8.4f,
        averageRank = 1.8f,
        averageYamsPerGame = 1.3f,
        recentAverageScore = 231.4f,
        recentWinRatePercent = 60,
        currentWinStreak = 2,
        bestWinStreak = 4,
        yamsGameRatePercent = 71,
        podiumRatePercent = 83,
        averageTurnsPerGame = 13f,
        averageScoreDeviation = 14.8f,
    )

private fun previewScoreTrend(): List<UserProfileScoreTrendPoint> =
    listOf(
        UserProfileScoreTrendPoint(label = "18", averagePointsPerTurn = 7.1f),
        UserProfileScoreTrendPoint(label = "19", averagePointsPerTurn = 8.3f),
        UserProfileScoreTrendPoint(label = "20", averagePointsPerTurn = 6.9f),
        UserProfileScoreTrendPoint(label = "21", averagePointsPerTurn = 9.4f),
        UserProfileScoreTrendPoint(label = "22", averagePointsPerTurn = 8.1f),
        UserProfileScoreTrendPoint(label = "23", averagePointsPerTurn = 8.8f),
        UserProfileScoreTrendPoint(label = "24", averagePointsPerTurn = 8.4f),
    )
