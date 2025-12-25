package com.neki.android.buildlogic.extensions

import com.neki.android.buildlogic.const.BuildConst
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        compileSdk = BuildConst.COMPILE_SDK

        compileOptions {
            sourceCompatibility = BuildConst.JAVA_VERSION
            targetCompatibility = BuildConst.JAVA_VERSION
        }

        configureKotlinAndroidOptions {
            jvmTarget = BuildConst.JDK_VERSION.toString()
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.configureKotlinAndroidOptions(
    block: KotlinJvmOptions.() -> Unit,
) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}