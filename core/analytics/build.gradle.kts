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

            // LogSnag (temporary analytics mirror)
            implementation(libs.logsnag.kotlin)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}
