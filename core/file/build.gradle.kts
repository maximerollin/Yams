plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Koin
            implementation(libs.koin.core)

            // FileKit
            api(libs.filekit.core)
        }
    }
}