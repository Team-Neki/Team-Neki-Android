package com.neki.android.buildlogic.extensions

import com.android.build.api.dsl.CommonExtension
import com.neki.android.buildlogic.const.BuildConst
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

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

        dependencies {
            add("detektPlugins", libs.findLibrary("detekt.formatting").get())
        }
    }

    extensions.configure<KotlinAndroidProjectExtension>("kotlin") {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(BuildConst.JDK_VERSION.toString()))
        }
    }
}
