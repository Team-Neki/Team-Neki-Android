plugins {
    alias(libs.plugins.neki.android.application)
}

android {
    namespace = "com.neki.android.app"
}

dependencies {
    implementation(projects.feature.sample)
}