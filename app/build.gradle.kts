plugins {
    alias(libs.plugins.neki.android.application)
}

android {
    namespace = "com.neki.android.app"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.dataApi)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.feature.sample)
}