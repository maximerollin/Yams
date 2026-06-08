package io.github.maximerollin.yams.feature.user.edition.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.core.model.UserId
import io.github.maximerollin.yams.feature.user.edition.UserEditionRoute
import kotlinx.serialization.Serializable

@Serializable
public data class UserEditionRoute(val userId: String)

public fun NavController.navigateToUserEdition(
    userId: UserId,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(UserEditionRoute(userId.value), builder = builder)
}

public fun NavGraphBuilder.userEditionScreen(
    onNavigateBack: () -> Unit,
) {
    composable<UserEditionRoute> {
        UserEditionRoute(
            userId = UserId(it.toRoute<UserEditionRoute>().userId),
            onNavigateBack = onNavigateBack,
        )
    }
}
