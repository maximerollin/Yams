plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Koin
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            // PostHog
            implementation(libs.posthog.android)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}
