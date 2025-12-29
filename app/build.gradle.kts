plugins {
    alias(libs.plugins.neki.android.application)
}

android {
    namespace = "com.neki.android.app"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.dataApi)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.feature.sample.impl)
    implementation(projects.feature.sample.api)

    implementation(libs.timber)

}