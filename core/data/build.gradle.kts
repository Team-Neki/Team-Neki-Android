import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.neki.hilt)
}

val localPropertiesFile = project.rootProject.file("local.properties")
val properties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.neki.android.core.data"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", properties["BASE_URL"].toString())
    }
}

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.annotation.experimental)

    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
}
