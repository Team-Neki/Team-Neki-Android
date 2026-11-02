plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.hilt)
}

android {
    namespace = "com.neki.android.core.analytics"
}

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.amplitude.analytics.android)
    implementation(libs.facebook.core)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
