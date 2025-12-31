import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
    }

    configure<DetektExtension> {
        parallel = true
        buildUponDefaultConfig = true
        toolVersion = rootProject.libs.versions.detekt.get()
        config.setFrom(files("$rootDir/detekt-config.yml"))
    }
}