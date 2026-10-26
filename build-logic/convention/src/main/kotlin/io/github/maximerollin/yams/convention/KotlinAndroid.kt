package io.github.maximerollin.yams.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    extension: KotlinMultiplatformAndroidLibraryExtension
) = extension.apply {
    val moduleName = path.split(":").drop(2).joinToString(".")
    namespace = when {
        moduleName.isNotEmpty() -> "io.github.maximerollin.yams.$moduleName"
        else -> "io.github.maximerollin.yams"
    }

    compileSdk = libs.findVersion("android.compileSdk").get().requiredVersion.toInt()
    minSdk = libs.findVersion("android.minSdk").get().requiredVersion.toInt()
    withHostTest {}
}
