plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Data
            implementation(projects.data.billing)
        }
    }
}
