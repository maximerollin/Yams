import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.buildConfig)
}

val yamsVersionName = providers.gradleProperty("YAMS_VERSION_NAME")
    .orElse("1.0.0")

kotlin {
    applyDefaultHierarchyTemplate()

    // Toolchain
    jvmToolchain(17)

    // Android Kotlin Multiplatform Library
    android {
        namespace = "io.github.maximerollin.yams.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources.enable = true
        withHostTest {}

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    iosArm64().binaries.framework {
        baseName = "YamsApp"
        isStatic = true
    }

    // JVM
    jvm()

    sourceSets {
        val jvmMain = getByName("jvmMain")

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.uiToolingPreview)

            implementation(projects.core.analytics)
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
            implementation(projects.feature.paywall)

            implementation(libs.androidx.navigation)
            implementation(libs.koin.compose)

            implementation(libs.coil.compose)
            implementation(libs.filekit.coil)
        }

        androidMain.dependencies {
            implementation(libs.jetbrains.compose.uiTooling)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        val mobileMain = create("mobileMain") {
            dependsOn(commonMain.get())
            dependencies {
                // RevenueCat (configured by each mobile platform entry point)
                implementation(libs.revenuecat.purchases.core)
            }
        }
        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)
    }
}

compose.desktop {
    application {
        mainClass = "io.github.maximerollin.yams.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.maximerollin.yams"
            packageVersion = "1.0.0"
        }
    }
}



val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

buildConfig {
    className("YamsBuildConfig")
    packageName("io.github.maximerollin.yams")
    useKotlinOutput { internalVisibility = false }

    // Empty when absent from local.properties -> RevenueCat configuration is skipped.
    val revenueCatPlayStoreApiKey = localProperties.getProperty("REVENUECAT_PLAY_STORE_API_KEY").orEmpty()
    val revenueCatAppStoreApiKey = localProperties.getProperty("REVENUECAT_APP_STORE_API_KEY").orEmpty()
    buildConfigField("REVENUECAT_PLAY_STORE_API_KEY", revenueCatPlayStoreApiKey)
    buildConfigField("REVENUECAT_APP_STORE_API_KEY", revenueCatAppStoreApiKey)

    // Empty when absent from local.properties -> PostHog configuration is skipped.
    val postHogApiKey = localProperties.getProperty("POSTHOG_API_KEY").orEmpty()
    val postHogHost = localProperties.getProperty("POSTHOG_HOST", "https://eu.i.posthog.com")
    val analyticsReleaseChannel = localProperties.getProperty("ANALYTICS_RELEASE_CHANNEL", "local")
    buildConfigField("VERSION_NAME", yamsVersionName.get())
    buildConfigField("POSTHOG_API_KEY", postHogApiKey)
    buildConfigField("POSTHOG_HOST", postHogHost)
    buildConfigField("ANALYTICS_RELEASE_CHANNEL", analyticsReleaseChannel)

    // Empty when absent from local.properties -> LogSnag mirroring is skipped.
    val logSnagToken = localProperties.getProperty("LOGSNAG_TOKEN").orEmpty()
    val logSnagProject = localProperties.getProperty("LOGSNAG_PROJECT").orEmpty()
    buildConfigField("LOGSNAG_TOKEN", logSnagToken)
    buildConfigField("LOGSNAG_PROJECT", logSnagProject)
}
