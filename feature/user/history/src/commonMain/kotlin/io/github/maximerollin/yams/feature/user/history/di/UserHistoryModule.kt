package io.github.maximerollin.yams.feature.user.history.di

import io.github.maximerollin.yams.data.billing.di.dataBillingModule
import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.user.di.dataUserModule
import io.github.maximerollin.yams.feature.user.history.UserHistoryViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureUserHistoryModule: Module = module {
    includes(dataGameModule, dataUserModule, dataBillingModule)
    viewModelOf(::UserHistoryViewModel)
}
