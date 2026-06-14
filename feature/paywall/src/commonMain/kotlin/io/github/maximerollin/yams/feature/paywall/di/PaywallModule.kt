package io.github.maximerollin.yams.feature.paywall.di

import io.github.maximerollin.yams.data.billing.di.dataBillingModule
import io.github.maximerollin.yams.feature.paywall.PaywallViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featurePaywallModule: Module = module {
    includes(dataBillingModule)
    viewModelOf(::PaywallViewModel)
}
