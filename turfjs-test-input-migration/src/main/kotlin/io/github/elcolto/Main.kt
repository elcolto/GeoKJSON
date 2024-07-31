package io.github.elcolto

import io.github.elcolto.github.models.GitHubTree
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import java.io.File
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry


private lateinit var httpClient: HttpClient

internal suspend fun main(args: Array<String>) {

    args.forEach { println(it) }
    val downloadPath = args.first()

    httpClient = HttpClient(CIO) {
        expectSuccess = true
        followRedirects = true
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }

        install(ContentEncoding) {
            deflate()
            gzip()
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        defaultRequest {
            url("https://api.github.com/")
            accept(ContentType.parse("application/vnd.github+json"))
            header("X-GitHub-Api-Version", "2022-11-28")
        }
    }

    // it's packages
    val tree = httpClient.get("/repos/Turfjs/turf/git/trees/1cd1112dfa24cf6516606dd18ff54007b75f74b5") {
        parameter("recursive", true)
    }
        .body<GitHubTree>()

    val testFiles = tree.tree.filter { node ->
        val path = node.path
        node.type == "blob" && path.contains("/test/") && (path.endsWith(".json") || path.endsWith(".geojson"))
    }
        .map { it.path }

    val pathsByCamelCaseIndicator = testFiles.groupBy { path ->
        path.toPath().parent?.segments?.camelCase { first ->
            first.split('-').camelCase()
        }.orEmpty()
    }

    pathsByCamelCaseIndicator.forEach { (name, paths) ->
        println(name)
        paths.forEach {
            println("\t$it")
        }
        println("..........................")
        println()
    }

    val firstIndicator = pathsByCamelCaseIndicator.keys.first()
    val firstPackage = pathsByCamelCaseIndicator[firstIndicator].orEmpty()


    val path = firstPackage.first()
    val testContent = httpClient.get("https://api.github.com/repos/Turfjs/turf/contents/packages/$path") {
        accept(ContentType.parse("application/vnd.github.raw+json"))
    }.bodyAsText()

    val file = GeoJsonDslBuilder.buildFileSpec(
        testContent,
        firstIndicator.capitalize(),
        path

    )
    file.writeTo(System.out)

    httpClient.close()
}


private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


private fun List<String>.camelCase(onFirstElement: (String) -> String = { it }) = mapIndexed { i: Int, text: String ->
    if (i == 0) onFirstElement(text) else text.capitalize()
}.joinToString("")


@Throws(IOException::class)
fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
    val destFile = File(destinationDir, zipEntry.name)

    val destDirPath = destinationDir.canonicalPath
    val destFilePath = destFile.canonicalPath

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
        throw IOException("Entry is outside of the target dir: " + zipEntry.name)
    }

    return destFile
}
