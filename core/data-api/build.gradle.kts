plugins {
    alias(libs.plugins.neki.android.library)
}

android {
    namespace = "com.neki.android.core.dataapi"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.androidx.datastore.preferences)
}