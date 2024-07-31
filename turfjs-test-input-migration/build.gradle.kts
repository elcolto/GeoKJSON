plugins {
    application
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.elcolto"
version = "1.0"

application {
    mainClass.set("io.github.elcolto.MainKt")
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization.kotlinx.json)
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
