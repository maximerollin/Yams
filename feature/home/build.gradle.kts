plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.game)
            implementation(projects.data.user)

            implementation(libs.coil.compose)

        }
    }
}
