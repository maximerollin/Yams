package io.github.maximerollin.yams.feature.user.edition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.maximerollin.yams.core.designsystem.component.AppIconButton
import io.github.maximerollin.yams.core.designsystem.component.AppTopBar
import io.github.maximerollin.yams.core.designsystem.component.YamsDestructiveButton
import io.github.maximerollin.yams.core.designsystem.component.YamsPrimaryButton
import io.github.maximerollin.yams.core.designsystem.icon.ChevronLeft
import io.github.maximerollin.yams.core.designsystem.icon.YamsIcons
import io.github.maximerollin.yams.core.designsystem.preview.YamsStoreScreenshotPreviews
import io.github.maximerollin.yams.core.designsystem.theme.YamsTheme
import io.github.maximerollin.yams.core.designsystem.theme.colors
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.core.ui.utils.AppAvatars
import io.github.maximerollin.yams.feature.user.edition.components.UserEditionAvatar
import io.github.maximerollin.yams.feature.user.edition.components.UserEditionInput
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import yams.feature.user.edition.generated.resources.*

@Composable
public fun UserEditionRoute(
    userId: UserId,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserEditionViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.onAction(UserEditionAction.ResetEdition)
        viewModel.onAction(UserEditionAction.StartEdition(userId))
    }

    LaunchedEffect(uiState.savedUserId) {
        if (uiState.savedUserId == userId) {
            onNavigateBack()
        }
    }

    UserEditionScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
private fun UserEditionScreen(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            AppTopBar(
                modifier = Modifier.statusBarsPadding(),
                isDividerVisible = false,
                start = {
                    AppIconButton(
                        icon = YamsIcons.ChevronLeft,
                        contentDescription = stringResource(Res.string.edition_back_cd),
                        onClick = onNavigateBack,
                    )
                },
                center = {
                    Text(
                        text = stringResource(Res.string.edition_title),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = YamsTheme.colors.brown,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                YamsPrimaryButton(
                    enabled = uiState.name.isNotBlank(),
                    text = stringResource(Res.string.edition_save),
                    onClick = { onAction(UserEditionAction.SaveUser) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            UserEditionContent(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

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
        UserEditionSheetContent(
            uiState = uiState,
            onAction = onAction,
            onDeleteUser = onDeleteUser,
            onDismissRequest = ::dismissSheet,
            isEdit = isEdit,
        )
    }
}

@Composable
private fun UserEditionSheetContent(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    onDeleteUser: (UserId) -> Unit,
    onDismissRequest: () -> Unit,
    isEdit: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
    ) {
        UserEditionContent(
            uiState = uiState,
            onAction = onAction,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
        )

        UserEditionActions(
            uiState = uiState,
            onAction = onAction,
            onDeleteUser = onDeleteUser,
            onDismissRequest = onDismissRequest,
            isEdit = isEdit,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun UserEditionContent(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .verticalScroll(scrollState)
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
    }
}

@Composable
private fun UserEditionActions(
    uiState: UserEditionUiState,
    onAction: (UserEditionAction) -> Unit,
    onDeleteUser: (UserId) -> Unit,
    onDismissRequest: () -> Unit,
    isEdit: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        YamsPrimaryButton(
            enabled = uiState.name.isNotBlank(),
            text = if (isEdit) {
                stringResource(Res.string.edition_save)
            } else {
                stringResource(Res.string.edition_create)
            },
            onClick = {
                onDismissRequest()
                onAction(UserEditionAction.SaveUser)
            }
        )

        if (isEdit && uiState.userId != null) {
            YamsDestructiveButton(
                text = stringResource(Res.string.edition_delete),
                onClick = {
                    onDismissRequest()
                    onDeleteUser(uiState.userId)
                }
            )
        }
    }
}

@YamsStoreScreenshotPreviews
@Composable
private fun UserEditionScreenPreview() {
    UserEditionStoreScreenshotContent()
}

@Preview(showBackground = true)
@Composable
private fun UserEditionSheetContentPreview() {
    YamsTheme {
        UserEditionSheetContent(
            uiState = UserEditionUiState(
                name = "Camille",
                avatar = Avatar.Drawable(AppAvatars[1]),
            ),
            onAction = {},
            onDeleteUser = {},
            onDismissRequest = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
public fun UserEditionStoreScreenshotContent() {
    YamsTheme {
        UserEditionScreen(
            uiState = UserEditionUiState(
                userId = UserId("store-preview-alpha"),
                name = "Alpha",
                avatar = Avatar.Drawable(AppAvatars[0]),
            ),
            onAction = {},
            onNavigateBack = {},
            modifier = Modifier.fillMaxSize(),
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
