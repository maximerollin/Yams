plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.data.game)
            implementation(libs.coil.compose)
        }
    }
}
