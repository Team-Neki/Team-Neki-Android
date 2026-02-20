import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.neki.android.application)
    alias(libs.plugins.neki.android.application.compose)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("neki_key_store.jks")
            storePassword = properties["STORE_PASSWORD"].toString()
            keyAlias = properties["KEY_ALIAS"].toString()
            keyPassword = properties["KEY_PASSWORD"].toString()
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            val naverMapClientId = properties["NAVER_MAP_DEV_CLIENT_ID"].toString()
            buildConfigField("String", "NAVER_MAP_CLIENT_ID", naverMapClientId)

            val kakaoKey = properties["KAKAO_DEV_NATIVE_APP_KEY"].toString()
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey.trim('"')
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", kakaoKey)
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            val naverMapClientId = properties["NAVER_MAP_CLIENT_ID"].toString()
            buildConfigField("String", "NAVER_MAP_CLIENT_ID", naverMapClientId)

            val kakaoKey = properties["KAKAO_NATIVE_APP_KEY"].toString()
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey.trim('"')
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", kakaoKey)
        }
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
}
