package io.github.elcolto.geokjson.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    extension: LibraryExtension
) = extension.apply {

    //get module name from module path
    val moduleName = path.split(":").last()
    namespace = if (moduleName.isNotEmpty()) "io.github.elcolto.geokjson.$moduleName" else error("module name is empty")

    compileSdk = 34
    defaultConfig {
        minSdk = 21
        lint.targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
