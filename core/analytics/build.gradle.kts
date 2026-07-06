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
            // Ktor engine used by logsnag-kotlin's Android worker.
            implementation(libs.ktor.client.okhttp)

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
