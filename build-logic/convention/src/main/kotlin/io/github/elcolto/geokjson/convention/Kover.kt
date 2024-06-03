package io.github.elcolto.geokjson.convention

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project

internal fun Project.configureKover(extension: KoverProjectExtension) = extension.apply {
    reports {
        total {
            xml {
                onCheck.set(true)
            }
            html {
                onCheck.set(true)
            }
        }
    }
}
