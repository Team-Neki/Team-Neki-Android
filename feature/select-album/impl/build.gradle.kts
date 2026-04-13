plugins {
    alias(libs.plugins.neki.android.feature.impl)
}

android {
    namespace = "com.neki.android.feature.select_album.impl"
}

dependencies {
    implementation(projects.feature.selectAlbum.api)
    implementation(projects.feature.archive.api)
}
