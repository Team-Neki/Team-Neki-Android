plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.map.impl"
}

dependencies {
    implementation(projects.feature.map.api)
    api(libs.map.sdk)

}
