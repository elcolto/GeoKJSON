import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.resources)
    alias(libs.plugins.android.library) apply true
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        lint.targetSdk = 34
    }
    namespace = "io.github.elcolto.geokjson.turf"
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()

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

    val macosX64 = macosX64()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val watchosArm32 = watchosArm32()
    val watchosArm64 = watchosArm64()
    val watchosX64 = watchosX64()
    val watchosSimulatorArm64 = watchosSimulatorArm64()
    val appleTargets = listOf(
        macosX64, macosArm64,
        iosArm64, iosX64, iosSimulatorArm64,
        watchosArm32, watchosArm64, watchosX64,
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


    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":geojson"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.resources)
            }
        }

        val macosX64Main by getting
        val macosArm64Main by getting
        val iosArm64Main by getting
        val iosX64Main by getting
        val iosSimulatorArm64Main by getting
        val watchosArm32Main by getting
        val watchosArm64Main by getting
        val watchosX64Main by getting
        val watchosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosX64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            watchosArm32Main.dependsOn(this)
            watchosArm64Main.dependsOn(this)
            watchosX64Main.dependsOn(this)
            watchosSimulatorArm64Main.dependsOn(this)
        }

        val macosX64Test by getting
        val macosArm64Test by getting
        val iosArm64Test by getting
        val iosX64Test by getting
        val iosSimulatorArm64Test by getting
        val watchosArm32Test by getting
        val watchosArm64Test by getting
        val watchosX64Test by getting
        val watchosSimulatorArm64Test by getting

        val iosTest by creating {
            dependsOn(commonTest)
            macosX64Test.dependsOn(this)
            macosArm64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosX64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
            watchosArm32Test.dependsOn(this)
            watchosArm64Test.dependsOn(this)
            watchosX64Test.dependsOn(this)
            watchosSimulatorArm64Test.dependsOn(this)
        }

        all {
            with(languageSettings) {
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}


tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    // custom output directory
    outputDirectory.set(layout.buildDirectory.asFile.get().resolve("$rootDir/docs/api"))
}

tasks.withType(KotlinCompile::class.java).configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
}

// Working around dokka problems
afterEvaluate {
    tasks.named("dokkaJavadocJar").configure {
        dependsOn(":geojson:dokkaHtml")
    }
}
