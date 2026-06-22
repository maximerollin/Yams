package io.github.maximerollin.yams.feature.game.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.YamsCelebrationConfetti
import io.github.maximerollin.yams.core.designsystem.icon.Undo
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsPhoneStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.Game
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.data.game.model.ScoreCellRef
import io.github.maximerollin.yams.data.preference.GamePlayUiDensity
import io.github.maximerollin.yams.feature.game.play.components.GamePlayCombinationSection
import io.github.maximerollin.yams.feature.game.play.components.GamePlayCustomRulesSection
import io.github.maximerollin.yams.feature.game.play.components.GamePlayDensityDiscoveryOverlay
import io.github.maximerollin.yams.feature.game.play.components.GamePlayFinishedDialog
import io.github.maximerollin.yams.feature.game.play.components.GamePlayInformationBottomSheet
import io.github.maximerollin.yams.feature.game.play.components.GamePlayScoreSheetOverviewCard
import io.github.maximerollin.yams.feature.game.play.components.GamePlayTopBar
import io.github.maximerollin.yams.feature.game.play.components.GamePlayUpperScoreSection
import io.github.maximerollin.yams.feature.game.play.components.shouldShowGamePlayDensityDiscovery
import io.github.maximerollin.yams.feature.game.play.mock.gamePlayPreviewUiState
import io.github.maximerollin.yams.feature.game.play.model.GamePlayColumnSummary
import io.github.maximerollin.yams.feature.game.play.model.GamePlayStateUi
import io.github.maximerollin.yams.feature.game.play.model.GameStatus
import io.github.maximerollin.yams.feature.game.play.model.PlayerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import yams.feature.game.play.generated.resources.*

@Composable
internal fun GamePlayRoute(
    gameId: GameId,
    onNavigateHome: () -> Unit,
    onNavigateToResults: (gameId: GameId) -> Unit,
    viewModel: GamePlayViewModel = koinViewModel { parametersOf(gameId) },
) {
    val uiState by viewModel.gamePlayStateUi.collectAsStateWithLifecycle()
    val navigateToGameResult by viewModel.navigateToGameResult.collectAsStateWithLifecycle()
    val isHapticFeedbackEnabled by viewModel.isHapticFeedbackEnabled.collectAsStateWithLifecycle()
    val gamePlayUiDensity by viewModel.gamePlayUiDensity.collectAsStateWithLifecycle()
    val hasSeenGamePlayDensityDiscovery by
        viewModel.hasSeenGamePlayDensityDiscovery.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToGameResult) {
        if (navigateToGameResult) {
            onNavigateToResults(gameId)
            viewModel.onGameResultNavigationHandled()
        }
    }

    GamePlayScreen(
        uiState = uiState,
        isHapticFeedbackEnabled = isHapticFeedbackEnabled,
        gamePlayUiDensity = gamePlayUiDensity,
        hasSeenGamePlayDensityDiscovery = hasSeenGamePlayDensityDiscovery,
        onNavigateHome = onNavigateHome,
        onGamePlayUiDensityChange = viewModel::onGamePlayUiDensityChange,
        onGamePlayDensityDiscoverySeen = viewModel::onGamePlayDensityDiscoverySeen,
        onScore = viewModel::onScore,
        onUndo = viewModel::onUndo,
        onGoToResults = viewModel::onGoToResults,
    )
}

