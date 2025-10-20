package io.github.maximerollin.yams.feature.welcome

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.icon.History
import io.github.maximerollin.yams.core.designsystem.icon.RocketLaunch
import io.github.maximerollin.yams.core.designsystem.icon.Trophy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.ui.AnimatedDiceBackground
import yams.core.ui.generated.resources.app_Icon
import yams.core.ui.generated.resources.Res as CoreUiRes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WelcomeRoute(
    onNavigateToNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WelcomeScreen(
        modifier = modifier,
        onNavigateToNewGame = onNavigateToNewGame,
    )
}

@Composable
internal fun WelcomeScreen(
    onNavigateToNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Animated dice background
            AnimatedDiceBackground(
                diceColor = YamsTheme.colors.brown,
                modifier = Modifier.fillMaxSize()
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(120.dp))
            
            // Icon + Title Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {

                Image(
                    painter = painterResource(CoreUiRes.drawable.app_Icon),
                    contentDescription = "Yams Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                )
                
                Text(
                    text = "Yams Score",
                    fontSize = 28.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    color = YamsTheme.colors.brown
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Parce que chaque partie mérite d'être mémorable !",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.2).sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Feature Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    icon = YamsIcons.RocketLaunch,
                    text = "Feuille de score interactive"
                )
                FeatureCard(
                    icon = YamsIcons.History,
                    text = "Historique de toutes vos parties"
                )
                FeatureCard(
                    icon = YamsIcons.Trophy,
                    text = "Statistiques et records personnels"
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Call to action button
            YamsPrimaryButton(
                onClick = onNavigateToNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Commencer ma première partie",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = YamsTheme.colors.gold
            )
            
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.2).sp,
                color = MaterialTheme.colorScheme.onSurface
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
        )
    }
}
