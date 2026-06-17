plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            implementation(projects.core.analytics)

            // Data
            implementation(projects.data.billing)
            implementation(projects.data.game)
        }
    }
}
