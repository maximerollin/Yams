plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.data.game)
            implementation(projects.data.preference)
            implementation(projects.core.review)
            implementation(libs.coil.compose)
        }
    }
}
