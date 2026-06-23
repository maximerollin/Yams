package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import io.github.maximerollin.yams.core.designsystem.preview.YamsPhoneStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.feature.game.play.model.CelebrationFallbackEnterMillis
import io.github.maximerollin.yams.feature.game.play.model.CelebrationFallbackExitMillis
import io.github.maximerollin.yams.feature.game.play.model.CelebrationFallbackHoldMillis
import io.github.maximerollin.yams.feature.game.play.model.GamePlayCelebration
import io.github.maximerollin.yams.feature.game.play.model.GamePlayCelebrationType
import io.github.maximerollin.yams.feature.game.play.model.copyRevealProgress
import io.github.maximerollin.yams.feature.game.play.model.timeoutMillis
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.Res
import yams.feature.game.play.generated.resources.play_celebration_big_score_title
import yams.feature.game.play.generated.resources.play_celebration_score_message
import yams.feature.game.play.generated.resources.play_celebration_yams_title
import yams.feature.game.play.generated.resources.play_points_value

@Composable
internal fun GamePlayCelebrationOverlay(
    celebration: GamePlayCelebration,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismiss: Boolean = true,
    forceFallback: Boolean = false,
    previewProgress: Float? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val animationsDisabled =
        coroutineScope.coroutineContext[MotionDurationScale]?.scaleFactor == 0f
    var useFallback by remember(celebration.id, forceFallback, animationsDisabled) {
        mutableStateOf(forceFallback || animationsDisabled)
    }
    var fallbackVisible by remember(celebration.id) { mutableStateOf(!autoDismiss) }
    var hasFinished by remember(celebration.id) { mutableStateOf(false) }
    val latestOnFinished by rememberUpdatedState(onFinished)
    val assetPath = assetPathFor(celebration.type, celebration.dieFace)

    val compositionResult = if (useFallback) {
        null
    } else {
        rememberLottieComposition(celebration.type, celebration.dieFace) {
            LottieCompositionSpec.JsonString(
                Res.readBytes(assetPath).decodeToString(),
            )
        }
    }
    val composition = compositionResult?.value
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = !useFallback && autoDismiss && previewProgress == null,
        iterations = 1,
    )
    val progress = previewProgress ?: animationState.progress

    fun finishOnce() {
        if (autoDismiss && !hasFinished) {
            hasFinished = true
            latestOnFinished()
        }
    }

    LaunchedEffect(celebration.id, compositionResult?.isFailure) {
        if (compositionResult?.isFailure == true) useFallback = true
    }

    LaunchedEffect(celebration.id, useFallback, autoDismiss) {
        if (!autoDismiss) return@LaunchedEffect
        if (useFallback) {
            fallbackVisible = true
            delay(CelebrationFallbackEnterMillis + CelebrationFallbackHoldMillis)
            fallbackVisible = false
            delay(CelebrationFallbackExitMillis)
            finishOnce()
        } else {
            delay(celebration.type.timeoutMillis)
            finishOnce()
        }
    }

    LaunchedEffect(
        celebration.id,
        useFallback,
        composition,
        animationState.isAtEnd,
    ) {
        if (
            autoDismiss &&
            !useFallback &&
            composition != null &&
            animationState.isAtEnd
        ) {
            finishOnce()
        }
    }

    if (compositionResult?.isLoading == true && !useFallback) return

    CelebrationScene(
        celebration = celebration,
        useFallback = useFallback,
        fallbackVisible = fallbackVisible,
        lottieContent = {
            Image(
                painter = rememberLottiePainter(
                    composition = composition,
                    // The animation State must be read *inside* this lambda. compottie
                    // wraps it in a derivedStateOf, which only recomputes when a snapshot
                    // State is read during its calculation. Passing a value captured in the
                    // composition scope reads no State here, so the painter stays frozen on
                    // the first frame (progress ~0 = blank) and the animation never plays.
                    progress = { previewProgress ?: animationState.progress },
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(maxWidth = 540.dp, maxHeight = 540.dp),
            )
        },
        showCopy = useFallback || progress >= celebration.type.copyRevealProgress,
        modifier = modifier,
    )
}

@Composable
private fun CelebrationScene(
    celebration: GamePlayCelebration,
    useFallback: Boolean,
    fallbackVisible: Boolean,
    lottieContent: @Composable () -> Unit,
    showCopy: Boolean,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (celebration.type) {
        GamePlayCelebrationType.YAMS -> YamsTheme.colors.gold
        GamePlayCelebrationType.BIG_SCORE -> YamsTheme.colors.info
    }
    val title = when (celebration.type) {
        GamePlayCelebrationType.YAMS ->
            stringResource(Res.string.play_celebration_yams_title)
        GamePlayCelebrationType.BIG_SCORE ->
            stringResource(Res.string.play_celebration_big_score_title)
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center,
    ) {
        if (!useFallback) lottieContent()

        AnimatedVisibility(
            visible = if (useFallback) fallbackVisible else showCopy,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = CelebrationFallbackEnterMillis.toInt(),
                ),
            ) + if (useFallback) {
                scaleIn(initialScale = 1f)
            } else {
                scaleIn(initialScale = 0.9f)
            },
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = CelebrationFallbackExitMillis.toInt(),
                ),
            ),
        ) {
            CelebrationCopy(
                celebration = celebration,
                title = title,
                accentColor = accentColor,
            )
        }
    }
}

@Composable
private fun CelebrationCopy(
    celebration: GamePlayCelebration,
    title: String,
    accentColor: Color,
) {
    Surface(
        modifier = Modifier
            .padding(24.dp)
            .semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Polite
            },
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(
                    Res.string.play_celebration_score_message,
                    celebration.playerName,
                ),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = accentColor,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.play_points_value, celebration.score),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private const val DefaultYamsDieFace = 5

private fun assetPathFor(
    type: GamePlayCelebrationType,
    dieFace: Int?,
): String = when (type) {
    GamePlayCelebrationType.YAMS ->
        "files/celebration_yams_${(dieFace ?: DefaultYamsDieFace).coerceIn(1, 6)}.json"
    GamePlayCelebrationType.BIG_SCORE -> "files/celebration_big_score.json"
}

@Composable
private fun CelebrationPreview(
    type: GamePlayCelebrationType,
    forceFallback: Boolean = false,
) {
    YamsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            GamePlayCelebrationOverlay(
                celebration = GamePlayCelebration(
                    id = 1,
                    type = type,
                    playerName = "Lina",
                    score = if (type == GamePlayCelebrationType.YAMS) 50 else 40,
                ),
                onFinished = {},
                modifier = Modifier.fillMaxSize(),
                autoDismiss = false,
                forceFallback = forceFallback,
                previewProgress = if (forceFallback) null else 0.55f,
            )
        }
    }
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun YamsCelebrationPreview() {
    CelebrationPreview(type = GamePlayCelebrationType.YAMS)
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun BigScoreCelebrationPreview() {
    CelebrationPreview(type = GamePlayCelebrationType.BIG_SCORE)
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun YamsCelebrationFallbackPreview() {
    CelebrationPreview(
        type = GamePlayCelebrationType.YAMS,
        forceFallback = true,
    )
}

@YamsPhoneStoreScreenshotPreviews
@Composable
private fun BigScoreCelebrationFallbackPreview() {
    CelebrationPreview(
        type = GamePlayCelebrationType.BIG_SCORE,
        forceFallback = true,
    )
}
