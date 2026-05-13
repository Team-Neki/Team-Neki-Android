plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.hilt)
}

android {
    namespace = "com.neki.android.core.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
