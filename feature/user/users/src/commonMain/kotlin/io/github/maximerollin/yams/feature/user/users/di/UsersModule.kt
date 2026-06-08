package io.github.maximerollin.yams.feature.user.users.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.user.di.dataUserModule
import io.github.maximerollin.yams.feature.user.users.UsersViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureUserUsersModule: Module = module {
    includes(dataGameModule, dataUserModule)
    viewModelOf(::UsersViewModel)
}
