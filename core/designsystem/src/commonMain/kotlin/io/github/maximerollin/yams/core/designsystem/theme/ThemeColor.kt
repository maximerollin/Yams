package io.github.maximerollin.yams.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Yams Brand Colors
internal val YamsGold = Color(0xFFDEBA48) // #deba48
internal val YamsBrown = Color(0xFF3A322F) // #3A322F

// Light Theme Colors
internal val YamsGold50 = Color(0xFFFDF8E8)
internal val YamsGold100 = Color(0xFFFAF0D1)
internal val YamsGold200 = Color(0xFFF5E1A3)
internal val YamsGold300 = Color(0xFFF0D275)
internal val YamsGold400 = Color(0xFFEBC347)
internal val YamsGold500 = YamsGold // Primary
internal val YamsGold600 = Color(0xFFB29539)
internal val YamsGold700 = Color(0xFF86702B)
internal val YamsGold800 = Color(0xFF594A1C)
internal val YamsGold900 = Color(0xFF2D250E)

// Brown Variants
internal val YamsBrown50 = Color(0xFFF7F6F5)
internal val YamsBrown100 = Color(0xFFEFEDEB)
internal val YamsBrown200 = Color(0xFFDFDBD7)
internal val YamsBrown300 = Color(0xFFCFC9C3)
internal val YamsBrown400 = Color(0xFFBFB7AF)
internal val YamsBrown500 = Color(0xFFAFA59B)
internal val YamsBrown600 = Color(0xFF8C7F73)
internal val YamsBrown700 = Color(0xFF695F55)
internal val YamsBrown800 = YamsBrown // Secondary
internal val YamsBrown900 = Color(0xFF1D1916)

// Neutral Colors
internal val YamsGray50 = Color(0xFFFAFAFA)
internal val YamsGray100 = Color(0xFFF5F5F5)
internal val YamsGray200 = Color(0xFFEEEEEE)
internal val YamsGray300 = Color(0xFFE0E0E0)
internal val YamsGray400 = Color(0xFFBDBDBD)
internal val YamsGray500 = Color(0xFF9E9E9E)
internal val YamsGray600 = Color(0xFF757575)
internal val YamsGray700 = Color(0xFF616161)
internal val YamsGray800 = Color(0xFF424242)
internal val YamsGray900 = Color(0xFF212121)

// Semantic Colors
internal val YamsSuccess = Color(0xFF4CAF50)
internal val YamsWarning = Color(0xFFFF9800)
internal val YamsError = Color(0xFFF44336)
internal val YamsInfo = Color(0xFF2196F3)

// Light Color Scheme
internal val YamsLightColors = lightColorScheme(
    primary = YamsGold500,
    onPrimary = Color.White,
    primaryContainer = YamsGold100,
    onPrimaryContainer = YamsGold900,
    
    secondary = YamsBrown800,
    onSecondary = Color.White,
    secondaryContainer = YamsBrown200,
    onSecondaryContainer = YamsBrown900,
    
    tertiary = YamsInfo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE3F2FD),
    onTertiaryContainer = Color(0xFF0D47A1),
    
    background = YamsGray50,
    onBackground = YamsGray900,
    
    surface = Color.White,
    onSurface = YamsGray900,
    surfaceVariant = YamsGray100,
    onSurfaceVariant = YamsGray700,
    
    outline = YamsGray400,
    outlineVariant = YamsGray200,
    
    error = YamsError,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

// Dark Color Scheme
internal val DarkColors = darkColorScheme(
    primary = YamsGold400,
    onPrimary = YamsGold900,
    primaryContainer = YamsGold700,
    onPrimaryContainer = YamsGold100,
    
    secondary = YamsBrown400,
    onSecondary = YamsBrown900,
    secondaryContainer = YamsBrown700,
    onSecondaryContainer = YamsBrown100,
    
    tertiary = Color(0xFF90CAF9),
    onTertiary = Color(0xFF0D47A1),
    tertiaryContainer = Color(0xFF1976D2),
    onTertiaryContainer = Color(0xFFE3F2FD),
    
    background = YamsGray900,
    onBackground = YamsGray100,
    
    surface = YamsGray800,
    onSurface = YamsGray100,
    surfaceVariant = YamsGray700,
    onSurfaceVariant = YamsGray300,
    
    outline = YamsGray500,
    outlineVariant = YamsGray700,
    
    error = Color(0xFFEF5350),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2)
)
