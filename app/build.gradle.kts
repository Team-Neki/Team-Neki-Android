plugins {
    alias(libs.plugins.neki.android.application)
    alias(libs.plugins.neki.android.application.compose)
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
    implementation(projects.feature.pose.api)
    implementation(projects.feature.pose.impl)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
}