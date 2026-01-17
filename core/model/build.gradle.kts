plugins {
    alias(libs.plugins.neki.kotlin.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    api(libs.kotlinx.datetime)
}
