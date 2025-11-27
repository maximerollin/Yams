import io.github.maximerollin.yams.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
            apply(libs.findPlugin("composeCompiler").get().get().pluginId)
        }

        val composeDeps = extensions.getByType<ComposeExtension>().dependencies

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                commonMain {
                    dependencies {
                        implementation(libs.findLibrary("jetbrains.compose.runtime").get())
                        implementation(libs.findLibrary("jetbrains.compose.foundation").get())
                        implementation(libs.findLibrary("jetbrains.compose.material3").get())
                        implementation(libs.findLibrary("jetbrains.compose.ui").get())
                        implementation(
                            libs.findLibrary("jetbrains.compose.components.resources").get()
                        )
                        implementation(libs.findLibrary("jetbrains.compose.uiToolingPreview").get())
                    }
                }

                androidMain.dependencies {
                    // @Preview
                    implementation(libs.findLibrary("jetbrains.compose.uiTooling").get())
                }

                getByName("jvmTest") {
                    dependencies {
                        implementation(composeDeps.desktop.currentOs)
                    }
                }
            }
        }
    }
}
