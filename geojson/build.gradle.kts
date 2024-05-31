import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.github.elcolto.geokjson.kotlinMultiplatform")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.benchmark)
}

kotlin {
    explicitApi()

    jvm {
        compilations.create("bench")
    }

    sourceSets {
        all {
            with(languageSettings) {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.js.ExperimentalJsExport")
                optIn("kotlinx.serialization.InternalSerializationApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }

        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.serialization)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting

        val commonBench by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kotlinx.benchmark)
            }
        }

        val jvmBench by getting {
            dependsOn(commonBench)
            dependsOn(jvmMain)
        }

    }
}

benchmark {
    this.configurations {
        getByName("main") {
            iterations = 5
        }
    }

    targets {
        register("jvmBench")
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    // custom output directory
    outputDirectory.set(buildDir.resolve("$rootDir/docs/api"))
}

tasks.withType(KotlinCompile::class.java).configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
}
