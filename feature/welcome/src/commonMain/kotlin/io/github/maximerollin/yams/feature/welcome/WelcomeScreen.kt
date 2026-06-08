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
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yams.feature.welcome.generated.resources.*
import yams.core.ui.generated.resources.app_icon
import yams.core.ui.generated.resources.Res as CoreUiRes

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
                        painter = painterResource(CoreUiRes.drawable.app_icon),
                        contentDescription = stringResource(Res.string.welcome_app_icon_cd),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                    )

                    Text(
                        text = stringResource(Res.string.welcome_title),
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
                    text = stringResource(Res.string.welcome_subtitle),
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
                        text = stringResource(Res.string.welcome_feature_score_sheet)
                    )
                    FeatureCard(
                        icon = YamsIcons.History,
                        text = stringResource(Res.string.welcome_feature_history)
                    )
                    FeatureCard(
                        icon = YamsIcons.Trophy,
                        text = stringResource(Res.string.welcome_feature_stats)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Call to action button
                YamsPrimaryButton(
                    onClick = onNavigateToNewGame,
                    text = stringResource(Res.string.welcome_cta)
                )

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

@Preview(uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Preview(name = "light")
@Composable
private fun WelcomeScreenPreview() {
    YamsTheme {
        WelcomeScreen(
            onNavigateToNewGame = {},
        )
    }
}
