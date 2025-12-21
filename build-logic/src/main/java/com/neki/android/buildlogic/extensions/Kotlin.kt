package com.neki.android.buildlogic.extensions

import com.neki.android.buildlogic.const.BuildConst
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.configureKotlin(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        compileSdk = BuildConst.COMPILE_SDK

        compileOptions {
            sourceCompatibility = BuildConst.JAVA_VERSION
            targetCompatibility = BuildConst.JAVA_VERSION
        }

        configureKotlinOptions {
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

        dependencies {
            "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
        }
    }
}

internal fun CommonExtension<*, *, *, *, *, *>.configureKotlinOptions(
    block: KotlinJvmOptions.() -> Unit,
) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}