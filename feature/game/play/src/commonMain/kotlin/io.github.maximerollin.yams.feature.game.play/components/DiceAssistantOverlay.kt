package io.github.maximerollin.yams.feature.game.play.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsSecondaryButton
import io.github.maximerollin.yams.core.designsystem.component.YamsTextButton
import io.github.maximerollin.yams.core.designsystem.icon.AddAPhoto
import io.github.maximerollin.yams.core.designsystem.icon.Check
import io.github.maximerollin.yams.core.designsystem.icon.Close
import io.github.maximerollin.yams.core.designsystem.icon.Lock
import io.github.maximerollin.yams.core.designsystem.icon.PencilSparkles
import io.github.maximerollin.yams.core.designsystem.icon.Strategy
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.designsystem.util.IconInfo
import io.github.maximerollin.yams.core.model.GameSettings
import io.github.maximerollin.yams.core.model.ScoreKey
import io.github.maximerollin.yams.feature.game.play.assistant.ConfirmedDiceRoll
import io.github.maximerollin.yams.feature.game.play.assistant.CorrectionReason
import io.github.maximerollin.yams.feature.game.play.assistant.DetectedDie
import io.github.maximerollin.yams.feature.game.play.assistant.DiceDetectionStabilizer
import io.github.maximerollin.yams.feature.game.play.assistant.DiceAssistantContext
import io.github.maximerollin.yams.feature.game.play.assistant.DiceAssistantUiState
import io.github.maximerollin.yams.feature.game.play.assistant.DiceAssistantWorkflow
import io.github.maximerollin.yams.feature.game.play.assistant.DiceRecommendation
import io.github.maximerollin.yams.feature.game.play.assistant.RollIndex
import io.github.maximerollin.yams.feature.game.play.assistant.rememberDiceCameraPreview
import io.github.maximerollin.yams.feature.game.play.assistant.rememberDiceRecognitionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.play.generated.resources.*
import kotlin.math.roundToInt

