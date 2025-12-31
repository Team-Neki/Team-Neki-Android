import com.neki.android.buildlogic.const.BuildConst
import com.neki.android.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class KotlinLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("java-library")
                apply("org.jetbrains.kotlin.jvm")
            }

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = BuildConst.JAVA_VERSION
                targetCompatibility = BuildConst.JAVA_VERSION
            }

            extensions.configure<KotlinProjectExtension> {
                jvmToolchain(BuildConst.JDK_VERSION)
            }

            dependencies {
                add("detektPlugins", libs.findLibrary("detekt.formatting").get())
            }
        }
    }
}