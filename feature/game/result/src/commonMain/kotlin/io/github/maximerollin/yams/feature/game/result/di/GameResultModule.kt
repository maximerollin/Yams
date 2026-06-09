package io.github.maximerollin.yams.feature.game.result.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.preference.di.dataPreferenceModule
import io.github.maximerollin.yams.feature.game.result.GameResultViewModel
import io.github.maximerollin.yams.feature.game.result.domain.FinishGameUseCase
import io.github.maximerollin.yams.feature.game.result.domain.GetGameResultPreviewUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

public val featureGameResultModule: Module = module {
    includes(dataGameModule, dataPreferenceModule)
    viewModelOf(::GameResultViewModel)
    factory { GetGameResultPreviewUseCase(get(), get(named("Default"))) }
    factoryOf(::FinishGameUseCase)
}
