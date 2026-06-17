package io.github.maximerollin.yams.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.toAnalyticsCountBucket
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.billing.model.AppPackage
import io.github.maximerollin.yams.data.game.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PaywallViewModel(
    private val fromScreen: String,
    private val billingRepository: BillingRepository,
    private val gameRepository: GameRepository,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {
    private val _yamsPlusResult = MutableStateFlow<Result<AppPackage>?>(null)
    val yamsPlusResult: StateFlow<Result<AppPackage>?> = _yamsPlusResult.asStateFlow()

    private val _purchaseLoading = MutableStateFlow(false)
    val purchaseLoading: StateFlow<Boolean> = _purchaseLoading.asStateFlow()

    private val _purchaseSucceeded = MutableStateFlow(false)
    val purchaseSucceeded: StateFlow<Boolean> = _purchaseSucceeded.asStateFlow()

    private var hasTrackedPaywallView = false

    fun loadProducts() {
        trackPaywallViewed()

        viewModelScope.launch {
            var result: Result<List<AppPackage>>
            do {
                result = billingRepository.fetchPackages()
                if (result.isFailure) {
                    result.exceptionOrNull()?.let { exception ->
                        _yamsPlusResult.update { Result.failure(exception) }
                    }
                    delay(1000) // Wait 1 second before retry
                }
            } while (result.isFailure)

            result.onSuccess {
                it.firstOrNull()?.let { pkg ->
                    _yamsPlusResult.update { Result.success(pkg) }
                } ?: run {
                    _yamsPlusResult.update { Result.failure(Exception("No products found")) }
                }
            }
        }
    }

    fun purchaseYamsPlus() {
        val pkg = _yamsPlusResult.value?.getOrNull() ?: return

        viewModelScope.launch {
            analyticsTracker.capture(
                event = "purchase started",
                properties = pkg.paywallProperties(),
            )
            _purchaseLoading.value = true
            billingRepository.purchase(pkg, fromScreen)
                .onSuccess {
                    analyticsTracker.capture(
                        event = "purchase succeeded",
                        properties = pkg.paywallProperties(),
                    )
                    _purchaseSucceeded.value = true
                }
                .onFailure { error ->
                    analyticsTracker.capture(
                        event = "purchase failed",
                        properties = pkg.paywallProperties() + mapOf(
                            "error_type" to error::class.simpleName,
                        ),
                    )
                }
            _purchaseLoading.value = false
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            analyticsTracker.capture(
                event = "purchase restore started",
                properties = mapOf(
                    "from_screen" to fromScreen,
                ),
            )
            _purchaseLoading.value = true
            billingRepository.restorePurchases(fromScreen)
                .onSuccess { isSubscribed ->
                    analyticsTracker.capture(
                        event = "purchase restore completed",
                        properties = mapOf(
                            "from_screen" to fromScreen,
                            "restored_subscription" to isSubscribed,
                        ),
                    )
                    if (isSubscribed) {
                        _purchaseSucceeded.value = true
                    }
                }
                .onFailure { error ->
                    analyticsTracker.capture(
                        event = "purchase restore failed",
                        properties = mapOf(
                            "from_screen" to fromScreen,
                            "error_type" to error::class.simpleName,
                        ),
                    )
                }
            _purchaseLoading.value = false
        }
    }

    fun onPurchaseSucceededHandled() {
        _purchaseSucceeded.value = false
    }

    private fun trackPaywallViewed() {
        if (hasTrackedPaywallView) return
        hasTrackedPaywallView = true

        viewModelScope.launch {
            analyticsTracker.capture(
                event = "paywall viewed",
                properties = mapOf(
                    "from_screen" to fromScreen,
                    "finished_games_bucket" to gameRepository
                        .getNumberOfFinishedGames()
                        .first()
                        .toAnalyticsCountBucket(),
                    "paywall_variant" to "default",
                ),
            )
        }
    }

    private fun AppPackage.paywallProperties(): Map<String, Any?> =
        mapOf(
            "from_screen" to fromScreen,
            "product_id" to product.id,
            "currency_code" to product.price.currencyCode,
        )
}
