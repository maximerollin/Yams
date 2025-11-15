plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            implementation(projects.core.database)
            implementation(projects.core.file)
            api(projects.core.model)

            // Koin
            implementation(libs.koin.core)
        }
    }
}