plugins {
    alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.data.billing)
            implementation(projects.data.game)
            implementation(projects.feature.user.common)

            implementation(libs.coil.compose)

        }
    }
}
