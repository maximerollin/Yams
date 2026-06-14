package io.github.maximerollin.yams.data.billing

import io.github.maximerollin.yams.data.billing.model.AppPackage
import io.github.maximerollin.yams.data.preference.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

public interface BillingRepository {
    public fun getYamsPlusStatus(): Flow<Boolean>
    public suspend fun fetchYamsPlusStatus()
    public suspend fun fetchPackages(): Result<List<AppPackage>>
    public suspend fun purchase(appPackage: AppPackage, fromScreen: String): Result<Unit>
    public suspend fun restorePurchases(fromScreen: String): Result<Boolean>
}

internal expect class RevenueCatBillingRepository(
    preferenceRepository: PreferenceRepository,
    coroutineScope: CoroutineScope,
) : BillingRepository {
    override fun getYamsPlusStatus(): Flow<Boolean>

    override suspend fun fetchYamsPlusStatus()
    override suspend fun fetchPackages(): Result<List<AppPackage>>
    override suspend fun purchase(appPackage: AppPackage, fromScreen: String): Result<Unit>
    override suspend fun restorePurchases(fromScreen: String): Result<Boolean>
}
