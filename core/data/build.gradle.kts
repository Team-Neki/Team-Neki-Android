plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.neki.android.core.data"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}