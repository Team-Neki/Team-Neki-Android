import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("neki.android.library")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                "api"(project(":core:navigation"))
                "api"(project(":core:model"))
            }
        }
    }
}
