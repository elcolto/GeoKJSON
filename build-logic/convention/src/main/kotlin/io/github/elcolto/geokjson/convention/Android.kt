package io.github.elcolto.geokjson.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureKotlinAndroid(
    extension: LibraryExtension
) = extension.apply {

    val moduleName = moduleName()
    namespace = if (moduleName.isNotEmpty()) "$GroupId.$moduleName" else error("module name is empty")

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
