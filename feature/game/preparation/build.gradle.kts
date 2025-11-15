plugins {
  alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Data
            api(projects.data.game)

            // Coil
            implementation(libs.coil.compose)

            // Reorderable
            implementation((libs.reorderable))
        }
    }
}


