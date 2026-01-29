plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.android.library.compose)
    alias(libs.plugins.neki.hilt)
}

android {
    namespace = "com.neki.android.core.common"
}

dependencies {
    api(libs.timber)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.core.ktx)

}
