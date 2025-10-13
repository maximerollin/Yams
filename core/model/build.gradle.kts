plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // FileKit
            api(libs.filekit.core)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
