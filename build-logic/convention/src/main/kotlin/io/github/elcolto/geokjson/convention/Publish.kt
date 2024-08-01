package io.github.elcolto.geokjson.convention

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Project

internal fun Project.configurePublishing(
    extension: MavenPublishBaseExtension
) = extension.apply {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release")
        )
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(GroupId, moduleName(), Version)

    pom {
        name.set("GeoKJSON")
        description.set("Kotlin Multiplatform library to provide GeoJSON and turf functionality.")
        inceptionYear.set("2024")
        url.set("https://github.com/elcolto/GeoKJSON")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("elcolto")
                name.set("Sebastian Heeschen")
                url.set("https://github.com/elcolto")
            }
            developer {
                id.set("JanTie")
                name.set("Jan Tiedemann")
                url.set("https://github.com/JanTie")
            }
        }
        scm {
            url.set("https://github.com/elcolto/GeoKJSON")
            connection.set("scm:git:git://github.com:elcolto/GeoKJSON.git")
            developerConnection.set("scm:git:ssh://git@github.com:elcolto/GeoKJSON.git")
        }
    }
}