@Composable
private fun GamePlayScreen(
    uiState: GamePlayStateUi?,
    isHapticFeedbackEnabled: Boolean = true,
    gamePlayUiDensity: GamePlayUiDensity = GamePlayUiDensity.NORMAL,
    hasSeenGamePlayDensityDiscovery: Boolean? = true,
    onNavigateHome: () -> Unit = {},
    onGamePlayUiDensityChange: (GamePlayUiDensity) -> Unit = {},
    onGamePlayDensityDiscoverySeen: () -> Unit = {},
    onScore: (Int, ScoreCellRef, Boolean) -> Unit = { _, _, _ -> },
    onUndo: () -> Unit = {},
    onGoToResults: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isInfoSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isDensityDiscoveryDismissedForSession by rememberSaveable { mutableStateOf(false) }
    var scoreSelectionRequest by remember { mutableStateOf<ScoreSelectionRequest?>(null) }
    var celebrationId by rememberSaveable { mutableStateOf(0) }
    var celebration by remember { mutableStateOf<GamePlayCelebration?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticFeedback = LocalHapticFeedback.current
    val isCompactUi = gamePlayUiDensity == GamePlayUiDensity.COMPACT
    val screenHorizontalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 10.dp else 20.dp,
        label = "screenHorizontalPadding",
    )
    val screenVerticalPadding by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 16.dp,
        label = "screenVerticalPadding",
    )
    val screenItemSpacing by animateDpAsState(
        targetValue = if (isCompactUi) 8.dp else 16.dp,
        label = "screenItemSpacing",
    )

    if (uiState == null) {
        GamePlayMessageState(
            message = stringResource(Res.string.play_loading),
            onNavigateHome = onNavigateHome,
            modifier = modifier,
        )
        return
    }

    val settings = uiState.game.settings
    if (uiState.playerStates.isEmpty()) {
        GamePlayMessageState(
            message = stringResource(Res.string.play_empty),
            onNavigateHome = onNavigateHome,
            onShowInformation = { isInfoSheetVisible = true },
            modifier = modifier,
        )

        if (isInfoSheetVisible) {
            GamePlayInformationBottomSheet(
                settings = settings,
                gamePlayUiDensity = gamePlayUiDensity,
                onGamePlayUiDensityChange = onGamePlayUiDensityChange,
                onDismiss = { isInfoSheetVisible = false },
            )
        }
        return
    }

    val currentTurnPlayer = uiState.currentPlayerState
    val currentTurnPlayerIndex = uiState.playerStates.indexOfFirst { playerState ->
        playerState.player.userId == uiState.currentPlayer.userId
    }.coerceAtLeast(0)

    var selectedPlayerIndex by rememberSaveable(
        uiState.playerStates.size,
        currentTurnPlayerIndex,
    ) {
        mutableStateOf(currentTurnPlayerIndex.coerceIn(0, uiState.playerStates.lastIndex))
    }

    val selectedPlayer = uiState.playerStates[selectedPlayerIndex]
    val columnCount = settings.columnCount.coerceAtLeast(1)
    val isMultiColumn = columnCount > 1
    val shouldShowFinishDialog =
        uiState.status == GameStatus.ENDED &&
            uiState.game is Game.GameInProgress &&
            celebration == null
    val isActivePlayerSheet = uiState.status == GameStatus.ONGOING &&
            selectedPlayer.player.userId == currentTurnPlayer.player.userId
    val isEditable = isActivePlayerSheet
    val hasUndoableMove = uiState.playerStates.any { playerState ->
        playerState.scoreEntries.values.any { columnScores ->
            columnScores.any { it != null }
        }
    }

    val upperRows = buildUpperScoreRows()
    val lowerRows = buildMainScoreRows(settings)
    val customRows = buildCustomScoreRows(settings)
    val allRows = upperRows + lowerRows + customRows
    val allPlayableRows = allRows.filter(ScoreRowUi::countsAsTurn)

    val screenScrollState = rememberScrollState()
    val upperScrollState = rememberScrollState()
    val lowerScrollState = rememberScrollState()
    val customScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val yamBonusSnackbarMessage = stringResource(
        Res.string.play_yam_bonus_snackbar,
        settings.extraFiveOfAKindValue ?: 0,
    )

    val upperSubtotals = List(columnCount) { columnIndex ->
        selectedPlayer.sumOf(upperRows, columnIndex)
    }
    val upperBonuses = List(columnCount) { columnIndex ->
        upperBonus(upperSubtotals[columnIndex], settings)
    }
    val upperTotals = List(columnCount) { columnIndex ->
        upperSubtotals[columnIndex] + upperBonuses[columnIndex]
    }
    val lowerTotals = List(columnCount) { columnIndex ->
        selectedPlayer.sumOf(lowerRows, columnIndex)
    }
    val customTotals = List(columnCount) { columnIndex ->
        selectedPlayer.sumOf(customRows, columnIndex)
    }
    val columnSummaries = List(columnCount) { columnIndex ->
        GamePlayColumnSummary(
            columnIndex = columnIndex,
            filledCells = selectedPlayer.filledCount(allPlayableRows, columnIndex),
            totalCells = allPlayableRows.size,
            totalScore = upperTotals[columnIndex] + lowerTotals[columnIndex] + customTotals[columnIndex],
        )
    }
    val overallTotal = columnSummaries.sumOf(GamePlayColumnSummary::totalScore)

    LaunchedEffect(selectedPlayer.player.userId) {
        scoreSelectionRequest = null
    }

    fun submitScore(option: ScoreSelectionOption, cell: ScoreCellRef) {
        if (!isEditable) return
        scoreSelectionRequest = null
        if (isHapticFeedbackEnabled) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        allRows
            .firstOrNull { row -> row.key == cell.key }
            ?.celebrationTypeFor(option = option, settings = settings)
            ?.let { type ->
                celebrationId += 1
                celebration = GamePlayCelebration(
                    id = celebrationId,
                    type = type,
                    playerName = selectedPlayer.player.name,
                    score = option.score,
                )
            }
        coroutineScope.launch {
            onScore(
                option.score,
                cell,
                option.awardsExtraFiveOfAKindBonus,
            )
            if (option.awardsExtraFiveOfAKindBonus) {
                snackbarHostState.showSnackbar(yamBonusSnackbarMessage)
            }
            screenScrollState.animateScrollTo(0)
        }
    }

    fun onScoreCellClick(row: ScoreRowUi, columnIndex: Int) {
        if (!isEditable || !row.isInteractive) return
        if (selectedPlayer.valuesFor(row.key, columnCount).getOrNull(columnIndex) != null) return

        val cell = ScoreCellRef(
            key = row.key,
            columnIndex = columnIndex,
        )

        if (row.scoreOptions.isEmpty()) {
            row.fixedScore?.let { fixedScore ->
                submitScore(
                    option = ScoreSelectionOption(score = fixedScore),
                    cell = cell,
                )
            }
            return
        }

        scoreSelectionRequest = ScoreSelectionRequest(
            cell = cell,
            options = scoreSelectionOptions(
                row = row,
                columnIndex = columnIndex,
                selectedPlayer = selectedPlayer,
                settings = settings,
            ),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                GamePlayTopBar(
                    onNavigateHome = onNavigateHome,
                    onShowInformation = { isInfoSheetVisible = true },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(screenScrollState)
                    .padding(
                        horizontal = screenHorizontalPadding,
                        vertical = screenVerticalPadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(screenItemSpacing),
            ) {
                GamePlayScoreSheetOverviewCard(
                    selectedPlayer = selectedPlayer,
                    currentTurnPlayer = currentTurnPlayer,
                    columnSummaries = columnSummaries,
                    showColumnSummaries = isMultiColumn,
                    isEditable = isActivePlayerSheet,
                    overallTotal = overallTotal,
                    selectedIndex = selectedPlayerIndex,
                    playerCount = uiState.playerStates.size,
                    isCompactUi = isCompactUi,
                    onPreviousPlayer = {
                        selectedPlayerIndex = if (selectedPlayerIndex == 0) {
                            uiState.playerStates.lastIndex
                        } else {
                            selectedPlayerIndex - 1
                        }
                    },
                    onNextPlayer = {
                        selectedPlayerIndex = (selectedPlayerIndex + 1) % uiState.playerStates.size
                    },
                )

                GamePlayUpperScoreSection(
                    settings = settings,
                    rows = upperRows,
                    selectedPlayer = selectedPlayer,
                    columnCount = columnCount,
                    isMultiColumn = isMultiColumn,
                    isEditable = isEditable,
                    scrollState = upperScrollState,
                    upperSubtotals = upperSubtotals,
                    upperBonuses = upperBonuses,
                    upperTotals = upperTotals,
                    onScoreCellClick = ::onScoreCellClick,
                    scoreSelectionRequest = scoreSelectionRequest,
                    onDismissScoreSelection = { scoreSelectionRequest = null },
                    onSelectScore = ::submitScore,
                    isCompactUi = isCompactUi,
                )

                GamePlayCombinationSection(
                    rows = lowerRows,
                    selectedPlayer = selectedPlayer,
                    columnCount = columnCount,
                    isMultiColumn = isMultiColumn,
                    isEditable = isEditable,
                    lowerTotals = lowerTotals,
                    scrollState = lowerScrollState,
                    onScoreCellClick = ::onScoreCellClick,
                    scoreSelectionRequest = scoreSelectionRequest,
                    onDismissScoreSelection = { scoreSelectionRequest = null },
                    onSelectScore = ::submitScore,
                    isCompactUi = isCompactUi,
                )

                GamePlayCustomRulesSection(
                    rows = customRows,
                    selectedPlayer = selectedPlayer,
                    columnCount = columnCount,
                    isMultiColumn = isMultiColumn,
                    isEditable = isEditable,
                    customTotals = customTotals,
                    scrollState = customScrollState,
                    onScoreCellClick = ::onScoreCellClick,
                    scoreSelectionRequest = scoreSelectionRequest,
                    onDismissScoreSelection = { scoreSelectionRequest = null },
                    onSelectScore = ::submitScore,
                    isCompactUi = isCompactUi,
                )
            }
        }

        if (hasUndoableMove) {
            FloatingActionButton(
                onClick = onUndo,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 20.dp, bottom = 24.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(),
            ) {
                Icon(
                    imageVector = YamsIcons.Undo,
                    contentDescription = stringResource(Res.string.play_undo_last_move),
                )
            }
        }

        celebration?.let { currentCelebration ->
            key(currentCelebration.id) {
                GamePlayCelebrationOverlay(
                    celebration = currentCelebration,
                    modifier = Modifier.fillMaxSize(),
                    onFinished = {
                        if (celebration?.id == currentCelebration.id) {
                            celebration = null
                        }
                    },
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                )
                .zIndex(3f),
        )
    }

    val isDensityDiscoveryVisible = shouldShowGamePlayDensityDiscovery(
        hasSeenDiscovery = hasSeenGamePlayDensityDiscovery,
        hasPlayers = uiState.playerStates.isNotEmpty(),
    ) && !isDensityDiscoveryDismissedForSession

    fun dismissDensityDiscovery() {
        isDensityDiscoveryDismissedForSession = true
        onGamePlayDensityDiscoverySeen()
    }

    if (isDensityDiscoveryVisible) {
        GamePlayDensityDiscoveryOverlay(
            onDismiss = ::dismissDensityDiscovery,
            onShowInformation = {
                dismissDensityDiscovery()
                isInfoSheetVisible = true
            },
        )
    }

    if (isInfoSheetVisible) {
        GamePlayInformationBottomSheet(
            settings = settings,
            gamePlayUiDensity = gamePlayUiDensity,
            onGamePlayUiDensityChange = onGamePlayUiDensityChange,
            onDismiss = { isInfoSheetVisible = false },
        )
    }

    if (shouldShowFinishDialog) {
        GamePlayFinishedDialog(
            onGoToResults = onGoToResults,
            onUndoLastMove = {
                scoreSelectionRequest = null
                onUndo()
            },
        )
    }
}

@Composable
private fun GamePlayMessageState(
    message: String,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
    onShowInformation: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            GamePlayTopBar(
                onNavigateHome = onNavigateHome,
                onShowInformation = onShowInformation ?: {},
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GamePlayCelebrationOverlay(
    celebration: GamePlayCelebration,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismiss: Boolean = true,
    initiallyVisible: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(initiallyVisible) }
    val accentColor = when (celebration.type) {
        GamePlayCelebrationType.YAMS -> YamsTheme.colors.gold
        GamePlayCelebrationType.BIG_SCORE -> YamsTheme.colors.info
    }
    val onAccentColor = when (celebration.type) {
        GamePlayCelebrationType.YAMS -> YamsTheme.colors.onGold
        GamePlayCelebrationType.BIG_SCORE -> YamsTheme.colors.onInfo
    }
    val title = when (celebration.type) {
        GamePlayCelebrationType.YAMS -> stringResource(Res.string.play_celebration_yams_title)
        GamePlayCelebrationType.BIG_SCORE -> stringResource(
            Res.string.play_celebration_big_score_title,
        )
    }

    LaunchedEffect(celebration.id, autoDismiss) {
        isVisible = true
        if (autoDismiss) {
            delay(GamePlayCelebrationVisibleMillis)
            isVisible = false
            delay(GamePlayCelebrationExitMillis)
            onFinished()
        }
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        if (celebration.type == GamePlayCelebrationType.YAMS) {
            YamsCelebrationConfetti(modifier = Modifier.fillMaxSize())
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0.72f,
                animationSpec = tween(durationMillis = 220),
            ) + fadeIn(animationSpec = tween(durationMillis = 160)),
            exit = scaleOut(
                targetScale = 0.9f,
                animationSpec = tween(durationMillis = GamePlayCelebrationExitMillis.toInt()),
            ) + fadeOut(animationSpec = tween(durationMillis = GamePlayCelebrationExitMillis.toInt())),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                GamePlayCelebrationDiceBurst(
                    type = celebration.type,
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxSize(),
                )

                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 8.dp,
                    shadowElevation = 18.dp,
                    border = BorderStroke(
                        width = 2.dp,
                        color = accentColor.copy(alpha = 0.72f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.displaySmall,
                            color = accentColor,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(
                                Res.string.play_celebration_score_message,
                                celebration.playerName,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Surface(
                            color = accentColor,
                            contentColor = onAccentColor,
                            shape = MaterialTheme.shapes.large,
                        ) {
                            Text(
                                text = stringResource(
                                    Res.string.play_points_value,
                                    celebration.score,
                                ),
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GamePlayCelebrationDiceBurst(
    type: GamePlayCelebrationType,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val diceConfigs = remember(type) { celebrationDiceConfigs(type) }
    val transition = rememberInfiniteTransition(label = "celebrationDiceBurst")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (type == GamePlayCelebrationType.YAMS) 880 else 1_120,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "celebrationDiceSpin",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 620, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "celebrationDicePulse",
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        diceConfigs.forEach { config ->
            val direction = if (config.spinDirection < 0f) -1f else 1f
            val animationDegrees = spin * FullCircleDegrees * direction + config.phaseDegrees
            val angle = animationDegrees.toRadians()
            val animatedOffsetY = config.offsetY + (sin(angle) * 10).dp
            GamePlayCelebrationDie(
                value = config.value,
                accentColor = accentColor,
                animationDegrees = animationDegrees,
                modifier = Modifier
                    .offset(x = config.offsetX, y = animatedOffsetY)
                    .size(config.size * pulse * config.scale),
            )
        }
    }
}

@Composable
private fun GamePlayCelebrationDie(
    value: Int,
    accentColor: Color,
    animationDegrees: Float,
    modifier: Modifier = Modifier,
) {
    val dotColor = YamsTheme.colors.brown

    Canvas(modifier = modifier) {
        drawProjectedDie(
            value = value,
            accentColor = accentColor,
            dotColor = dotColor,
            animationDegrees = animationDegrees,
        )
    }
}

private fun DrawScope.drawProjectedDie(
    value: Int,
    accentColor: Color,
    dotColor: Color,
    animationDegrees: Float,
) {
    val phase = animationDegrees.toRadians()
    val cubeSide = size.minDimension * 0.58f
    val depthX = cubeSide * (0.3f + 0.05f * cos(phase).toFloat())
    val depthY = -cubeSide * (0.26f + 0.045f * sin(phase).toFloat())
    val totalWidth = cubeSide + depthX
    val totalHeight = cubeSide - depthY
    val frontTopLeft = Offset(
        x = (size.width - totalWidth) / 2f,
        y = (size.height - totalHeight) / 2f - depthY,
    )
    val a = frontTopLeft
    val b = frontTopLeft + Offset(cubeSide, 0f)
    val c = frontTopLeft + Offset(cubeSide, cubeSide)
    val d = frontTopLeft + Offset(0f, cubeSide)
    val depth = Offset(depthX, depthY)
    val backA = a + depth
    val backB = b + depth
    val backC = c + depth
    val strokeWidth = size.minDimension * 0.026f
    val frontFace = listOf(a, b, c, d)
    val topFace = listOf(backA, backB, b, a)
    val sideFace = listOf(b, backB, backC, c)

    drawFace(
        points = topFace,
        color = Color.White.copy(alpha = 0.98f),
        strokeColor = accentColor.copy(alpha = 0.28f),
        strokeWidth = strokeWidth,
    )
    drawFace(
        points = sideFace,
        color = accentColor.copy(alpha = 0.28f),
        strokeColor = accentColor.copy(alpha = 0.38f),
        strokeWidth = strokeWidth,
    )
    drawFace(
        points = frontFace,
        color = Color.White,
        strokeColor = accentColor.copy(alpha = 0.4f),
        strokeWidth = strokeWidth,
    )

    drawFacePips(
        value = nextDieFaceValue(value, 1),
        dotColor = dotColor.copy(alpha = 0.62f),
        topLeft = backA,
        topRight = backB,
        bottomLeft = a,
        dotRadius = cubeSide * 0.04f,
    )
    drawFacePips(
        value = nextDieFaceValue(value, 2),
        dotColor = dotColor.copy(alpha = 0.54f),
        topLeft = b,
        topRight = backB,
        bottomLeft = c,
        dotRadius = cubeSide * 0.04f,
    )
    drawFacePips(
        value = value,
        dotColor = dotColor,
        topLeft = a,
        topRight = b,
        bottomLeft = d,
        dotRadius = cubeSide * 0.062f,
    )
}

private fun DrawScope.drawFace(
    points: List<Offset>,
    color: Color,
    strokeColor: Color,
    strokeWidth: Float,
) {
    val path = points.toPath()
    drawPath(path = path, color = color)
    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = strokeWidth),
    )
}

private fun DrawScope.drawFacePips(
    value: Int,
    dotColor: Color,
    topLeft: Offset,
    topRight: Offset,
    bottomLeft: Offset,
    dotRadius: Float,
) {
    val left = 0.28f
    val center = 0.5f
    val right = 0.72f
    val top = 0.28f
    val bottom = 0.72f

    fun pip(horizontal: Float, vertical: Float) {
        drawCircle(
            color = dotColor,
            radius = dotRadius,
            center = pointOnFace(
                topLeft = topLeft,
                topRight = topRight,
                bottomLeft = bottomLeft,
                horizontal = horizontal,
                vertical = vertical,
            ),
        )
    }

    when (value.coerceIn(1, 6)) {
        1 -> pip(center, center)
        2 -> {
            pip(left, top)
            pip(right, bottom)
        }
        3 -> {
            pip(left, top)
            pip(center, center)
            pip(right, bottom)
        }
        4 -> {
            pip(left, top)
            pip(right, top)
            pip(left, bottom)
            pip(right, bottom)
        }
        5 -> {
            pip(left, top)
            pip(right, top)
            pip(center, center)
            pip(left, bottom)
            pip(right, bottom)
        }
        6 -> {
            pip(left, top)
            pip(right, top)
            pip(left, center)
            pip(right, center)
            pip(left, bottom)
            pip(right, bottom)
        }
    }
}

private fun List<Offset>.toPath(): Path = Path().apply {
    val firstPoint = first()
    moveTo(firstPoint.x, firstPoint.y)
    drop(1).forEach { point -> lineTo(point.x, point.y) }
    close()
}

private fun pointOnFace(
    topLeft: Offset,
    topRight: Offset,
    bottomLeft: Offset,
    horizontal: Float,
    vertical: Float,
): Offset =
    Offset(
        x = topLeft.x + (topRight.x - topLeft.x) * horizontal +
            (bottomLeft.x - topLeft.x) * vertical,
        y = topLeft.y + (topRight.y - topLeft.y) * horizontal +
            (bottomLeft.y - topLeft.y) * vertical,
    )

private fun nextDieFaceValue(value: Int, offset: Int): Int =
    ((value.coerceIn(1, 6) - 1 + offset) % 6) + 1

private fun Float.toRadians(): Double = this * PI / 180.0

private val AllFiveDiceScoreOptions: List<Int> = listOf(0) + (5..30).toList()
private val MatchingThreeDiceScoreOptions: List<Int> = listOf(0) + (1..6).map { it * 3 }
private val MatchingFourDiceScoreOptions: List<Int> = listOf(0) + (1..6).map { it * 4 }
private val ThreeOfAKindAllDiceScoreOptions: List<Int> =
    possibleAllDiceScoreOptions(minMatchingDiceCount = 3)
private val FourOfAKindAllDiceScoreOptions: List<Int> =
    possibleAllDiceScoreOptions(minMatchingDiceCount = 4)
private const val BigScoreThreshold = 30
private const val GamePlayCelebrationVisibleMillis = 2_300L
private const val GamePlayCelebrationExitMillis = 220L
private const val FullCircleDegrees = 360f

private fun possibleAllDiceScoreOptions(minMatchingDiceCount: Int): List<Int> = buildSet {
    add(0)
    for (firstDie in 1..6) {
        for (secondDie in 1..6) {
            for (thirdDie in 1..6) {
                for (fourthDie in 1..6) {
                    for (fifthDie in 1..6) {
                        val dice = listOf(firstDie, secondDie, thirdDie, fourthDie, fifthDie)
                        if (dice.groupingBy { it }.eachCount().values.any { it >= minMatchingDiceCount }) {
                            add(dice.sum())
                        }
                    }
                }
            }
        }
    }
}.toList().sorted()

@Composable
private fun buildUpperScoreRows(): List<ScoreRowUi> = listOf(
    ScoreRowUi(
        key = ScoreKey.ONES,
        badge = "1",
        label = stringResource(Res.string.play_row_ones),
        supportingText = stringResource(Res.string.play_sum_of_number, 1),
        scoreOptions = upperScoreOptions(1),
    ),
    ScoreRowUi(
        key = ScoreKey.TWOS,
        badge = "2",
        label = stringResource(Res.string.play_row_twos),
        supportingText = stringResource(Res.string.play_sum_of_number, 2),
        scoreOptions = upperScoreOptions(2),
    ),
    ScoreRowUi(
        key = ScoreKey.THREES,
        badge = "3",
        label = stringResource(Res.string.play_row_threes),
        supportingText = stringResource(Res.string.play_sum_of_number, 3),
        scoreOptions = upperScoreOptions(3),
    ),
    ScoreRowUi(
        key = ScoreKey.FOURS,
        badge = "4",
        label = stringResource(Res.string.play_row_fours),
        supportingText = stringResource(Res.string.play_sum_of_number, 4),
        scoreOptions = upperScoreOptions(4),
    ),
    ScoreRowUi(
        key = ScoreKey.FIVES,
        badge = "5",
        label = stringResource(Res.string.play_row_fives),
        supportingText = stringResource(Res.string.play_sum_of_number, 5),
        scoreOptions = upperScoreOptions(5),
    ),
    ScoreRowUi(
        key = ScoreKey.SIXES,
        badge = "6",
        label = stringResource(Res.string.play_row_sixes),
        supportingText = stringResource(Res.string.play_sum_of_number, 6),
        scoreOptions = upperScoreOptions(6),
    ),
)

@Composable
private fun buildMainScoreRows(settings: GameSettings): List<ScoreRowUi> = buildList {
    if (settings.isThreeOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.THREE_OF_A_KIND,
                label = stringResource(Res.string.play_three_of_kind),
                supportingText = scoringDescription(
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                ),
                fixedScore = fixedScore(
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                ),
                scoreOptions = selectableScoreOptions(
                    scoring = settings.threeOfAKindScoring,
                    fixedValue = settings.threeOfAKindValue,
                    allFiveDiceOptions = ThreeOfAKindAllDiceScoreOptions,
                ),
            ),
        )
    }
    if (settings.isFourOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FOUR_OF_A_KIND,
                label = stringResource(Res.string.play_four_of_kind),
                supportingText = scoringDescription(
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                ),
                fixedScore = fixedScore(
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                ),
                scoreOptions = selectableScoreOptions(
                    scoring = settings.fourOfAKindScoring,
                    fixedValue = settings.fourOfAKindValue,
                    allFiveDiceOptions = FourOfAKindAllDiceScoreOptions,
                ),
            ),
        )
    }
    if (settings.isFullHouseEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FULL_HOUSE,
                label = stringResource(Res.string.play_full),
                supportingText = stringResource(
                    Res.string.play_fixed_points,
                    settings.fullHouseValue,
                ),
                fixedScore = settings.fullHouseValue,
                scoreOptions = listOf(0, settings.fullHouseValue).distinct().sorted(),
            ),
        )
    }
    if (settings.isSmallStraightEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.SMALL_STRAIGHT,
                label = stringResource(Res.string.play_small_straight),
                supportingText = stringResource(
                    Res.string.play_fixed_points,
                    settings.smallStraightValue ?: 0,
                ),
                fixedScore = settings.smallStraightValue ?: 0,
                scoreOptions = listOf(0, settings.smallStraightValue ?: 0).distinct().sorted(),
            ),
        )
    }
    if (settings.isLargeStraightEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.LARGE_STRAIGHT,
                label = stringResource(Res.string.play_large_straight),
                supportingText = stringResource(
                    Res.string.play_fixed_points,
                    settings.largeStraightValue ?: 0,
                ),
                fixedScore = settings.largeStraightValue ?: 0,
                scoreOptions = listOf(0, settings.largeStraightValue ?: 0).distinct().sorted(),
            ),
        )
    }
    if (settings.isFiveOfAKindEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.FIVE_OF_A_KIND,
                label = stringResource(Res.string.play_yams),
                supportingText = stringResource(
                    Res.string.play_fixed_points,
                    settings.fiveOfAKindValue,
                ),
                fixedScore = settings.fiveOfAKindValue,
                scoreOptions = listOf(0, settings.fiveOfAKindValue).distinct().sorted(),
            ),
        )
    }
    if (settings.isChanceEnabled) {
        add(
            ScoreRowUi(
                key = ScoreKey.CHANCE,
                label = stringResource(Res.string.play_chance),
                supportingText = scoringDescription(
                    scoring = settings.chanceValue,
                    fixedValue = null,
                ),
                fixedScore = fixedScore(
                    scoring = settings.chanceValue,
                    fixedValue = null,
                ),
                scoreOptions = selectableScoreOptions(
                    scoring = settings.chanceValue,
                    fixedValue = null,
                    allFiveDiceOptions = AllFiveDiceScoreOptions,
                ),
            ),
        )
    }
    val extraFiveOfAKindValue = settings.extraFiveOfAKindValue
    if (settings.isExtraFiveOfAKindEnabled && extraFiveOfAKindValue != null) {
        add(
            ScoreRowUi(
                key = ScoreKey.EXTRA_FIVE_OF_A_KIND,
                label = stringResource(Res.string.play_yams_bonus),
                supportingText = stringResource(
                    Res.string.play_extra_yams_auto,
                    extraFiveOfAKindValue,
                ),
                isInteractive = false,
                countsAsTurn = false,
            ),
        )
    }
}

