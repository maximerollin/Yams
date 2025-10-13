import io.github.maximerollin.yams.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("yams.kotlin.multiplatform").get().get().pluginId)
            apply(libs.findPlugin("yams.compose.multiplatform").get().get().pluginId)
            apply(libs.findPlugin("kotlinSerialization").get().get().pluginId)
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain.dependencies {
                    // Core
                    implementation(project(":core:designsystem"))
                    implementation(project(":core:ui"))

                    // Navigation
                    implementation(libs.findLibrary("androidx.navigation").get())
                    implementation(libs.findLibrary("kotlinx.serialization.json").get())

                    // Lifecycle
                    implementation(libs.findLibrary("androidx.lifecycle.runtime.compose").get())

                    // Koin
                    implementation(libs.findLibrary("koin.core.annotations").get())
                    implementation(libs.findLibrary("koin.compose").get())
                    implementation(libs.findLibrary("koin.compose.viewmodel").get())
                }
            }
        }
    }
}
