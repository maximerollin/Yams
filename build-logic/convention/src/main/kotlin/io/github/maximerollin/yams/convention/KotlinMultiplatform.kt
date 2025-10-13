package io.github.maximerollin.yams.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    val moduleName = path.split(":").drop(2).joinToString(".")

    explicitApi()
    applyDefaultHierarchyTemplate()

    // Toolchain
    jvmToolchain(17)

    // Android
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = moduleName
            isStatic = true
        }
    }

    // JVM
    jvm()

    //common dependencies
    sourceSets.apply {
        commonMain {
            dependencies {
                // Coroutines
                implementation(libs.findLibrary("kotlinx-coroutines-core").get())

                // Logger
                implementation(libs.findLibrary("napier").get())
            }
        }

        commonTest.dependencies {
            // Test
            implementation(libs.findLibrary("kotlin-test").get())
            implementation(libs.findLibrary("kotlinx-coroutines-test").get())
        }

        // Create a new source set for mobileMain
        create("mobileMain") { dependsOn(commonMain.get()) }
        androidMain.get().dependsOn(getByName("mobileMain"))
        iosMain.get().dependsOn(getByName("mobileMain"))
    }
}
