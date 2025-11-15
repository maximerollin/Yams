package io.github.maximerollin.yams.feature.game.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun GameCreationRoute(
    onNavigateToPreparation: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameCreationScreen(
        modifier = modifier,
        onNavigateToPreparation = onNavigateToPreparation,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
internal fun GameCreationScreen(
    onNavigateToPreparation: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Créer une partie",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Configurez votre partie de Yams",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // TODO: Add game configuration options here
            // - Number of players
            // - Player names
            // - Game mode (classic, variant, etc.)
            
            Text(
                text = "Configuration à venir...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue button
            YamsPrimaryButton(
                onClick = onNavigateToPreparation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Continuer",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Back button (secondary action)
            YamsPrimaryButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Retour",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun GameCreationScreenPreview() {
    YamsTheme {
        GameCreationScreen(
            onNavigateToPreparation = {},
            onNavigateBack = {},
        )
    }
}


