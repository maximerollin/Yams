import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "io.github.maximerollin.yams.screenshots"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.welcome)
    implementation(projects.feature.home)
    implementation(projects.feature.game.creation)
    implementation(projects.feature.game.preparation)
    implementation(projects.feature.game.play)
    implementation(projects.feature.game.result)
    implementation(projects.feature.user.edition)
    implementation(projects.feature.user.users)
    implementation(projects.feature.user.profile)
    implementation(projects.feature.user.history)

    implementation(libs.jetbrains.compose.runtime)
    implementation(libs.jetbrains.compose.ui)
    implementation(libs.jetbrains.compose.foundation)
    implementation(libs.jetbrains.compose.material3)
    implementation(libs.jetbrains.compose.components.resources)

    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.coil.compose)
    screenshotTestImplementation(libs.jetbrains.compose.uiTooling)
}