@Composable
internal fun DiceAssistantOverlay(
    context: DiceAssistantContext,
    hasSeenUsageGuide: Boolean? = true,
    onUsageGuideSeen: () -> Unit = {},
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val recognitionEngine = rememberDiceRecognitionEngine()
    val cameraPreview = rememberDiceCameraPreview(recognitionEngine)
    val detectionStabilizer = remember {
        DiceDetectionStabilizer(requiredConsistentFrames = 1)
    }
    var rollIndex by rememberSaveable { mutableStateOf(RollIndex.ONE) }
    var detectedDice by remember { mutableStateOf(emptyList<DetectedDie>()) }
    var scanFeedback by remember { mutableStateOf(ScanFeedback.SEARCHING) }
    var isUsageGuideDismissedForSession by rememberSaveable { mutableStateOf(false) }
    val isUsageGuideVisible =
        hasSeenUsageGuide == false && !isUsageGuideDismissedForSession
    fun scanningState(index: RollIndex = rollIndex): DiceAssistantUiState =
        if (recognitionEngine.isAvailable) {
            DiceAssistantUiState.Scanning(
                rollIndex = index,
                detectedDice = detectedDice,
            )
        } else {
            DiceAssistantUiState.Error
        }

    var uiState by remember {
        mutableStateOf(scanningState())
    }

    LaunchedEffect(uiState) {
        val calculatingState = uiState as? DiceAssistantUiState.Calculating ?: return@LaunchedEffect
        uiState = withContext(Dispatchers.Default) {
            DiceAssistantWorkflow.confirm(
                rollIndex = calculatingState.roll.rollIndex,
                faces = calculatingState.roll.faces,
                context = context,
            )
        }
    }

    fun selectRoll(index: RollIndex) {
        rollIndex = index
        detectedDice = emptyList()
        detectionStabilizer.reset()
        scanFeedback = ScanFeedback.SEARCHING
        uiState = scanningState(index)
    }

    fun showCorrection(reason: CorrectionReason = CorrectionReason.USER_REQUEST) {
        uiState = DiceAssistantUiState.Correction(
            rollIndex = rollIndex,
            detectedDice = detectedDice,
            faces = DiceAssistantWorkflow.correctionFacesFrom(detectedDice),
            reason = reason,
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DiceAssistantSystemBarsEffect()
        DiceAssistantOverlayContent(
            state = uiState,
            selectedRollIndex = rollIndex,
            onDismiss = onDismiss,
            columnCount = context.settings.columnCount,
            hasIgnoredCustomRules = context.settings.areCustomRulesEnabled &&
                context.settings.customGameSettings.any { it.isEnabled },
            scanFeedback = scanFeedback,
            onRollIndexSelected = ::selectRoll,
            onManualCorrection = ::showCorrection,
            onConfirmFaces = { faces ->
                uiState = if (DiceAssistantWorkflow.canConfirm(faces)) {
                    DiceAssistantUiState.Calculating(
                        roll = ConfirmedDiceRoll(
                            faces = faces,
                            rollIndex = rollIndex,
                        ),
                    )
                } else {
                    DiceAssistantWorkflow.confirm(
                        rollIndex = rollIndex,
                        faces = faces,
                        context = context,
                    )
                }
            },
            onRetry = {
                detectedDice = emptyList()
                detectionStabilizer.reset()
                scanFeedback = ScanFeedback.SEARCHING
                uiState = scanningState()
            },
            cameraPreview = { previewModifier ->
                if (isUsageGuideVisible) {
                    CameraGuidePlaceholder(modifier = previewModifier)
                } else {
                    cameraPreview.Content(
                        modifier = previewModifier,
                        onDetections = { dice ->
                            scanFeedback = scanFeedback.advancedWith(dice.size)
                            detectionStabilizer.accept(dice)?.let { stableDice ->
                                detectedDice = stableDice
                                uiState = DiceAssistantWorkflow.stateForDetections(
                                    rollIndex = rollIndex,
                                    detections = stableDice,
                                )
                            }
                        },
                        onPermissionDenied = {
                            uiState = DiceAssistantWorkflow.permissionDenied()
                        },
                        onCameraError = {
                            uiState = DiceAssistantUiState.Error
                        },
                    )
                }
            },
            modifier = modifier,
        )
    }

    if (isUsageGuideVisible) {
        DiceAssistantUsageGuideDialog(
            onDismiss = {
                isUsageGuideDismissedForSession = true
                onUsageGuideSeen()
            },
        )
    }
}

@Composable
private fun DiceAssistantOverlayContent(
    state: DiceAssistantUiState,
    selectedRollIndex: RollIndex,
    onDismiss: () -> Unit,
    columnCount: Int,
    hasIgnoredCustomRules: Boolean,
    scanFeedback: ScanFeedback,
    onRollIndexSelected: (RollIndex) -> Unit,
    onManualCorrection: () -> Unit,
    onConfirmFaces: (List<Int>) -> Unit,
    onRetry: () -> Unit,
    cameraPreview: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AssistantHeader(onDismiss = onDismiss)

            RollSelector(
                selectedRollIndex = selectedRollIndex,
                onRollIndexSelected = onRollIndexSelected,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (state) {
                    is DiceAssistantUiState.Scanning -> ScanningContent(
                        cameraPreview = cameraPreview,
                        feedback = scanFeedback,
                    )

                    is DiceAssistantUiState.Correction -> CorrectionContent(
                        state = state,
                        onConfirmFaces = onConfirmFaces,
                    )

                    is DiceAssistantUiState.Calculating -> CalculatingContent(
                        roll = state.roll,
                    )

                    is DiceAssistantUiState.Recommendation -> RecommendationContent(
                        state = state,
                        showColumn = columnCount == 2,
                        hasIgnoredCustomRules = hasIgnoredCustomRules,
                        onRetry = onRetry,
                    )

                    DiceAssistantUiState.PermissionDenied -> MessageContent(
                        icon = YamsIcons.Lock,
                        title = stringResource(Res.string.play_assistant_permission_title),
                        message = stringResource(Res.string.play_assistant_permission_message),
                        primaryText = stringResource(Res.string.play_assistant_manual_entry),
                        onPrimaryClick = onManualCorrection,
                    )

                    DiceAssistantUiState.Error -> MessageContent(
                        icon = YamsIcons.AddAPhoto,
                        title = stringResource(Res.string.play_assistant_error_title),
                        message = stringResource(Res.string.play_assistant_error_message),
                        primaryText = stringResource(Res.string.play_assistant_retry),
                        onPrimaryClick = onRetry,
                        secondaryText = stringResource(Res.string.play_assistant_manual_entry),
                        onSecondaryClick = onManualCorrection,
                    )
                }
            }
        }
    }
}

@Composable
private fun AssistantHeader(
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = YamsIcons.PencilSparkles,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = YamsTheme.colors.gold,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.play_assistant_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = YamsTheme.colors.brown,
            )
            Text(
                text = stringResource(Res.string.play_assistant_private_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = YamsIcons.Close,
                contentDescription = stringResource(Res.string.play_assistant_close),
            )
        }
    }
}

