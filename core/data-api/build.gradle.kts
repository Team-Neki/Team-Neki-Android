plugins {
    alias(libs.plugins.neki.kotlin.library)
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.androidx.datastore.preferences)
}