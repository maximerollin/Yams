package io.github.maximerollin.yams.feature.welcome

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.YamsCard
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.icon.Groups
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.RocketLaunch
import io.github.maximerollin.yams.core.designsystem.icon.Strategy
import io.github.maximerollin.yams.core.designsystem.icon.Timeline
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WelcomeRoute(
    onNavigateToNewGame: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WelcomeScreen(
        modifier = modifier,
        onNavigateToNewGame = onNavigateToNewGame,
        onNavigateToPlayers = onNavigateToPlayers,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToStatistics = onNavigateToStatistics,
    )
}

@Composable
internal fun WelcomeScreen(
    onNavigateToNewGame: () -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header avec gradient
            WelcomeHeader()
            
            // Contenu principal
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Bouton principal - Nouvelle partie
                YamsPrimaryButton(
                    onClick = onNavigateToNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Icon(
                        imageVector = YamsIcons.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nouvelle Partie",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Grille des fonctionnalités
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(
                        title = "Joueurs",
                        subtitle = "Gérer les joueurs",
                        icon = YamsIcons.Groups,
                        onClick = onNavigateToPlayers,
                        modifier = Modifier.weight(1f)
                    )
                    
                    FeatureCard(
                        title = "Historique",
                        subtitle = "Parties jouées",
                        icon = YamsIcons.History,
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCard(
                        title = "Statistiques",
                        subtitle = "Performances",
                        icon = YamsIcons.Timeline,
                        onClick = onNavigateToStatistics,
                        modifier = Modifier.weight(1f)
                    )
                    
                    FeatureCard(
                        title = "Règles",
                        subtitle = "Comment jouer",
                        icon = YamsIcons.Strategy,
                        onClick = { /* TODO: Navigation vers les règles */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Section "À propos du Yams"
                AboutYamsSection()
                
                // Espace en bas
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun WelcomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        YamsTheme.colors.gold.copy(alpha = 0.8f),
                        YamsTheme.colors.gold.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icône de dés
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = YamsIcons.Trophy,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = YamsTheme.colors.gold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Yams",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = YamsTheme.colors.brown
            )
            
            Text(
                text = "Le jeu de dés incontournable",
                style = MaterialTheme.typography.titleMedium,
                color = YamsTheme.colors.brown.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = YamsTheme.colors.gold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AboutYamsSection() {
    YamsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "À propos du Yams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = YamsTheme.colors.gold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Le Yams (aussi appelé Yahtzee) est un jeu de dés passionnant où l'objectif est de réaliser des combinaisons pour marquer le maximum de points.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• 5 dés, 13 catégories, 3 lancers par tour\n• Stratégie et chance se mélangent\n• Parfait pour jouer en famille ou entre amis",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    YamsTheme {
        WelcomeScreen(
            onNavigateToNewGame = {},
            onNavigateToPlayers = {},
            onNavigateToHistory = {},
            onNavigateToStatistics = {}
        )
    }
}
