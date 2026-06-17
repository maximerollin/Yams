package io.github.maximerollin.yams.data.billing

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesDelegate
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreProduct
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.result.awaitCustomerInfoResult
import com.revenuecat.purchases.kmp.result.awaitOfferingsResult
import com.revenuecat.purchases.kmp.result.awaitPurchaseResult
import com.revenuecat.purchases.kmp.result.awaitRestoreResult
import io.github.aakira.napier.Napier
import io.github.maximerollin.yams.data.billing.model.AppPackage
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal actual class RevenueCatBillingRepository actual constructor(
    private val preferenceRepository: PreferenceRepository,
    private val coroutineScope: CoroutineScope,
) : BillingRepository {
    init {
        configuredPurchases("init")?.delegate = object : PurchasesDelegate {
            override fun onCustomerInfoUpdated(customerInfo: CustomerInfo) {
                coroutineScope.launch {
                    Napier.d { "init - onCustomerInfoUpdated" }
                    val isSubscribed = getYamsPlusStatusFromCustomerInfo(customerInfo)
                    Napier.d { "init - onCustomerInfoUpdated - Yams+: $isSubscribed" }
                    preferenceRepository.setYamsPlusStatus(isSubscribed)
                }
            }

            override fun onPurchasePromoProduct(
                product: StoreProduct,
                startPurchase: ((PurchasesError, Boolean) -> Unit, (StoreTransaction, CustomerInfo) -> Unit) -> Unit
            ) {
                Napier.d { "init - onPurchasePromoProduct - product: $product" }
            }
        }
    }

    actual override fun getYamsPlusStatus(): Flow<Boolean> =
        preferenceRepository.getYamsPlusStatus()
            .onStart { fetchYamsPlusStatus() }

    actual override suspend fun fetchYamsPlusStatus() {
        coroutineScope.launch {
            val purchases = configuredPurchases("fetch Yams+ status") ?: run {
                preferenceRepository.setYamsPlusStatus(false)
                return@launch
            }

            purchases
                .awaitCustomerInfoResult()
                .onSuccess {
                    Napier.d { "fetch Yams+ status - success" }
                    val isSubscribed = getYamsPlusStatusFromCustomerInfo(it)
                    Napier.d { "fetch Yams+ status - Yams+: $isSubscribed" }
                    preferenceRepository.setYamsPlusStatus(isSubscribed)
                }
                .onFailure {
                    Napier.w { "fetch Yams+ status - Failed to get customer info: ${it.message}" }
                }
        }.join()
    }

    actual override suspend fun fetchPackages(): Result<List<AppPackage>> {
        val purchases = configuredPurchases("fetchPackages") ?: return Result.success(emptyList())

        return purchases
            .awaitOfferingsResult()
            .onFailure { Napier.w { "fetchPackages - Failed to get offerings: ${it.message}" } }
            .map { offerings ->
                offerings.current?.availablePackages
                    ?.takeUnless { it.isEmpty() }
                    ?.map { pkg -> AppPackage(revenueCatPackage = pkg) }
                    ?.also { Napier.d { "fetchPackages - Fetched packages: $it" } }
                    ?: emptyList()
            }
    }

    actual override suspend fun purchase(
        appPackage: AppPackage,
        fromScreen: String,
    ): Result<Unit> {
        return coroutineScope.async {
            val purchases = configuredPurchases("purchase")
                ?: return@async Result.failure(revenueCatNotConfiguredException())

            purchases
                .awaitPurchaseResult(packageToPurchase = appPackage.revenueCatPackage)
                .onSuccess {
                    Napier.i { "purchase - 🎉 Purchase successful: $it" }
                    val isSubscribed = getYamsPlusStatusFromCustomerInfo(it.customerInfo)
                    Napier.i { "purchase - Yams+: $isSubscribed" }
                    preferenceRepository.setYamsPlusStatus(true)
                }
                .onFailure {
                    Napier.w { "purchase - Purchase failed: ${it.message}" }
                }
                .map { }
        }.await()
    }

    actual override suspend fun restorePurchases(fromScreen: String): Result<Boolean> {
        return coroutineScope.async {
            val purchases = configuredPurchases("restorePurchases") ?: run {
                preferenceRepository.setYamsPlusStatus(false)
                return@async Result.success(false)
            }

            purchases.awaitRestoreResult()
                .onSuccess { customerInfo ->
                    Napier.i { "restorePurchases - success" }
                    val isSubscribed = getYamsPlusStatusFromCustomerInfo(customerInfo)
                    Napier.i { "restorePurchases - Yams+: $isSubscribed" }
                    preferenceRepository.setYamsPlusStatus(isSubscribed)
                }
                .onFailure {
                    Napier.w { "restorePurchases - Restore failed: ${it.message}" }
                }
                .map { customerInfo ->
                    getYamsPlusStatusFromCustomerInfo(customerInfo)
                }
        }.await()
    }

    private fun getYamsPlusStatusFromCustomerInfo(customerInfo: CustomerInfo?): Boolean {
        val isSubscribed = customerInfo?.entitlements[YAMS_PLUS_ENTITLEMENT_ID]?.isActive == true
        Napier.d { "get Yams+ from ci - active entitlements: ${customerInfo?.entitlements?.active?.isNotEmpty()}" }
        Napier.d { "get Yams+ from ci - Yams+ subscription active: $isSubscribed" }
        return isSubscribed
    }

    private fun configuredPurchases(operation: String): Purchases? =
        runCatching { Purchases.sharedInstance }
            .onFailure { exception ->
                Napier.w { "$operation - RevenueCat is not configured: ${exception.message}" }
            }
            .getOrNull()

    private fun revenueCatNotConfiguredException(): IllegalStateException =
        IllegalStateException("RevenueCat is not configured")

    private companion object {
        private const val YAMS_PLUS_ENTITLEMENT_ID = "plus"
    }
}
