package com.neki.android.buildlogic.extensions

import com.android.build.api.dsl.CommonExtension
import com.neki.android.buildlogic.const.BuildConst
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = BuildConst.COMPILE_SDK

        defaultConfig {
            minSdk = BuildConst.MIN_SDK
        }

        compileOptions {
            sourceCompatibility = BuildConst.JAVA_VERSION
            targetCompatibility = BuildConst.JAVA_VERSION
        }

        configureAndroidOptions {
            jvmTarget = BuildConst.JDK_VERSION.toString()
        }

        dependencies {
            add("detektPlugins", libs.findLibrary("detekt.formatting").get())
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.configureAndroidOptions(
    block: KotlinJvmOptions.() -> Unit,
) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
