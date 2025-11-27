package io.github.maximerollin.yams.feature.user.edition.di

import io.github.maximerollin.yams.data.user.di.dataUserModule
import io.github.maximerollin.yams.feature.user.edition.UserEditionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureUserEditionModule: Module = module {
    includes(dataUserModule)
    viewModelOf(::UserEditionViewModel)
}