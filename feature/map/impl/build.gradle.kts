plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.map.impl"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(projects.feature.map.api)
}