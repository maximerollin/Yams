package io.github.maximerollin.yams.feature.user.edition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.maximerollin.yams.core.designsystem.component.YamsDestructiveButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.edition.components.UserEditionAvatar
import io.github.maximerollin.yams.feature.user.edition.components.UserEditionInput
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun UserEditionBottomSheet(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    onDeleteUser: (UserId) -> Unit,
    onDismissRequest: () -> Unit,
    isEdit: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun dismissSheet() {
        scope
            .launch { sheetState.hide() }
            .invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        UserEditionScreen(
            uiState = uiState,
            onAction = onAction,
            onDeleteUser = onDeleteUser,
            onDismissRequest = ::dismissSheet,
            isEdit = isEdit,
            modifier = modifier,
        )
    }
}

@Composable
private fun UserEditionScreen(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    onDeleteUser: (UserId) -> Unit,
    onDismissRequest: () -> Unit,
    isEdit: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .verticalScroll(scrollState)
            .navigationBarsPadding()
            .padding(16.dp),
    ) {
        UserEditionAvatar(
            selectedAvatar = uiState.avatar,
            onAvatarSelected = { onAction(UserEditionAction.EditAvatar(it)) }
        )

        UserEditionInput(
            name = uiState.name,
            onChangeName = { onAction(UserEditionAction.EditName(it)) },
        )

        YamsPrimaryButton(
            enabled = uiState.name.isNotBlank(),
            text = if (isEdit) "Edit" else "Create",
            onClick = {
                onDismissRequest()
                onAction(UserEditionAction.SaveUser)
            }
        )

        if (isEdit && uiState.userId != null) {
            YamsDestructiveButton(
                text = "Delete",
                onClick = {
                    onDismissRequest()
                    onDeleteUser(uiState.userId)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserEditionScreenPreview() {
    YamsTheme {
        UserEditionScreen(
            uiState = UserEditionUiState(),
            onAction = {},
            isEdit = true,
            onDeleteUser = {},
            onDismissRequest = {},
        )
    }
}

@Preview
@Composable
private fun UserEditionBottomSheetPreview() {
    YamsTheme {
        UserEditionBottomSheet(
            uiState = UserEditionUiState(),
            onAction = {},
            isEdit = false,
            onDeleteUser = {},
            onDismissRequest = {},
        )
    }
}