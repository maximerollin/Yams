package io.github.maximerollin.yams.core.analytics.di

import io.github.maximerollin.yams.core.analytics.AnalyticsTracker
import io.github.maximerollin.yams.core.analytics.PlatformAnalyticsTracker
import org.koin.core.module.Module
import org.koin.dsl.module

public val analyticsModule: Module = module {
    single<AnalyticsTracker> { PlatformAnalyticsTracker() }
}
