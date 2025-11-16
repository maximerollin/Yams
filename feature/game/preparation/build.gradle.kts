plugins {
  alias(libs.plugins.yams.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Data
            api(projects.data.game)
            api(projects.data.user)
            api(projects.data.preference)

            // Coil
            implementation(libs.coil.compose)

            // Reorderable
            implementation((libs.reorderable))
        }
    }
}


