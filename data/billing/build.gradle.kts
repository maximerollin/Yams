plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.preference)
            implementation(libs.koin.core)
        }

        getByName("mobileMain").dependencies {
            implementation(libs.revenuecat.purchases.core)
            implementation(libs.revenuecat.purchases.result)
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
}
