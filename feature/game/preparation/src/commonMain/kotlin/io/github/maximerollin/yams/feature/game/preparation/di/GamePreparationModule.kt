package io.github.maximerollin.yams.feature.game.preparation.di

import io.github.maximerollin.yams.data.game.di.dataGameModule
import io.github.maximerollin.yams.data.preference.di.dataPreferenceModule
import io.github.maximerollin.yams.data.user.di.dataUserModule
import org.koin.core.module.Module
import org.koin.dsl.module

public val featureGamePreparationModule: Module = module {
    includes(dataGameModule, dataUserModule, dataPreferenceModule)
}
