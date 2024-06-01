package io.github.elcolto.geokjson.convention

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

internal fun Project.configureDetekt(
    extension: DetektExtension
) = extension.apply {
    buildUponDefaultConfig = true
    source.setFrom("src")
}
