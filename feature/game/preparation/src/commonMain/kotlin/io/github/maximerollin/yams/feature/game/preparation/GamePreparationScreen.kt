package io.github.maximerollin.yams.feature.game.preparation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun GamePreparationRoute(
    modifier: Modifier = Modifier,
) {
    GamePreparationScreen(
        modifier = modifier,
    )
}

@Composable
internal fun GamePreparationScreen(
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
                text = "Préparez-vous !",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = YamsTheme.colors.brown,
                textAlign = TextAlign.Center
            )


        }
    }
}

@Preview
@Composable
private fun GamePreparationScreenPreview() {
    YamsTheme {
        GamePreparationScreen(
        )
    }
}


