package io.github.maximerollin.yams.feature.paywall.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.maximerollin.yams.feature.paywall.PaywallRoute
import kotlinx.serialization.Serializable

@Serializable
public data class PaywallRoute(
    val fromScreen: String,
)

public fun NavController.navigateToPaywall(
    fromScreen: String,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(PaywallRoute(fromScreen = fromScreen), builder = builder)
}

public fun NavGraphBuilder.paywallScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
) {
    composable<PaywallRoute> {
        val route = it.toRoute<PaywallRoute>()
        PaywallRoute(
            fromScreen = route.fromScreen,
            onNavigateBack = onNavigateBack,
            onNavigateHome = onNavigateHome,
        )
    }
}
