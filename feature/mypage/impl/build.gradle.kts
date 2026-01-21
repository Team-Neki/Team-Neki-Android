plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.mypage.impl"
}

dependencies {
    implementation(projects.feature.mypage.api)
}
