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

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
}
