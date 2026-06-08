import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.buildConfig)
}

kotlin {
    applyDefaultHierarchyTemplate()

    // Toolchain
    jvmToolchain(17)

    // Android
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "YamsApp"
            isStatic = true
        }
    }

    // JVM
    jvm()

    sourceSets {
        val jvmMain by getting

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.uiToolingPreview)

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

            implementation(libs.androidx.navigation)
            implementation(libs.koin.compose)

            implementation(libs.coil.compose)
            implementation(libs.filekit.coil)
        }

        androidMain.dependencies {
            implementation(libs.jetbrains.compose.uiTooling)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.startup)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        val mobileMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)
    }
}

android {
    namespace = "io.github.maximerollin.yams"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.maximerollin.yams"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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



buildConfig {
    className("YamsBuildConfig")
    packageName("io.github.maximerollin.yams")
    useKotlinOutput { internalVisibility = false }
}
