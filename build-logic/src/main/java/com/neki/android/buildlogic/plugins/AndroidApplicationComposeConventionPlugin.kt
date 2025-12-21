import com.android.build.api.dsl.ApplicationExtension
import com.neki.android.buildlogic.extensions.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(plugins) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            configureCompose(
                extensions.getByType<ApplicationExtension>()
            )
        }
    }
}