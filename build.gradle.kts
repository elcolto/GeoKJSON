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
    }
}


val apiDocsPath = rootDir.absoluteFile.resolve("docs/api")

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(apiDocsPath)
    moduleName.set("GeoKJSON")
    pluginsMapConfiguration.set(
        mapOf(
            "org.jetbrains.dokka.base.DokkaBase" to """
            {
                "footerMessage": "Copyright &copy; 2024 Sebastian Heeschen"
            }
        """.trimIndent()
        )
    )
}
