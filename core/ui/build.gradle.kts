plugins {
    alias(libs.plugins.neki.android.library)
    alias(libs.plugins.neki.android.library.compose)
}


android {
    namespace = "com.neki.android.core.ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.collections.immutable)

    api(libs.coil.compose)
    api(libs.coil.network.okhttp)
}