@Composable
private fun RollSelector(
    selectedRollIndex: RollIndex,
    onRollIndexSelected: (RollIndex) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RollIndex.entries.forEach { rollIndex ->
            val selected = selectedRollIndex == rollIndex
            Button(
                onClick = { onRollIndexSelected(rollIndex) },
                modifier = Modifier.weight(1f),
                colors = if (selected) {
                    ButtonDefaults.buttonColors(
                        containerColor = YamsTheme.colors.gold,
                        contentColor = YamsTheme.colors.onGold,
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    )
                },
                shape = RoundedCornerShape(8.dp),
                border = if (selected) {
                    null
                } else {
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                },
            ) {
                Text(
                    text = rollIndex.localizedLabel(),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun ScanningContent(
    cameraPreview: @Composable (Modifier) -> Unit,
    feedback: ScanFeedback,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)),
    ) {
        cameraPreview(Modifier.fillMaxSize())
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
                AnimatedStatusText(
                    text = feedback.localizedText(),
                )
            }
        }
    }
}

@Composable
private fun AnimatedStatusText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    fontWeight: FontWeight? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val transition = rememberInfiniteTransition(label = "assistantStatus")
    val dotProgress by transition.animateFloat(
        initialValue = 1f,
        targetValue = 3.99f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Restart,
        ),
        label = "assistantStatusDots",
    )
    val dotCount = dotProgress.toInt().coerceIn(1, 3)
    Text(
        text = "$text${".".repeat(dotCount)}",
        style = style,
        fontWeight = fontWeight,
        color = color,
    )
}

@Composable
private fun CameraGuidePlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = YamsIcons.AddAPhoto,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DiceAssistantUsageGuideDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = YamsIcons.PencilSparkles,
                contentDescription = null,
                tint = YamsTheme.colors.gold,
            )
        },
        title = {
            Text(text = stringResource(Res.string.play_assistant_guide_title))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.play_assistant_guide_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                GuideStep(
                    number = 1,
                    text = stringResource(Res.string.play_assistant_guide_light),
                )
                GuideStep(
                    number = 2,
                    text = stringResource(Res.string.play_assistant_guide_angle),
                )
                GuideStep(
                    number = 3,
                    text = stringResource(Res.string.play_assistant_guide_still),
                )
            }
        },
        confirmButton = {
            YamsTextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.play_assistant_guide_start))
            }
        },
    )
}

@Composable
private fun GuideStep(
    number: Int,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = YamsTheme.colors.gold,
            contentColor = YamsTheme.colors.onGold,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CorrectionContent(
    state: DiceAssistantUiState.Correction,
    onConfirmFaces: (List<Int>) -> Unit,
) {
    var faces by remember(state.faces) { mutableStateOf(state.faces) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = correctionTitle(state.reason),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = YamsTheme.colors.brown,
                )
                Text(
                    text = stringResource(Res.string.play_assistant_correction_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DiceFaceEditor(
                    faces = faces,
                    onFacesChange = { faces = it },
                )
            }
        }

        YamsPrimaryButton(
            onClick = { onConfirmFaces(faces) },
            enabled = DiceAssistantWorkflow.canConfirm(faces),
            text = stringResource(Res.string.play_assistant_get_advice),
            icon = IconInfo(
                vector = YamsIcons.PencilSparkles,
                contentDescription = stringResource(Res.string.play_assistant_get_advice),
            ),
        )
    }
}

