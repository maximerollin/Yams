plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.game)
            implementation(libs.coil.compose)
            implementation(libs.confettikit)
        }
    }
}
