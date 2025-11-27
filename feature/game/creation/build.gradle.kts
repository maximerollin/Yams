plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Data
            implementation(projects.data.game)
            implementation(projects.data.user)
            implementation(projects.feature.user.edition)

            // Coil
            implementation(libs.coil.compose)

            // FileKit
            implementation(libs.filekit.dialogs.compose)
        }
    }
}


