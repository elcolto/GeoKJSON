import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("io.github.elcolto.geokjson.library")
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

kover {
    reports {
        filters {
            excludes {
                // exclusion rules - classes to exclude from report
                classes("io.github.elcolto.geokjson.geojson.GeoJsonBenchmark")
            }
        }

        verify {
            rule {
                minBound(72, CoverageUnit.INSTRUCTION) // 72% instruction coverage on applying plugin
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    // custom output directory
    outputDirectory.set(buildDir.resolve("$rootDir/docs/api"))
}