@Composable
private fun buildCustomScoreRows(settings: GameSettings): List<ScoreRowUi> {
    if (!settings.areCustomRulesEnabled) return emptyList()
    return settings.customGameSettings
        .filter { it.isEnabled }
        .map { rule ->
            ScoreRowUi(
                key = ScoreKey.custom(rule.id),
                label = rule.title,
                supportingText = rule.description ?: scoringDescription(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                ),
                fixedScore = fixedScore(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                ),
                scoreOptions = selectableScoreOptions(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                    allFiveDiceOptions = AllFiveDiceScoreOptions,
                ),
            )
        }
}

@Composable
private fun scoringDescription(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
): String = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> stringResource(Res.string.play_sum_5_dice)
    GameSettings.SettingsScoring.SUM_MATCHING_THREE -> stringResource(
        Res.string.play_sum_3_identical,
    )
    GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> stringResource(
        Res.string.play_sum_4_identical,
    )
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> stringResource(
        Res.string.play_fixed_points,
        fixedValue ?: 0,
    )

    null -> stringResource(Res.string.play_rule_defined_score)
}

private fun fixedScore(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
): Int? = when (scoring) {
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> fixedValue ?: 0

    else -> null
}

private fun upperScoreOptions(dieValue: Int): List<Int> =
    (0..5).map { count -> count * dieValue }

