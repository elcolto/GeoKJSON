plugins {
    application
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.elcolto"
version = "1.0"

application {
    mainClass.set("MainKt")
    run {
        applicationDefaultJvmArgs = listOf(layout.buildDirectory.toString())
    }
}

sourceSets {
    main {
        java.srcDir("build/generated/classes/turf/main/kotlin")
    }
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.client.logging)
    implementation(libs.logback)
    implementation(libs.kotlinx.serialization)
    implementation(libs.okio)

    implementation(project(":geojson"))

    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
