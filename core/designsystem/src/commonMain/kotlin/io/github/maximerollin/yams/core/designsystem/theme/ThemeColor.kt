package io.github.maximerollin.yams.core.designsystem.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Yams Brand Colors - Warm & Cozy
internal val YamsGold = Color(0xFFDEBA48) // #deba48
internal val YamsBrown = Color(0xFF3A322F) // #3A322F

// Warm Paper Palette
internal val PaperCream = Color(0xFFFAF6F0) // Main background - warm cream
internal val PaperWhite = Color(0xFFFFFBF5) // Surface - slightly warmer white
internal val PaperBeige = Color(0xFFF5EFE7) // Cards/elevated surfaces
internal val PaperTan = Color(0xFFE8DFD2) // Dividers/borders

// Gold Variants - Warm tones
internal val YamsGold50 = Color(0xFFFDF9EB)
internal val YamsGold100 = Color(0xFFFAF2D7)
internal val YamsGold200 = Color(0xFFF5E5AF)
internal val YamsGold300 = Color(0xFFF0D887)
internal val YamsGold400 = Color(0xFFEBCB5F)
internal val YamsGold500 = YamsGold // Primary
internal val YamsGold600 = Color(0xFFB29539)
internal val YamsGold700 = Color(0xFF86702B)
internal val YamsGold800 = Color(0xFF594A1C)
internal val YamsGold900 = Color(0xFF2D250E)

// Brown Variants - Rich and warm
internal val YamsBrown50 = Color(0xFFF8F6F4)
internal val YamsBrown100 = Color(0xFFF0EDE8)
internal val YamsBrown200 = Color(0xFFE1DBD1)
internal val YamsBrown300 = Color(0xFFD2C9BA)
internal val YamsBrown400 = Color(0xFFC3B7A3)
internal val YamsBrown500 = Color(0xFFB4A58C)
internal val YamsBrown600 = Color(0xFF8C7F6E)
internal val YamsBrown700 = Color(0xFF695F52)
internal val YamsBrown800 = YamsBrown // Secondary - deep brown
internal val YamsBrown900 = Color(0xFF1D1916)

// Warm Neutral Colors
internal val WarmGray50 = Color(0xFFFCFAF7)
internal val WarmGray100 = Color(0xFFF7F4F0)
internal val WarmGray200 = Color(0xFFEEE9E3)
internal val WarmGray300 = Color(0xFFE0D8CE)
internal val WarmGray400 = Color(0xFFC4BAB0)
internal val WarmGray500 = Color(0xFFA89C92)
internal val WarmGray600 = Color(0xFF8A7F75)
internal val WarmGray700 = Color(0xFF6D6258)
internal val WarmGray800 = Color(0xFF4A4239)
internal val WarmGray900 = Color(0xFF2B2620)

// Semantic Colors - Slightly warmer tones
internal val YamsSuccess = Color(0xFF5CB85C) // Warmer green
internal val YamsWarning = Color(0xFFF0AD4E) // Warmer orange
internal val YamsError = Color(0xFFD9534F) // Warmer red
internal val YamsInfo = Color(0xFF5BC0DE) // Warmer blue

// Warm Paper-Style Color Scheme
internal val YamsColorScheme = lightColorScheme(
    primary = YamsGold500,
    onPrimary = Color.White,
    primaryContainer = YamsGold100,
    onPrimaryContainer = YamsGold900,
    
    secondary = YamsBrown700,
    onSecondary = PaperWhite,
    secondaryContainer = YamsBrown200,
    onSecondaryContainer = YamsBrown900,
    
    tertiary = YamsInfo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5F9),
    onTertiaryContainer = Color(0xFF0D3C55),
    
    background = PaperCream, // Warm cream background
    onBackground = WarmGray900,
    
    surface = PaperWhite, // Slightly warm white for surfaces
    onSurface = WarmGray900,
    surfaceVariant = PaperBeige, // Beige for cards
    onSurfaceVariant = WarmGray700,
    
    outline = WarmGray400,
    outlineVariant = PaperTan,
    
    error = YamsError,
    onError = Color.White,
    errorContainer = Color(0xFFFDECEB),
    onErrorContainer = Color(0xFF8B0000)
)
