plugins {
    alias(libs.plugins.neki.kotlin.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(libs.compose.stable.marker)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlinx.serialization.json)
}
