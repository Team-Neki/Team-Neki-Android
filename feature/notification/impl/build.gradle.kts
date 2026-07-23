plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.notification.impl"
}

dependencies {
    implementation(projects.feature.notification.api)
}
