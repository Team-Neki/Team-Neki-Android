import com.android.build.api.dsl.ApplicationExtension
import com.neki.android.buildlogic.const.BuildConst
import com.neki.android.buildlogic.extensions.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(plugins) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("neki.hilt")
            }

            extensions.configure<ApplicationExtension> {
                configureAndroid(this)

                defaultConfig.apply {
                    applicationId = BuildConst.APPLICATION_ID

                    minSdk = BuildConst.MIN_SDK
                    targetSdk = BuildConst.TARGET_SDK

                    versionCode = BuildConst.VERSION_CODE
                    versionName = BuildConst.VERSION_NAME
                }
            }
        }
    }
}