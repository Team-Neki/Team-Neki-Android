plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.archive.impl"
}

dependencies {
    implementation(projects.feature.archive.api)
    
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
