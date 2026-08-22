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

fun metaProperty(name: String): String = (
    properties.getProperty(name)
        ?: providers.gradleProperty(name).orNull
        ?: error("$name must be configured in local.properties or as a Gradle property")
    )
    .trim()
    .removeSurrounding("\"")
    .takeIf { it.isNotEmpty() }
    ?: error("$name must not be empty")

android {
    namespace = "com.neki.android.app"

    defaultConfig {
        resValue("string", "facebook_app_id", metaProperty("META_APP_ID"))
        resValue("string", "facebook_client_token", metaProperty("META_CLIENT_TOKEN"))
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        (project.findProperty("versionCode") as String?)?.toIntOrNull()?.let { versionCode = it }
        (project.findProperty("versionName") as String?)?.let { versionName = it }
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
            // CI(Firebase 배포)는 -PversionName으로 완성된 값을 넘기므로, Gradle Property가 없는 로컬 빌드에서만 suffix를 붙인다.
            if (project.findProperty("versionName") == null) {
                versionNameSuffix = "-dev"
            }

            signingConfig = signingConfigs.getByName("release")

            val naverMapClientId = properties["NAVER_MAP_DEV_CLIENT_ID"].toString()
            buildConfigField("String", "NAVER_MAP_CLIENT_ID", naverMapClientId)

            val kakaoKey = properties["KAKAO_DEV_NATIVE_APP_KEY"].toString()
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey.trim('"')
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", kakaoKey)

            val amplitudeApiKey = properties["AMPLITUDE_DEV_API_KEY"].toString()
            buildConfigField("String", "AMPLITUDE_API_KEY", amplitudeApiKey)
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

            val amplitudeApiKey = properties["AMPLITUDE_API_KEY"].toString()
            buildConfigField("String", "AMPLITUDE_API_KEY", amplitudeApiKey)
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
    implementation(projects.core.analytics)
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
    implementation(projects.feature.notification.api)
    implementation(projects.feature.notification.impl)
    implementation(projects.feature.photoUpload.api)
    implementation(projects.feature.photoUpload.impl)
    implementation(projects.feature.selectAlbum.api)
    implementation(projects.feature.selectAlbum.impl)

    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.facebook.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.hilt.lifecycle.viewModel.compose)
}
