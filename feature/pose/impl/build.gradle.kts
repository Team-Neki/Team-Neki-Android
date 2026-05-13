plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.pose.impl"
}

dependencies {
    implementation(projects.feature.pose.api)
    implementation(projects.feature.photoUpload.api)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.paging.compose)
    implementation(libs.zoomable)
}
