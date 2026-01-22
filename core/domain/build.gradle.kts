plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.hilt)
}

android {
    namespace = "com.neki.android.core.domain"
}

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.model)
}
