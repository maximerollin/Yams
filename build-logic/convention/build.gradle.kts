plugins {
  `kotlin-dsl`
}

group = "io.github.maximerollin.yams.buildlogic"

dependencies {
  compileOnly(libs.plugins.androidApplication.toDep())
  compileOnly(libs.plugins.kotlinMultiplatform.toDep())
  compileOnly(libs.plugins.composeMultiplatform.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
  "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
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
