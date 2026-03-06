package io.github.maximerollin.yams.feature.game.preparation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimarySmallButton
import io.github.maximerollin.yams.core.designsystem.icon.Strategy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.GameId
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.User
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationBottomBar
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationSection
import io.github.maximerollin.yams.feature.game.preparation.components.GamePreparationSettings
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

    LaunchedEffect(gamePreparationUiState.navigateToGame) {
        gamePreparationUiState.navigateToGame?.let { gameId ->
            onNavigateGame(gameId)
        }
    }

    GamePreparationScreen(
        gamePreparationUiState = gamePreparationUiState,
        usersState = usersState,
        onNavigateBack = onNavigateBack,
        onCreateGame = viewModel::createGame,
        onToggleIsUserOrderRandomized = viewModel::onToggleIsUserOrderRandomized,
        onToggleGameSettings = viewModel::onToggleGameSettings,
        onUpdateGameSettings = viewModel::onUpdateGameSettings,
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
    onToggleGameSettings: (GameSettings.RuleSet) -> Unit,
    onUpdateGameSettings: (GameSettings) -> Unit,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            GamePreparationUserOrder(
                users = usersState,
                isUserOrderRandomized = gamePreparationUiState.isUserOrderRandomized,
                onToggleIsUserOrderRandomized = onToggleIsUserOrderRandomized,
                onOrderUser = onOrderUser,
            )

            GamePreparationSettings(
                uiState = gamePreparationUiState.gameSettings,
                onToggleGameSettings = onToggleGameSettings,
                onUpdateGameSettings = onUpdateGameSettings,
            )

            GamePreparationStrategyTips(
                selectedRuleSet = gamePreparationUiState.gameSettings.ruleSet,
            )
        }
    }
}

@Composable
private fun GamePreparationStrategyTips(
    selectedRuleSet: GameSettings.RuleSet,
    modifier: Modifier = Modifier,
) {
    val tips = when (selectedRuleSet) {
        GameSettings.RuleSet.YAMS -> listOf(
            "Vise rapidement une ligne forte dans la section supérieure pour sécuriser le bonus.",
            "Quand un lancer est moyen, garde une paire flexible pour viser Full ou brelan au tour suivant.",
            "Ne force pas le Yams trop tôt: maximise d'abord les points stables.",
            "Utilise la chance comme filet de sécurité quand aucune combinaison forte ne sort.",
            "Une fois le bonus du haut bien engagé, vise surtout les grosses cases (Full, suites, Yams).",
        )

        GameSettings.RuleSet.YAHTZEE -> listOf(
            "Priorise les combinaisons régulières en début de partie pour limiter les zéros.",
            "Un carré faible peut parfois rapporter moins qu'une suite: compare avant de valider.",
            "Garde un plan B sur chaque lancer pour éviter de bloquer une ligne clé.",
            "Sécurise d'abord les combinaisons faciles pour garder les choix ouverts en fin de partie.",
            "Quand deux options se valent, choisis celle qui laisse le plus de flexibilité au lancer suivant.",
        )

        GameSettings.RuleSet.CUSTOM -> listOf(
            "Commence par les règles à valeur fixe pour sécuriser un socle de points.",
            "Si une règle custom est risquée, attends un lancer naturellement favorable.",
            "Répartis les prises de risque entre les joueurs pour garder une partie serrée.",
            "Repère les règles custom les plus rentables et garde-les pour les lancers vraiment favorables.",
            "Quand une règle est rarement réalisable, n'hésite pas à la sacrifier tôt pour mieux gérer la suite.",
        )
    }
    var currentTipIndex by remember(selectedRuleSet) { mutableStateOf(0) }
    var tipsDirection by remember(selectedRuleSet) { mutableStateOf(1) }

    GamePreparationSection(
        title = "Astuces express",
        subtitle = "Un conseil rapide pour bien démarrer la partie.",
        icon = YamsIcons.Strategy,
        accentColor = MaterialTheme.colorScheme.tertiary,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Astuce ${currentTipIndex + 1}/${tips.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                YamsPrimarySmallButton(
                    onClick = {
                        tipsDirection = -1
                        currentTipIndex = (currentTipIndex - 1).let { index ->
                            if (index < 0) tips.lastIndex else index
                        }
                    },
                    text = "Préc."
                )
                YamsPrimarySmallButton(
                    onClick = {
                        tipsDirection = 1
                        currentTipIndex = (currentTipIndex + 1) % tips.size
                    },
                    text = "Suiv."
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedContent(
            targetState = currentTipIndex,
            transitionSpec = {
                when (tipsDirection) {
                    1 -> (slideInHorizontally { it / 2 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 2 } + fadeOut())

                    else -> (slideInHorizontally { -it / 2 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 2 } + fadeOut())
                }
            },
            label = "tips_carousel"
        ) { tipIndex ->
            PreparationTipItem(
                step = tipIndex + 1,
                tip = tips[tipIndex],
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(tips.size) { index ->
                val isActive = index == currentTipIndex
                Box(
                    modifier = Modifier
                        .size(if (isActive) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            }
                        )
                )
                if (index < tips.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
private fun PreparationTipItem(
    step: Int,
    tip: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
            ) {
                Text(
                    text = step.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }

            Text(
                text = tip,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview
@Composable
private fun GamePreparationScreenRandomPreview() {
    YamsTheme {
        GamePreparationScreen(
            gamePreparationUiState = GamePreparationUiState(
                isUserOrderRandomized = true,
                gameSettings = GameSettings.YamsSettings()
            ),
            usersState = UserMocks.users.subList(0, 3),
            onNavigateBack = {},
            onCreateGame = {},
            onToggleIsUserOrderRandomized = {},
            onToggleGameSettings = {},
            onUpdateGameSettings = {},
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
            onToggleGameSettings = {},
            onUpdateGameSettings = {},
            onOrderUser = { _, _ -> {} }
        )
    }
}
