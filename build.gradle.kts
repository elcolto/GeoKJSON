plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

dependencies {
    // integrate each module for merged kover report
    subprojects.forEach { project ->
        kover(project(":${project.name}"))
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

        total {
            xml {
                onCheck = true
            }
            html {
                onCheck = true
            }
        }
    }
}
