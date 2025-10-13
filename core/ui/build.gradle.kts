plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
    alias(libs.plugins.yams.compose.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            api(projects.core.designsystem)
            api(projects.core.model)

            // FileKit
            api(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)

            // Coil
            implementation(libs.coil.compose)

        }
    }
}

compose.resources {
    publicResClass = true
}
