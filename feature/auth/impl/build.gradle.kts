plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.auth.impl"
}

dependencies {
    implementation(libs.androidx.activity.compose)

    implementation(projects.feature.auth.api)
    api(libs.kakao.user)
}
