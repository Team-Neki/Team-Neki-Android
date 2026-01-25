import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

val localPropertiesFile = project.rootProject.file("local.properties")
val properties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.neki.android.feature.photo_upload.impl"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "PHOTOISM_IMAGE_URL", properties["PHOTOISM_IMAGE_URL"].toString())
        buildConfigField("String", "PHOTOISM_IMG_URL_MIME_TYPE", properties["PHOTOISM_IMG_URL_MIME_TYPE"].toString())
        buildConfigField("String", "LIFE_FOUR_CUT_IMAGE_URL", properties["LIFE_FOUR_CUT_IMAGE_URL"].toString())
        buildConfigField("String", "LIFE_FOUR_CUT_URL_MIME_TYPE", properties["LIFE_FOUR_CUT_URL_MIME_TYPE"].toString())
    }
}

dependencies {
    implementation(projects.feature.photoUpload.api)
    implementation(projects.feature.archive.api)

    implementation(libs.androidx.activity.compose)
    implementation(libs.mlkit.barcode.scanning)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.compose)

}
