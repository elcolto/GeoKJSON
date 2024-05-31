import com.android.build.api.dsl.LibraryExtension
import io.github.elcolto.geokjson.convention.configureFormatting
import io.github.elcolto.geokjson.convention.configureKotlinAndroid
import io.github.elcolto.geokjson.convention.configureKotlinMultiplatform
import io.github.elcolto.geokjson.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KotlinMultiplatformPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
            apply(libs.findPlugin("android-library").get().get().pluginId)
            apply(libs.findPlugin("publish").get().get().pluginId)
            apply(libs.findPlugin("dokka").get().get().pluginId)
            apply(libs.findPlugin("ktlint").get().get().pluginId)
        }

        extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
        extensions.configure<LibraryExtension>(::configureKotlinAndroid)
        extensions.configure<KtlintExtension>(::configureFormatting)
    }
}