@Composable
private fun CalculatingContent(
    roll: ConfirmedDiceRoll,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = YamsTheme.colors.gold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedStatusText(
            text = stringResource(Res.string.play_assistant_calculating_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = YamsTheme.colors.brown,
        )
        Spacer(modifier = Modifier.height(12.dp))
        DiceFaceRow(faces = roll.faces)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.play_assistant_calculating_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DiceFaceEditor(
    faces: List<Int>,
    onFacesChange: (List<Int>) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        faces.forEachIndexed { index, face ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        Res.string.play_assistant_die_number,
                        index + 1,
                    ),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                FaceStepper(
                    face = face,
                    onFaceChange = { updatedFace ->
                        onFacesChange(
                            faces.mapIndexed { faceIndex, currentFace ->
                                if (faceIndex == index) updatedFace else currentFace
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun FaceStepper(
    face: Int,
    onFaceChange: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = { onFaceChange((face - 1).coerceAtLeast(1)) },
            enabled = face > 1,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = "-")
        }
        DiceFacePill(face = face)
        OutlinedButton(
            onClick = { onFaceChange((face + 1).coerceAtMost(6)) },
            enabled = face < 6,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(text = "+")
        }
    }
}

@Composable
private fun RecommendationContent(
    state: DiceAssistantUiState.Recommendation,
    showColumn: Boolean,
    hasIgnoredCustomRules: Boolean,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = state.roll.rollIndex.localizedLabel(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DiceFaceRow(faces = state.roll.faces)
                RecommendationMessage(
                    recommendation = state.recommendation,
                    showColumn = showColumn,
                )
                if (hasIgnoredCustomRules) {
                    Text(
                        text = stringResource(
                            Res.string.play_assistant_recommendation_custom_rules_ignored,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        YamsSecondaryButton(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = YamsIcons.AddAPhoto,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(Res.string.play_assistant_recommendation_new_scan))
        }
    }
}

@Composable
private fun RecommendationMessage(
    recommendation: DiceRecommendation,
    showColumn: Boolean,
) {
    when (recommendation) {
        is DiceRecommendation.KeepDice -> {
            Text(
                text = stringResource(
                    Res.string.play_assistant_recommendation_keep_reroll,
                    recommendation.keepFaces.size,
                    recommendation.rerollCount,
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = YamsTheme.colors.brown,
            )
            DiceFaceRow(faces = recommendation.keepFaces)
            ExpectedValueText(expectedValue = recommendation.expectedValue)
        }

        is DiceRecommendation.ScoreCell -> {
            Text(
                text = stringResource(
                    Res.string.play_assistant_recommendation_score,
                    labelForScoreKey(recommendation.key),
                    columnSuffix(
                        columnIndex = recommendation.columnIndex,
                        showColumn = showColumn,
                    ),
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = YamsTheme.colors.brown,
            )
            Text(
                text = if (recommendation.awardsExtraFiveOfAKindBonus) {
                    stringResource(
                        Res.string.play_assistant_recommendation_points_bonus,
                        recommendation.score,
                    )
                } else {
                    stringResource(
                        Res.string.play_assistant_recommendation_points,
                        recommendation.score,
                    )
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ExpectedValueText(expectedValue = recommendation.expectedValue)
        }

        is DiceRecommendation.NoAvailableMove -> {
            Text(
                text = stringResource(Res.string.play_assistant_recommendation_none),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = YamsTheme.colors.brown,
            )
        }
    }
}

@Composable
private fun ExpectedValueText(
    expectedValue: Double,
) {
    Text(
        text = stringResource(
            Res.string.play_assistant_recommendation_expected,
            expectedValue.asSingleDecimal(),
        ),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun MessageContent(
    icon: ImageVector,
    title: String,
    message: String,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = YamsTheme.colors.gold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = YamsTheme.colors.brown,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        YamsPrimaryButton(
            onClick = onPrimaryClick,
            text = primaryText,
        )
        if (secondaryText != null && onSecondaryClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            YamsSecondaryButton(
                onClick = onSecondaryClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = secondaryText)
            }
        }
    }
}

@Composable
private fun DiceFaceRow(
    faces: List<Int>,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        faces.forEach { face ->
            DiceFacePill(face = face)
        }
    }
}

@Composable
private fun DiceFacePill(
    face: Int,
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = YamsTheme.colors.gold,
        contentColor = YamsTheme.colors.onGold,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = face.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun correctionTitle(reason: CorrectionReason): String = stringResource(
    when (reason) {
        CorrectionReason.LOW_CONFIDENCE -> Res.string.play_assistant_correction_low_confidence
        CorrectionReason.DETECTION_COUNT -> Res.string.play_assistant_correction_count
        CorrectionReason.USER_REQUEST -> Res.string.play_assistant_correction_confirm
    },
)

@Composable
private fun labelForScoreKey(key: ScoreKey): String = stringResource(
    when (key) {
        ScoreKey.ONES -> Res.string.play_row_ones
        ScoreKey.TWOS -> Res.string.play_row_twos
        ScoreKey.THREES -> Res.string.play_row_threes
        ScoreKey.FOURS -> Res.string.play_row_fours
        ScoreKey.FIVES -> Res.string.play_row_fives
        ScoreKey.SIXES -> Res.string.play_row_sixes
        ScoreKey.THREE_OF_A_KIND -> Res.string.play_three_of_kind
        ScoreKey.FOUR_OF_A_KIND -> Res.string.play_four_of_kind
        ScoreKey.FULL_HOUSE -> Res.string.play_full
        ScoreKey.SMALL_STRAIGHT -> Res.string.play_small_straight
        ScoreKey.LARGE_STRAIGHT -> Res.string.play_large_straight
        ScoreKey.FIVE_OF_A_KIND -> Res.string.play_yams
        ScoreKey.EXTRA_FIVE_OF_A_KIND -> Res.string.play_yams_bonus
        ScoreKey.CHANCE -> Res.string.play_chance
        else -> Res.string.play_rule_custom
    },
)

@Composable
private fun RollIndex.localizedLabel(): String = stringResource(
    when (this) {
        RollIndex.ONE -> Res.string.play_assistant_roll_one
        RollIndex.TWO -> Res.string.play_assistant_roll_two
        RollIndex.THREE -> Res.string.play_assistant_roll_three
    },
)

@Composable
private fun ScanFeedback.localizedText(): String = stringResource(
    when (this) {
        ScanFeedback.SEARCHING -> Res.string.play_assistant_scanning
        ScanFeedback.DETECTING -> Res.string.play_assistant_scanning_detecting
        ScanFeedback.HOLD_STILL -> Res.string.play_assistant_scanning_hold
    },
)

private fun ScanFeedback.advancedWith(detectionCount: Int): ScanFeedback {
    val detectedFeedback = when (detectionCount) {
        0 -> ScanFeedback.SEARCHING
        1 -> ScanFeedback.DETECTING
        else -> ScanFeedback.HOLD_STILL
    }
    return maxOf(this, detectedFeedback)
}

@Composable
private fun columnSuffix(
    columnIndex: Int?,
    showColumn: Boolean,
): String = if (showColumn && columnIndex != null) {
    stringResource(Res.string.play_assistant_recommendation_column, columnIndex + 1)
} else {
    ""
}

private enum class ScanFeedback {
    SEARCHING,
    DETECTING,
    HOLD_STILL,
}

private fun Double.asSingleDecimal(): String =
    ((this * 10).roundToInt() / 10.0).toString()

@Composable
private fun PreviewCameraFrame(modifier: Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Aperçu caméra",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun DiceAssistantScanningPreview() {
    DiceAssistantPreviewContent(
        state = DiceAssistantUiState.Scanning(
            rollIndex = RollIndex.ONE,
            detectedDice = emptyList(),
        ),
    )
}

@Preview
@Composable
private fun DiceAssistantCorrectionPreview() {
    DiceAssistantPreviewContent(
        state = DiceAssistantUiState.Correction(
            rollIndex = RollIndex.TWO,
            detectedDice = emptyList(),
            faces = listOf(6, 6, 5, 2, 1),
            reason = CorrectionReason.USER_REQUEST,
        ),
    )
}

@Preview
@Composable
private fun DiceAssistantRecommendationPreview() {
    DiceAssistantPreviewContent(
        state = DiceAssistantUiState.Recommendation(
            roll = ConfirmedDiceRoll(
                faces = listOf(6, 6, 6, 2, 3),
                rollIndex = RollIndex.TWO,
            ),
            recommendation = DiceRecommendation.KeepDice(
                keepFaces = listOf(6, 6, 6),
                rerollCount = 2,
                expectedValue = 31.4,
            ),
        ),
    )
}

@Preview
@Composable
private fun DiceAssistantCalculatingPreview() {
    DiceAssistantPreviewContent(
        state = DiceAssistantUiState.Calculating(
            roll = ConfirmedDiceRoll(
                faces = listOf(6, 6, 6, 2, 3),
                rollIndex = RollIndex.TWO,
            ),
        ),
    )
}

@Preview
@Composable
private fun DiceAssistantErrorPreview() {
    DiceAssistantPreviewContent(
        state = DiceAssistantUiState.Error,
    )
}

@Preview
@Composable
private fun DiceAssistantPermissionDeniedPreview() {
    DiceAssistantPreviewContent(state = DiceAssistantUiState.PermissionDenied)
}

@Composable
private fun DiceAssistantPreviewContent(
    state: DiceAssistantUiState,
) {
    YamsTheme {
        DiceAssistantOverlayContent(
            state = state,
            selectedRollIndex = when (state) {
                is DiceAssistantUiState.Correction -> state.rollIndex
                is DiceAssistantUiState.Calculating -> state.roll.rollIndex
                is DiceAssistantUiState.Recommendation -> state.roll.rollIndex
                is DiceAssistantUiState.Scanning -> state.rollIndex
                else -> RollIndex.ONE
            },
            onDismiss = {},
            columnCount = 1,
            hasIgnoredCustomRules = false,
            scanFeedback = ScanFeedback.HOLD_STILL,
            onRollIndexSelected = {},
            onManualCorrection = {},
            onConfirmFaces = {},
            onRetry = {},
            cameraPreview = { PreviewCameraFrame(it) },
        )
    }
}

@Suppress("unused")
private fun previewContext(): DiceAssistantContext =
    DiceAssistantContext(
        settings = GameSettings.YamsSettings(),
        scoreEntries = emptyMap(),
    )
