plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.photo_upload.impl"
}

dependencies {
    implementation(projects.feature.photoUpload.api)

    implementation(libs.androidx.activity.compose)
    implementation(libs.mlkit.barcode.scanning)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.androidx.camera.compose)
}
