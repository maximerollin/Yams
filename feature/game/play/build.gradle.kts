plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            api(projects.data.game)
            implementation(projects.data.preference)
            implementation(libs.coil.compose)
            implementation(libs.compottie.lite)
        }
    }
}
