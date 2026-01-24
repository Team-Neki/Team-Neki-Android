plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.android.library.compose)
}

android {
    namespace = "com.neki.android.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
