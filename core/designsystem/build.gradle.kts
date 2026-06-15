plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
    alias(libs.plugins.yams.compose.multiplatform)
}

compose.resources {
    publicResClass = true
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.confettikit)
        }
    }
}
