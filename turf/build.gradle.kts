import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("io.github.elcolto.geokjson.library")
}

kotlin {
    explicitApi()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":geojson"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.okio)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.okio.nodefilesystem)
            }
        }
        val wasmJsTest by getting {
            dependencies {
                implementation(libs.okio.fakefilesystem)
            }
        }
    }
}

kover {
    reports {
        verify {
            rule {
                minBound(92, CoverageUnit.INSTRUCTION) // 92% instruction coverage on applying plugin
            }
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    // custom output directory
    outputDirectory.set(layout.buildDirectory.asFile.get().resolve("$rootDir/docs/api"))
}

// Working around dokka problems
afterEvaluate {
    tasks.named("dokkaJavadocJar").configure {
        dependsOn(":geojson:dokkaHtml")
    }
}
