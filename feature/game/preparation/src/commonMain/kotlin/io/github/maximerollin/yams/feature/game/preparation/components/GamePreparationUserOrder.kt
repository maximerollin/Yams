package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.icon.DragHandle
import io.github.maximerollin.yams.core.designsystem.icon.Groups
import io.github.maximerollin.yams.core.designsystem.icon.Tactic
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.User
import sh.calvin.reorderable.ReorderableColumn

@Composable
public fun GamePreparationUserOrder(
    users: List<User>,
    isUserOrderRandomized: Boolean,
    onToggleIsUserOrderRandomized: (Boolean) -> Unit,
    onOrderUser: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    GamePreparationSection(
        title = "Ordre des joueurs",
        icon = YamsIcons.Groups,
        modifier = modifier,
    ) {
        val shape = RoundedCornerShape(32.dp)
        Surface(
            shape = shape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserOrderToggleButton(
                    label = "Aléatoire",
                    icon = YamsIcons.Tactic,
                    selected = isUserOrderRandomized,
                    onClick = { onToggleIsUserOrderRandomized(true) },
                    modifier = Modifier.weight(1f),
                )

                UserOrderToggleButton(
                    label = "Choisir",
                    icon = YamsIcons.DragHandle,
                    selected = !isUserOrderRandomized,
                    onClick = { onToggleIsUserOrderRandomized(false) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        AnimatedContent(
            targetState = !isUserOrderRandomized,
        ) { isUserOrderRandomized ->

            if (isUserOrderRandomized) {
                val hapticFeedback = LocalHapticFeedback.current

                ReorderableColumn(
                    list = users,
                    onSettle = { from, to ->
                        onOrderUser(from, to)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) { index, user, isDragging ->
                    key(user.id.value) {
                        ReorderableItem {
                            val elevation by animateDpAsState(
                                targetValue = if (isDragging) 8.dp else 2.dp,
                                label = "elevation"
                            )

                            UserOrderDraggableItem(
                                user = user,
                                position = index + 1,
                                elevation = elevation,
                                modifier = Modifier
                                    .draggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        }
                                    )
                            )
                        }
                    }

                }
            }

        }
    }
}

@Preview
@Composable
private fun GamePreparationUserOrderRandomPreview() {
    YamsTheme {
        GamePreparationUserOrder(
            users = UserMocks.users.subList(0, 3),
            isUserOrderRandomized = true,
            onToggleIsUserOrderRandomized = {},
            onOrderUser = { a, b -> {} }
        )
    }
}

@Preview
@Composable
private fun GamePreparationUserOrderManualPreview() {
    YamsTheme {
        GamePreparationUserOrder(
            users = UserMocks.users.subList(0, 3),
            isUserOrderRandomized = false,
            onToggleIsUserOrderRandomized = {},
            onOrderUser = { a, b -> {} }
        )
    }
}