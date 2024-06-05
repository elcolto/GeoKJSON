package io.github.elcolto.geokjson.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    jvmToolchain(17)

    // targets
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }


    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val appleTargets = listOf(
        iosArm64, iosX64, iosSimulatorArm64,
        watchosArm32, watchosArm64,
        watchosSimulatorArm64,
    )

    appleTargets.forEach { target ->
        with(target) {
            binaries {
                framework {
                    baseName = "GeoKJSON"
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()

    //common dependencies
    sourceSets.apply {

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-annotations-common"))
        }

        val ioMain by creating {
            dependsOn(commonMain.get())
            sourceSets["iosArm64Main"].dependsOn(this)
            sourceSets["iosX64Main"].dependsOn(this)
            sourceSets["iosSimulatorArm64Main"].dependsOn(this)
            sourceSets["watchosArm32Main"].dependsOn(this)
            sourceSets["watchosArm64Main"].dependsOn(this)
            sourceSets["watchosSimulatorArm64Main"].dependsOn(this)
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}
