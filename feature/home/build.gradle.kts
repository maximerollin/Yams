plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.game)
            implementation(projects.feature.user.common)

            implementation(libs.coil.compose)

        }
    }
}
