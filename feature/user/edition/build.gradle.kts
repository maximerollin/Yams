plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Data
            implementation(projects.data.user)

            // Coil
            implementation(libs.coil.compose)

            // FileKit
            implementation(libs.filekit.dialogs.compose)
        }
    }
}