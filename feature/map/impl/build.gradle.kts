plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.map.impl"
}

dependencies {
    implementation(projects.feature.map.api)
    implementation(projects.core.dataApi)
    api(libs.map.sdk)
    implementation(libs.naver.map.compose)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.compose)
}
