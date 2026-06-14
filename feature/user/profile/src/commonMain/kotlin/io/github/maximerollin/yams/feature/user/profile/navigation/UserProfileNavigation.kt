package io.github.maximerollin.yams.feature.user.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.profile.UserProfileRoute as UserProfileScreenRoute
import kotlinx.serialization.Serializable

@Serializable
public data class UserProfileRoute(val userId: String)

public fun NavController.navigateToUserProfile(
    userId: UserId,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(UserProfileRoute(userId.value), builder = builder)
}

public fun NavGraphBuilder.userProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToUserEdition: (UserId) -> Unit,
    onNavigateToUserHistory: (UserId) -> Unit,
    onNavigateToPaywall: () -> Unit,
) {
    composable<UserProfileRoute> {
        UserProfileScreenRoute(
            userId = UserId(it.toRoute<UserProfileRoute>().userId),
            onNavigateBack = onNavigateBack,
            onNavigateToUsers = onNavigateToUsers,
            onNavigateToUserEdition = onNavigateToUserEdition,
            onNavigateToUserHistory = onNavigateToUserHistory,
            onNavigateToPaywall = onNavigateToPaywall,
        )
    }
}
