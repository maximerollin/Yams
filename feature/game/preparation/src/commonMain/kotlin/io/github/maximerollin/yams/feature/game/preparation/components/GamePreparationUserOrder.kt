package io.github.maximerollin.yams.feature.game.preparation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var showUsers by remember { mutableStateOf(true) }

    GamePreparationSection(
        title = "Ordre des joueurs",
        onAction = { showUsers = !showUsers },
        onActionEnabled = !isUserOrderRandomized,
        onActionState = showUsers,
        icon = YamsIcons.Groups,
        modifier = modifier,
    ) {
        AnimatedSegmentedControl(
            items = listOf(
                SegmentedControlItem(label = "Aléatoire", icon = YamsIcons.Tactic),
                SegmentedControlItem(label = "Choisir", icon = YamsIcons.DragHandle),
            ),
            selectedIndex = if (isUserOrderRandomized) 0 else 1,
            onSelectedIndexChange = { index ->
                onToggleIsUserOrderRandomized(index == 0)
            },
            modifier = Modifier.fillMaxWidth(),
            height = 44.dp,
        )

        AnimatedContent(targetState = !isUserOrderRandomized && showUsers) { showUsersList ->
            if (showUsersList) {
                val hapticFeedback = LocalHapticFeedback.current
                val usersListScrollState = rememberScrollState()

                ReorderableColumn(
                    list = users,
                    onSettle = { from, to ->
                        onOrderUser(from, to)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 8.dp)
                        .heightIn(max = 320.dp)
                        .verticalScroll(usersListScrollState),
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
                                    .fillMaxWidth()
                                    .let {
                                        if (index == users.lastIndex) {
                                            it.padding(bottom = 4.dp)
                                        } else {
                                            it
                                        }
                                    }
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
