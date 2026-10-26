plugins {
  `kotlin-dsl`
}

group = "io.github.maximerollin.yams.buildlogic"

dependencies {
    compileOnly(libs.android.tools.gradle.api)
    compileOnly(libs.jetbrains.compose.gradle.plugin)
    compileOnly(libs.kotlin.compose.gradle.plugin)
    compileOnly(libs.kotlin.multiplatform.gradle.plugin)
}

gradlePlugin {
  plugins { 
      register("kotlin-multiplatform") {
          id = libs.plugins.yams.kotlin.multiplatform.get().pluginId
          implementationClass = "KotlinMultiplatformConventionPlugin"
      }
      register("compose-multiplatform") {
          id = libs.plugins.yams.compose.multiplatform.get().pluginId
          implementationClass = "ComposeMultiplatformConventionPlugin"
      }
      register("feature") {
          id = libs.plugins.yams.feature.get().pluginId
          implementationClass = "FeatureConventionPlugin"
      }
  }
}
