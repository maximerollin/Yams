package io.github.maximerollin.yams.feature.user.profile.di

import io.github.maximerollin.yams.data.billing.di.dataBillingModule
import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.user.di.dataUserModule
import io.github.maximerollin.yams.feature.user.profile.UserProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureUserProfileModule: Module = module {
    includes(dataBillingModule, dataGameModule, dataUserModule)
    viewModelOf(::UserProfileViewModel)
}
