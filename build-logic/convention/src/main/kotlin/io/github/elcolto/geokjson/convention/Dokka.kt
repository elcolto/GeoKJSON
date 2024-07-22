package io.github.elcolto.geokjson.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTaskPartial

internal fun Project.configureDokka() {
    tasks.withType<DokkaTaskPartial> {
        dokkaSourceSets.configureEach {
            includes.from("README.md")
            reportUndocumented.set(true)
        }
        suppressInheritedMembers.set(true)
    }
}
