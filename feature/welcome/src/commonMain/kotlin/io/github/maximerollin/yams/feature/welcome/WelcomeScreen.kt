package io.github.maximerollin.yams.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
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
    isAnimatedBackgroundEnabled: Boolean = true,
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
                modifier = Modifier.fillMaxSize(),
                isAnimationEnabled = isAnimatedBackgroundEnabled,
                randomSeed = if (isAnimatedBackgroundEnabled) null else 42,
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

                // Feature list
                FeatureList {
                    FeatureRow(
                        icon = YamsIcons.RocketLaunch,
                        text = stringResource(Res.string.welcome_feature_score_sheet)
                    )
                    FeatureDivider()
                    FeatureRow(
                        icon = YamsIcons.History,
                        text = stringResource(Res.string.welcome_feature_history)
                    )
                    FeatureDivider()
                    FeatureRow(
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
private fun FeatureList(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = YamsTheme.colors.gold
        )

        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.56f),
        modifier = Modifier.padding(start = 52.dp, end = 16.dp)
    )
}

@YamsStoreScreenshotPreviews
@Composable
private fun WelcomeScreenPreview() {
    WelcomeStoreScreenshotContent()
}

@Composable
public fun WelcomeStoreScreenshotContent() {
    YamsTheme {
        WelcomeScreen(
            onNavigateToNewGame = {},
            isAnimatedBackgroundEnabled = false,
        )
    }
}
