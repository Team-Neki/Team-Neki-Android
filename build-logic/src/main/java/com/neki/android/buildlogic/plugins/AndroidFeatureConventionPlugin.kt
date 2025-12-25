import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("neki.android.library")
                apply("neki.android.library.compose")
                apply("neki.hilt")
            }

            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:model"))
                "implementation"(project(":core:data-api"))
                "implementation"(project(":core:common"))
            }
        }
    }
}
