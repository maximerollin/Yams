package io.github.maximerollin.yams.feature.home.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.feature.home.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureHomeModule: Module = module {
    includes(dataGameModule)
    viewModelOf(::HomeViewModel)
}
