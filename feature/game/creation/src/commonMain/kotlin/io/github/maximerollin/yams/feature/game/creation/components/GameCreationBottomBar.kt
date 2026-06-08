package io.github.maximerollin.yams.feature.game.creation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.mocks.UserMocks
import io.github.maximerollin.yams.core.model.User
import org.jetbrains.compose.resources.stringResource
import yams.feature.game.creation.generated.resources.*

private const val MAX_VISIBLE_AVATAR_COUNT = 5

@Composable
internal fun GameCreationBottomBar(
    selectedUsers: List<User>,
    onCreateGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleUsers = selectedUsers.take(MAX_VISIBLE_AVATAR_COUNT)
    val remainingUsersCount = (selectedUsers.size - visibleUsers.size).coerceAtLeast(0)

    Column(modifier = Modifier.navigationBarsPadding()) {
        AnimatedVisibility(
            visible = selectedUsers.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp,
                ),
                modifier = modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-12).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        visibleUsers.forEach { user ->
                            SelectedUserAvatar(user = user)
                        }

                        if (remainingUsersCount > 0) {
                            RemainingSelectedUsersBadge(remainingUsersCount = remainingUsersCount)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(
                            if (selectedUsers.size > 1) {
                                Res.string.creation_selected_players_many
                            } else {
                                Res.string.creation_selected_players_one
                            },
                            selectedUsers.size,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(999.dp),
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        YamsPrimaryButton(
            onClick = onCreateGame,
            enabled = selectedUsers.isNotEmpty(),
            text = stringResource(Res.string.creation_continue),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun SelectedUserAvatar(
    user: User,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = stringResource(Res.string.creation_user_avatar_cd),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun RemainingSelectedUsersBadge(
    remainingUsersCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = "+$remainingUsersCount",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview
@Composable
private fun GameCreationBottomBarPreview() {
    YamsTheme {
        GameCreationBottomBar(
            selectedUsers = UserMocks.users.subList(0, 3),
            onCreateGame = {},
        )
    }
}

@Preview
@Composable
private fun GameCreationBottomBarPreviewManyPlayers() {
    YamsTheme {
        GameCreationBottomBar(
            selectedUsers = UserMocks.users.take(7),
            onCreateGame = {}
        )
    }
}

@Preview
@Composable
private fun GameCreationBottomBarPreviewEmpty() {
    YamsTheme {
        GameCreationBottomBar(
            selectedUsers = emptyList(),
            onCreateGame = {}
        )
    }
}
