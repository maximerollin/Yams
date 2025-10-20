package io.github.maximerollin.yams.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
public data class YamsColor(
    // Brand Colors
    val gold: Color = Color.Unspecified,
    val onGold: Color = Color.Unspecified,
    val brown: Color = Color.Unspecified,
    val onBrown: Color = Color.Unspecified,
    
    // Ranking Colors
    val firstPlace: Color = Color.Unspecified,
    val onFirstPlace: Color = Color.Unspecified,
    val secondPlace: Color = Color.Unspecified,
    val onSecondPlace: Color = Color.Unspecified,
    val thirdPlace: Color = Color.Unspecified,
    val onThirdPlace: Color = Color.Unspecified,
    
    // Semantic Colors
    val success: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
    val onWarning: Color = Color.Unspecified,
    val info: Color = Color.Unspecified,
    val onInfo: Color = Color.Unspecified,
    
    // Dice Colors
    val diceBackground: Color = Color.Unspecified,
    val diceSelected: Color = Color.Unspecified,
    val diceDots: Color = Color.Unspecified,
)

public val YamsCustomColors: YamsColor = YamsColor(
    // Brand Colors
    gold = YamsGold,
    onGold = Color.White,
    brown = YamsBrown800,
    onBrown = Color.White,
    
    // Ranking Colors
    firstPlace = Color(0xFFFFD700), // Gold
    onFirstPlace = YamsBrown900,
    secondPlace = Color(0xFFC0C0C0), // Silver
    onSecondPlace = YamsBrown900,
    thirdPlace = Color(0xFFCD7F32), // Bronze
    onThirdPlace = Color.White,
    
    // Semantic Colors
    success = YamsSuccess,
    onSuccess = Color.White,
    warning = YamsWarning,
    onWarning = Color.White,
    info = YamsInfo,
    onInfo = Color.White,
    
    // Dice Colors
    diceBackground = PaperWhite,
    diceSelected = YamsGold200,
    diceDots = YamsBrown700,
)

public val LocalYamsColor: ProvidableCompositionLocal<YamsColor> =
    staticCompositionLocalOf { YamsCustomColors }

public val YamsTheme.colors: YamsColor
    @Composable
    @ReadOnlyComposable
    get() = LocalYamsColor.current
