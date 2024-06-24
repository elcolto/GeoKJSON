package io.github.elcolto.geokjson.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.moduleName() = path.split(":").last()

internal const val GroupId = "io.github.elcolto.geokjson"
const val Version = "0.1.0"
