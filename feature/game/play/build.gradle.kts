plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.data.billing)
            api(projects.data.game)
            implementation(projects.data.preference)
            implementation(libs.coil.compose)
            implementation(libs.compottie.lite)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.guava)
            implementation(libs.litert)
            implementation(libs.litert.gpu)
            implementation(libs.litert.gpu.api)
        }
    }
}
