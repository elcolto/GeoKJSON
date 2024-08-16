import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("io.github.elcolto.geokjson.library")
    alias(libs.plugins.resources)
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
            kotlin.srcDir("build/generated/classes/turf/main/kotlin")

            dependencies {
                implementation(libs.resources)
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
