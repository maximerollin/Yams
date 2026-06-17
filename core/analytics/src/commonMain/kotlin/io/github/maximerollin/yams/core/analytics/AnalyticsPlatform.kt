package io.github.maximerollin.yams.core.analytics

internal fun String.withAnalyticsPlatformPrefix(platform: String): String =
    "$platform $this"

internal fun Map<String, Any?>.withAnalyticsPlatform(platform: String): Map<String, Any?> =
    this + ("platform" to platform)
