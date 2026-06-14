package io.github.maximerollin.yams.data.billing.di

import io.github.maximerollin.yams.data.billing.BillingRepository
import io.github.maximerollin.yams.data.billing.RevenueCatBillingRepository
import io.github.maximerollin.yams.data.preference.di.dataPreferenceModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val dataBillingModule: Module = module {
    includes(dataPreferenceModule)

    // Repository
    singleOf(::RevenueCatBillingRepository) bind BillingRepository::class
}
