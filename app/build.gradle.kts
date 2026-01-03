import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.neki.android.application)
    alias(libs.plugins.neki.android.application.compose)
}

android {
    namespace = "com.neki.android.app"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "NAVER_MAP_CLIENT_ID",
            getLocalPropertiesValue("NAVER_MAP_CLIENT_ID"),
        )
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
    implementation(projects.feature.pose.api)
    implementation(projects.feature.pose.impl)
    implementation(projects.feature.archive.api)
    implementation(projects.feature.archive.impl)
    implementation(projects.feature.map.api)
    implementation(projects.feature.map.impl)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
}

fun getLocalPropertiesValue(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}
