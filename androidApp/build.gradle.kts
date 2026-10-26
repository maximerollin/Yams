import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

val yamsVersionCode = providers.gradleProperty("YAMS_VERSION_CODE")
    .map(String::toInt)
    .orElse(1)
val yamsVersionName = providers.gradleProperty("YAMS_VERSION_NAME")
    .orElse("1.0.0")

android {
    namespace = "io.github.maximerollin.yams"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.maximerollin.yams"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = yamsVersionCode.get()
        versionName = yamsVersionName.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["firebase_analytics_collection_enabled"] = false
            manifestPlaceholders["firebase_crashlytics_collection_enabled"] = false
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            manifestPlaceholders["firebase_analytics_collection_enabled"] = true
            manifestPlaceholders["firebase_crashlytics_collection_enabled"] = true
        }
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
    implementation(projects.app)
    implementation(projects.core.analytics)
    implementation(projects.core.review)

    implementation(libs.jetbrains.compose.runtime)
    implementation(libs.jetbrains.compose.foundation)
    implementation(libs.jetbrains.compose.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.startup)
    implementation(libs.androidx.startup)
    implementation(libs.filekit.core)
    implementation(libs.revenuecat.purchases.core)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}
