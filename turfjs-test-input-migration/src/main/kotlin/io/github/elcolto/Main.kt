package io.github.elcolto

import io.github.elcolto.github.models.GitHubTree
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private lateinit var httpClient: HttpClient

internal suspend fun main() {

    httpClient = HttpClient(CIO) {
        expectSuccess = true

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
        .map { it.path to it.sha }

    testFiles.forEach {
        println(it.first)
    }
    val path = testFiles.first().first
    val testfile = httpClient.get("https://api.github.com/repos/Turfjs/turf/contents/packages/$path") {
        accept(ContentType.parse("application/vnd.github.raw+json"))
    }.bodyAsText()

    println(testfile)

    httpClient.close()
}

