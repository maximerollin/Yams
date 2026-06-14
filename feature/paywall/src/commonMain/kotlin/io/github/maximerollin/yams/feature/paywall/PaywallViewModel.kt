package io.github.maximerollin.yams.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.billing.model.AppPackage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PaywallViewModel(
    private val fromScreen: String,
    private val billingRepository: BillingRepository,
) : ViewModel() {
    val isYamsPlusActive: StateFlow<Boolean?> = billingRepository
        .getYamsPlusStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private val _yamsPlusResult = MutableStateFlow<Result<AppPackage>?>(null)
    val yamsPlusResult: StateFlow<Result<AppPackage>?> = _yamsPlusResult.asStateFlow()

    private val _purchaseLoading = MutableStateFlow(false)
    val purchaseLoading: StateFlow<Boolean> = _purchaseLoading.asStateFlow()

    fun loadProducts() {
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
            _purchaseLoading.value = true
            billingRepository.purchase(pkg, fromScreen)
            _purchaseLoading.value = false
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            _purchaseLoading.value = true
            billingRepository.restorePurchases(fromScreen)
            _purchaseLoading.value = false
        }
    }
}
