plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.neki.hilt)
}

android {
    namespace = "com.neki.android.core.data"
}

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.annotation.experimental)
}