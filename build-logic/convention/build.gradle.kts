plugins {
    `kotlin-dsl`
}

group = "io.github.elcolto.geokjson.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "io.github.elcolto.geokjson.kotlinMultiplatform"
            implementationClass = "KotlinMultiplatformPlugin"
        }
    }
}
