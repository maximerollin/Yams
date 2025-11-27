package io.github.maximerollin.yams.feature.game.creation.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.user.di.dataUserModule
import io.github.maximerollin.yams.feature.game.creation.GameCreationViewModel
import io.github.maximerollin.yams.feature.user.edition.di.featureUserEditionModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public val featureGameCreationModule: Module = module {
    includes(dataUserModule, dataGameModule, featureUserEditionModule)
    viewModelOf(::GameCreationViewModel)
}