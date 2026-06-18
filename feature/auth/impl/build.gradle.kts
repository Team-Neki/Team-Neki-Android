plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.auth.impl"
}

dependencies {
    implementation(libs.androidx.activity.compose)

    implementation(projects.feature.auth.api)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.kotlinx.coroutines.play.services)
}