private fun selectableScoreOptions(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
    allFiveDiceOptions: List<Int>,
): List<Int> = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE -> allFiveDiceOptions
    GameSettings.SettingsScoring.SUM_MATCHING_THREE -> MatchingThreeDiceScoreOptions
    GameSettings.SettingsScoring.SUM_MATCHING_FOUR -> MatchingFourDiceScoreOptions
    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM -> listOf(0, fixedValue ?: 0).distinct().sorted()

    null -> emptyList()
}

private fun scoreSelectionOptions(
    row: ScoreRowUi,
    columnIndex: Int,
    selectedPlayer: PlayerState,
    settings: GameSettings,
): List<ScoreSelectionOption> {
    val isExtraFiveOfAKindBonusAvailable =
        settings.isExtraFiveOfAKindEnabled &&
            settings.extraFiveOfAKindValue != null &&
            row.key != ScoreKey.EXTRA_FIVE_OF_A_KIND &&
            row.key != ScoreKey.FIVE_OF_A_KIND &&
            (selectedPlayer.valuesFor(ScoreKey.FIVE_OF_A_KIND, settings.columnCount.coerceAtLeast(1))
                .getOrNull(columnIndex) ?: 0) > 0

    return row.scoreOptions.flatMap { score ->
        when {
            !isExtraFiveOfAKindBonusAvailable -> {
                listOf(ScoreSelectionOption(score = score))
            }

            fiveOfAKindDetection(row, score, settings) == FiveOfAKindDetection.CERTAIN -> {
                listOf(
                    ScoreSelectionOption(
                        score = score,
                        awardsExtraFiveOfAKindBonus = true,
                    )
                )
            }

            fiveOfAKindDetection(row, score, settings) == FiveOfAKindDetection.POSSIBLE -> {
                listOf(
                    ScoreSelectionOption(score = score),
                    ScoreSelectionOption(
                        score = score,
                        awardsExtraFiveOfAKindBonus = true,
                    ),
                )
            }

            else -> {
                listOf(ScoreSelectionOption(score = score))
            }
        }
    }
}

