package io.github.maximerollin.yams.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun YamsAvatar(
    avatarUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    borderColor: Color? = null,
    borderWidth: Dp = 2.dp,
    fallbackText: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (borderColor != null) {
                    Modifier.border(
                        border = BorderStroke(borderWidth, borderColor),
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null) {
//            AsyncImage(
//                model = avatarUrl,
//                contentDescription = contentDescription,
//                modifier = Modifier
//                    .size(size - if (borderColor != null) borderWidth * 2 else 0.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
        } else {
            // Fallback avec initiales ou icône
            Box(
                modifier = Modifier
                    .size(size - if (borderColor != null) borderWidth * 2 else 0.dp)
                    .clip(CircleShape)
                    .background(YamsTheme.colors.gold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fallbackText?.take(2)?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = YamsTheme.colors.gold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
public fun YamsPlayerAvatar(
    avatarUrl: String?,
    playerName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isSelected: Boolean = false,
    isWinner: Boolean = false
) {
    val borderColor = when {
        isWinner -> YamsTheme.colors.firstPlace
        isSelected -> YamsTheme.colors.gold
        else -> null
    }
    
    val fallbackText = playerName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
    
    YamsAvatar(
        avatarUrl = avatarUrl,
        contentDescription = "Avatar de $playerName",
        modifier = modifier,
        size = size,
        borderColor = borderColor,
        borderWidth = if (borderColor != null) 3.dp else 0.dp,
        fallbackText = fallbackText
    )
}

@Composable
public fun YamsAvatarLarge(
    avatarUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    borderColor: Color? = YamsTheme.colors.gold
) {
    YamsAvatar(
        avatarUrl = avatarUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = 120.dp,
        borderColor = borderColor,
        borderWidth = 4.dp
    )
}

@Composable
public fun YamsAvatarSmall(
    avatarUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fallbackText: String? = null
) {
    YamsAvatar(
        avatarUrl = avatarUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        size = 32.dp,
        borderColor = null,
        fallbackText = fallbackText
    )
}

@Preview
@Composable
private fun YamsAvatarPreview() {
    YamsTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            // Avatar avec image DiceBear
            YamsPlayerAvatar(
                avatarUrl = "https://api.dicebear.com/9.x/adventurer/svg?seed=maxime&backgroundColor=fdf8e8",
                playerName = "Maxime Rollin",
                isSelected = true
            )
            
            // Avatar sans image (fallback)
            YamsPlayerAvatar(
                avatarUrl = null,
                playerName = "John Doe",
                isWinner = true
            )
            
            // Avatar large
            YamsAvatarLarge(
                avatarUrl = "https://api.dicebear.com/9.x/avataaars/svg?seed=sarah&backgroundColor=ffffff",
                contentDescription = "Sarah's avatar"
            )
            
            // Avatar petit
            YamsAvatarSmall(
                avatarUrl = null,
                contentDescription = "Small avatar",
                fallbackText = "AB"
            )
        }
    }
}
