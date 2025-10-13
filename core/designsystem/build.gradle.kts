plugins {
    alias(libs.plugins.yams.kotlin.multiplatform)
    alias(libs.plugins.yams.compose.multiplatform)
}

compose.resources {
    publicResClass = true
}
