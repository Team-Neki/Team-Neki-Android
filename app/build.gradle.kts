import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.neki.android.application)
    alias(libs.plugins.neki.android.application.compose)
    alias(libs.plugins.oss.licenses)
}

val localPropertiesFile = project.rootProject.file("local.properties")
val properties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.neki.android.app"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "NAVER_MAP_CLIENT_ID", properties["NAVER_MAP_CLIENT_ID"].toString())
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            properties["KAKAO_NATIVE_APP_KEY"].toString()
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
    implementation(projects.core.ui)
    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.impl)
    implementation(projects.feature.pose.api)
    implementation(projects.feature.pose.impl)
    implementation(projects.feature.archive.api)
    implementation(projects.feature.archive.impl)
    implementation(projects.feature.map.api)
    implementation(projects.feature.map.impl)
    implementation(projects.feature.mypage.api)
    implementation(projects.feature.mypage.impl)
    implementation(projects.feature.photoUpload.api)
    implementation(projects.feature.photoUpload.impl)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
}