private fun fiveOfAKindDetection(
    row: ScoreRowUi,
    score: Int,
    settings: GameSettings,
): FiveOfAKindDetection {
    if (score <= 0) return FiveOfAKindDetection.NONE

    return when (row.key) {
        ScoreKey.ONES,
        ScoreKey.TWOS,
        ScoreKey.THREES,
        ScoreKey.FOURS,
        ScoreKey.FIVES,
        ScoreKey.SIXES -> {
            val dieValue = upperRowDieValue(row.key) ?: return FiveOfAKindDetection.NONE
            if (score == dieValue * 5) {
                FiveOfAKindDetection.CERTAIN
            } else {
                FiveOfAKindDetection.NONE
            }
        }

        ScoreKey.THREE_OF_A_KIND -> scoringFiveOfAKindDetection(
            scoring = settings.threeOfAKindScoring,
            fixedValue = settings.threeOfAKindValue,
            score = score,
            allowFixedScore = settings.jokerRule,
        )

        ScoreKey.FOUR_OF_A_KIND -> scoringFiveOfAKindDetection(
            scoring = settings.fourOfAKindScoring,
            fixedValue = settings.fourOfAKindValue,
            score = score,
            allowFixedScore = settings.jokerRule,
        )

        ScoreKey.FULL_HOUSE ->
            if (settings.jokerRule && score == settings.fullHouseValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.SMALL_STRAIGHT ->
            if (settings.jokerRule && score == settings.smallStraightValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.LARGE_STRAIGHT ->
            if (settings.jokerRule && score == settings.largeStraightValue) {
                FiveOfAKindDetection.POSSIBLE
            } else {
                FiveOfAKindDetection.NONE
            }

        ScoreKey.CHANCE -> scoringFiveOfAKindDetection(
            scoring = settings.chanceValue,
            fixedValue = null,
            score = score,
            allowFixedScore = false,
        )

        else -> settings.customGameSettings
            .firstOrNull { ScoreKey.custom(it.id) == row.key }
            ?.let { rule ->
                scoringFiveOfAKindDetection(
                    scoring = rule.scoring,
                    fixedValue = rule.value,
                    score = score,
                    allowFixedScore = true,
                )
            }
            ?: FiveOfAKindDetection.NONE
    }
}

private fun scoringFiveOfAKindDetection(
    scoring: GameSettings.SettingsScoring?,
    fixedValue: Int?,
    score: Int,
    allowFixedScore: Boolean,
): FiveOfAKindDetection = when (scoring) {
    GameSettings.SettingsScoring.SUM_ALL_FIVE_DICE ->
        if (score in AllFiveDiceScoreOptions && score % 5 == 0) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.SUM_MATCHING_THREE ->
        if (score in MatchingThreeDiceScoreOptions) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.SUM_MATCHING_FOUR ->
        if (score in MatchingFourDiceScoreOptions) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    GameSettings.SettingsScoring.FIXED,
    GameSettings.SettingsScoring.FIXED_CUSTOM ->
        if (allowFixedScore && score == fixedValue) {
            FiveOfAKindDetection.POSSIBLE
        } else {
            FiveOfAKindDetection.NONE
        }

    null -> FiveOfAKindDetection.NONE
}

private fun upperRowDieValue(key: ScoreKey): Int? = when (key) {
    ScoreKey.ONES -> 1
    ScoreKey.TWOS -> 2
    ScoreKey.THREES -> 3
    ScoreKey.FOURS -> 4
    ScoreKey.FIVES -> 5
    ScoreKey.SIXES -> 6
    else -> null
}

private fun upperBonus(subtotal: Int, settings: GameSettings): Int {
    if (!settings.isUpperBonusEnabled) return 0
    return if (subtotal >= settings.upperBonusThreshold) settings.upperBonusValue else 0
}

@Composable
internal fun bonusStatusText(
    subtotal: Int,
    settings: GameSettings,
): String {
    val bonus = upperBonus(subtotal, settings)
    if (bonus > 0) return stringResource(Res.string.play_bonus_applied_status, bonus)
    val remaining = (settings.upperBonusThreshold - subtotal).coerceAtLeast(0)
    return stringResource(Res.string.play_bonus_remaining_status, remaining)
}

internal fun PlayerState.valueFor(key: ScoreKey): Int? =
    valueAt(key = key, columnIndex = 0)

internal fun PlayerState.valuesFor(
    key: ScoreKey,
    columnCount: Int,
): List<Int?> = List(columnCount) { columnIndex ->
    valueAt(key = key, columnIndex = columnIndex)
}

private fun PlayerState.sumOf(
    rows: List<ScoreRowUi>,
    columnIndex: Int,
): Int = rows.sumOf { row ->
    valueAt(key = row.key, columnIndex = columnIndex) ?: 0
}

private fun PlayerState.filledCount(
    rows: List<ScoreRowUi>,
    columnIndex: Int,
): Int = rows.count { row ->
    valueAt(key = row.key, columnIndex = columnIndex) != null
}

private fun PlayerState.valueAt(
    key: ScoreKey,
    columnIndex: Int,
): Int? = when (key) {
    ScoreKey.EXTRA_FIVE_OF_A_KIND -> extraFiveOfAKindScores.getOrElse(columnIndex) { 0 }
    else -> scoreEntries[key]?.getOrNull(columnIndex)
}

internal data class ScoreRowUi(
    val key: ScoreKey,
    val label: String,
    val supportingText: String,
    val badge: String? = null,
    val fixedScore: Int? = null,
    val scoreOptions: List<Int> = emptyList(),
    val isInteractive: Boolean = true,
    val countsAsTurn: Boolean = true,
)

internal data class ScoreSelectionRequest(
    val cell: ScoreCellRef,
    val options: List<ScoreSelectionOption>,
)

internal data class ScoreSelectionOption(
    val score: Int,
    val awardsExtraFiveOfAKindBonus: Boolean = false,
)

private data class GamePlayCelebration(
    val id: Int,
    val type: GamePlayCelebrationType,
    val playerName: String,
    val score: Int,
)

private enum class GamePlayCelebrationType {
    YAMS,
    BIG_SCORE,
}

private data class CelebrationDiceConfig(
    val value: Int,
    val offsetX: Dp,
    val offsetY: Dp,
    val size: Dp,
    val baseRotationDegrees: Float,
    val spinDirection: Float,
    val phaseDegrees: Float,
    val scale: Float,
)

private fun celebrationDiceConfigs(type: GamePlayCelebrationType): List<CelebrationDiceConfig> =
    when (type) {
        GamePlayCelebrationType.YAMS -> listOf(
            CelebrationDiceConfig(6, (-132).dp, (-116).dp, 76.dp, -18f, 0.7f, 0f, 1.05f),
            CelebrationDiceConfig(5, 122.dp, (-112).dp, 70.dp, 16f, -0.8f, 72f, 0.98f),
            CelebrationDiceConfig(4, (-152).dp, 116.dp, 64.dp, 24f, -0.65f, 144f, 0.9f),
            CelebrationDiceConfig(3, 150.dp, 118.dp, 66.dp, -22f, 0.76f, 216f, 0.92f),
            CelebrationDiceConfig(6, 0.dp, (-176).dp, 58.dp, 8f, 0.92f, 288f, 0.86f),
        )

        GamePlayCelebrationType.BIG_SCORE -> listOf(
            CelebrationDiceConfig(6, (-118).dp, (-104).dp, 62.dp, -14f, 0.55f, 0f, 0.94f),
            CelebrationDiceConfig(5, 112.dp, (-96).dp, 58.dp, 18f, -0.62f, 120f, 0.88f),
            CelebrationDiceConfig(4, 0.dp, 128.dp, 60.dp, -8f, 0.64f, 240f, 0.9f),
        )
    }

private fun ScoreRowUi.celebrationTypeFor(
    option: ScoreSelectionOption,
    settings: GameSettings,
): GamePlayCelebrationType? {
    if (option.score <= 0) return null

    return when {
        isYamsCelebration(option = option, settings = settings) -> GamePlayCelebrationType.YAMS
        option.score >= BigScoreThreshold -> GamePlayCelebrationType.BIG_SCORE
        else -> null
    }
}

private fun ScoreRowUi.isYamsCelebration(
    option: ScoreSelectionOption,
    settings: GameSettings,
): Boolean =
    option.awardsExtraFiveOfAKindBonus ||
        key == ScoreKey.FIVE_OF_A_KIND ||
        fiveOfAKindDetection(
            row = this,
            score = option.score,
            settings = settings,
        ) == FiveOfAKindDetection.CERTAIN

private enum class FiveOfAKindDetection {
    NONE,
    POSSIBLE,
    CERTAIN,
}

@YamsStoreScreenshotPreviews
@Composable
private fun GamePlayScreenPreview() {
    GamePlayTwoColumnsStoreScreenshotContent()
}

@Composable
public fun GamePlayTwoColumnsStoreScreenshotContent() {
    YamsTheme {
        GamePlayScreen(
            uiState = gamePlayPreviewUiState(columnCount = 2),
        )
    }
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun GamePlayScreenSingleColumnPreview() {
    GamePlaySingleColumnStoreScreenshotContent()
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun GamePlayDensityDiscoveryPreview() {
    YamsTheme {
        GamePlayScreen(
            uiState = gamePlayPreviewUiState(columnCount = 1),
            hasSeenGamePlayDensityDiscovery = false,
        )
    }
}

@Composable
public fun GamePlaySingleColumnStoreScreenshotContent() {
    YamsTheme {
        GamePlayScreen(
            uiState = gamePlayPreviewUiState(columnCount = 1),
        )
    }
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun GamePlayCelebrationOverlayPreview() {
    YamsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            GamePlayCelebrationOverlay(
                celebration = GamePlayCelebration(
                    id = 1,
                    type = GamePlayCelebrationType.YAMS,
                    playerName = "Lina",
                    score = 50,
                ),
                modifier = Modifier.fillMaxSize(),
                autoDismiss = false,
                initiallyVisible = true,
                onFinished = {},
            )
        }
    }
}
