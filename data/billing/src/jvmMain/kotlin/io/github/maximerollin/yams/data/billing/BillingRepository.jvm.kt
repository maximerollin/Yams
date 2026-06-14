package io.github.maximerollin.yams.data.billing

import io.github.maximerollin.yams.data.billing.mock.AppProductMocks
import io.github.maximerollin.yams.data.billing.model.AppPackage
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal actual class RevenueCatBillingRepository actual constructor(
    private val preferenceRepository: PreferenceRepository,
    private val coroutineScope: CoroutineScope,
) : BillingRepository {
    private var isYamsPlusSubscribed = false

    actual override fun getYamsPlusStatus(): Flow<Boolean> =
        preferenceRepository.getYamsPlusStatus()
            .onStart {
                coroutineScope.launch {
                    fetchYamsPlusStatus()
                }
            }

    actual override suspend fun fetchYamsPlusStatus() {
        preferenceRepository.setYamsPlusStatus(isYamsPlusSubscribed)
    }

    actual override suspend fun fetchPackages(): Result<List<AppPackage>> {
        return Result.success(listOf(AppPackage(product = AppProductMocks.yamsPlus)))
    }

    actual override suspend fun purchase(appPackage: AppPackage, fromScreen: String): Result<Unit> {
        delay(1200)
        isYamsPlusSubscribed = true
        preferenceRepository.setYamsPlusStatus(true)
        return Result.success(Unit)
    }

    actual override suspend fun restorePurchases(fromScreen: String): Result<Boolean> {
        delay(1200)
        preferenceRepository.setYamsPlusStatus(isYamsPlusSubscribed)
        return Result.success(isYamsPlusSubscribed)
    }
}
