plugins {
    `kotlin-dsl`
}

group = "io.github.elcolto.geokjson.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.binary.validator.plugin)
    compileOnly(libs.kover.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
    compileOnly(libs.publish.plugin)
    compileOnly(libs.dokka.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("commonLibraryPlugin") {
            id = "io.github.elcolto.geokjson.library"
            implementationClass = "CommonGeoKJSONLibraryPlugin"
        }
    }
}
