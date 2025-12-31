plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.pose.impl"
}

dependencies {
    implementation(projects.feature.pose.api)
}