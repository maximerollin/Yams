plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.model)
            implementation(libs.koin.core)
            implementation(libs.filekit.core)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.serialization.json)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-opt-in=kotlin.time.ExperimentalTime")
    }
}