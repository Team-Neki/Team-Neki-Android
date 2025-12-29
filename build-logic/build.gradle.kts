plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "neki.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "neki.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "neki.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "neki.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "neki.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidFeatureApi") {
            id = "neki.android.feature.api"
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("androidFeatureImplCompose") {
            id = "neki.android.feature.impl"
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("hilt") {
            id = "neki.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}