package io.github.maximerollin.yams.feature.user.edition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.core.ui.utils.AppAvatars
import io.github.maximerollin.yams.data.user.UserRepository
import io.github.maximerollin.yams.feature.user.edition.model.Avatar
import io.github.maximerollin.yams.feature.user.edition.model.toUserCreateAvatar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public class UserEditionViewModel(
    private val userRepository: UserRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserEditionUiState())
    public val uiState: StateFlow<UserEditionUiState> = _uiState.asStateFlow()

    public fun onAction(action: UserEditionAction) {
        when (action) {
            is UserEditionAction.EditName -> editUserName(action.name)
            is UserEditionAction.EditAvatar -> editUserAvatar(action.avatar)
            is UserEditionAction.StartEdition -> startUserEdition(action.userId)
            is UserEditionAction.ResetEdition -> resetEdition()
            is UserEditionAction.SaveUser -> saveUser()
        }
    }

    private fun editUserName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    public fun editUserAvatar(avatar: Avatar) {
        _uiState.value = _uiState.value.copy(avatar = avatar)
    }

    public fun startUserEdition(userId: UserId) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId).first() ?: return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    userId = user.id,
                    name = user.name,
                    avatar = user.avatar?.let { Avatar.File(it) }
                        ?: Avatar.Drawable(AppAvatars.random())
                )
            }
        }
    }

    private fun resetEdition() {
        _uiState.update { UserEditionUiState() }
    }

    private fun saveUser() {
        val (userId, name, avatar) = uiState.value

        if (name.isBlank()) return

        viewModelScope.launch {
            when (userId) {
                null -> {
                    val newUserId = userRepository.createUser(
                        name = name.trim(),
                        avatar = avatar.toUserCreateAvatar(),
                    )
                    _uiState.update {
                        it.copy(
                            savedNewUserId = newUserId,
                            savedUserId = newUserId,
                        )
                    }
                    analyticsTracker.capture(
                        event = "player created",
                        properties = mapOf(
                            "avatar_source" to avatar.toAnalyticsAvatarSource(),
                        ),
                    )
                }

                else -> {
                    userRepository.updateUser(
                        id = userId,
                        name = name.trim(),
                        avatar = avatar.toUserCreateAvatar(),
                    )
                    _uiState.update { it.copy(savedUserId = userId) }
                    analyticsTracker.capture(
                        event = "player updated",
                        properties = mapOf(
                            "avatar_source" to avatar.toAnalyticsAvatarSource(),
                        ),
                    )
                }
            }
        }
    }

}

private fun Avatar.toAnalyticsAvatarSource(): String = when (this) {
    is Avatar.Drawable -> "preset"
    is Avatar.File -> "file"
}

public data class UserEditionUiState(
    val userId: UserId? = null,
    val name: String = "",
    val avatar: Avatar = Avatar.Drawable(AppAvatars.random()),
    val savedNewUserId: UserId? = null,
    val savedUserId: UserId? = null,
)

public sealed interface UserEditionAction {
    public data class EditName(val name: String) : UserEditionAction
    public data class EditAvatar(val avatar: Avatar) : UserEditionAction
    public data class StartEdition(val userId: UserId) : UserEditionAction
    public object ResetEdition : UserEditionAction
    public object SaveUser : UserEditionAction
}
