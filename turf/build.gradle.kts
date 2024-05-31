plugins {
    id("io.github.elcolto.geokjson.kotlinMultiplatform")
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
            dependencies {
                implementation(libs.resources)
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
