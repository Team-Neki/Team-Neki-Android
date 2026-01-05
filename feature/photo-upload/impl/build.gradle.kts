plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.photo_upload.impl"
}

dependencies {
    implementation(projects.feature.photoUpload.api)
}
