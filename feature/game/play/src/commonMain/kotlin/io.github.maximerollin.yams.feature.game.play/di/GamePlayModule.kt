package io.github.maximerollin.yams.feature.game.play.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.preference.di.dataPreferenceModule
import io.github.maximerollin.yams.feature.game.play.GamePlayViewModel
import io.github.maximerollin.yams.feature.game.play.domain.GetGamePlayStateUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

public val featureGamePlayModule: Module = module {
    includes(dataGameModule, dataPreferenceModule)
    viewModelOf(::GamePlayViewModel)
    factory { GetGamePlayStateUseCase(get(), get(named("Default"))) }
}
