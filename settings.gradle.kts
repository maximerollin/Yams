rootProject.name = "Yams"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app")
include(":core:database")
include(":core:designsystem")
include(":core:file")
include(":core:model")
include(":core:ui")
include(":data:game")
include(":data:preference")
include(":data:user")
include(":feature:home")
include(":feature:game:creation")
include(":feature:game:preparation")
include(":feature:game:play")
include(":feature:game:result")
include(":feature:user:edition")
include(":feature:user:common")
include(":feature:user:users")
include(":feature:user:profile")
include(":feature:user:history")
include(":feature:welcome")
