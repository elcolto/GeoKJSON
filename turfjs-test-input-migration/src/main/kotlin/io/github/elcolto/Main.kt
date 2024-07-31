package io.github.elcolto

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.GeoJson
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.Path.Companion.toPath
import java.util.*

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


    val geoJson: GeoJson = when (Json.parseToJsonElement(testContent)
        .jsonObject["type"]?.jsonPrimitive?.content) {
        "Feature" -> Feature.fromJson(testContent)
        "FeatureCollection" -> FeatureCollection.fromJson(testContent)
        else -> error("not applicable")
    }

    val file = FileSpec.builder("io.github.elcolto.turf.test", firstIndicator.capitalize())
        .addImport(
            "io.github.elcolto.geokjson.geojson.dsl",
            listOf(
                "featureCollection",
                "geometryCollection",
                "lineString",
                "lngLat",
                "multiLineString",
                "multiPoint",
                "multiPolygon",
                "point",
                "polygon",
            )
        )
        .addProperty(
            PropertySpec.builder(
                path.toPath().name.removeSuffix(".json").removeSuffix(".geojson"),
                geoJson.javaClass
            )
                .getter(
                    geoJsonToFunSpec(geoJson).build()
                )
                .build()
        )
        .build()

    file.writeTo(System.out)

    httpClient.close()
}


fun geoJsonToFunSpec(geoJson: GeoJson): FunSpec.Builder {

    val featureBlocks = when (geoJson) {
        is FeatureCollection -> CodeBlock.builder()
            .beginControlFlow("featureCollection {")
            .indent()
            .apply {
                geoJson.features.forEach { feature ->
                    featureToFunSpec(feature)
                }
            }
            .unindent()
            .endControlFlow()


        is Feature -> (featureToFunSpec(geoJson))
        is Geometry -> (geometryBlock(geoJson)) // TODO add feature id
        else -> error("not applicable")
    }

    return FunSpec.getterBuilder()
        .addCode(
            featureBlocks.build()
        )

}

private fun featureToFunSpec(feature: Feature): CodeBlock.Builder {
    val geometryBlock = feature.geometry?.let { geometryBlock(it, feature.id) }

    val propertyBlock = feature.properties.takeIf { it.isNotEmpty() }?.let { map ->
        CodeBlock.builder().apply {
            map.map { (key, value) ->
                add("put(%S, %S)", key, value)
            }
        }
            .build()
    }

    return CodeBlock.builder()
        .beginControlFlow("feature(geometry = ${geometryBlock?.build()}) {")
        .indent()
        //properties
        .apply {
            propertyBlock?.let {
                add(it)
            }
        }
        .unindent()
        .endControlFlow()
}

private fun geometryBlock(geometry: Geometry, id: String? = null): CodeBlock.Builder {
    return CodeBlock.builder().apply {
        when (geometry) {
            is GeometryCollection -> {
                beginControlFlow("geometryCollection {")
                indent()
                geometry.geometries.forEach { innerGeometry ->
                    add(geometryBlock(innerGeometry).build())
                }
                unindent()
                endControlFlow()
            }

            is LineString -> {
                beginControlFlow("lineString {")
                indent()
                geometry.coordinates.forEach { position ->
                    add(position)
                }
                unindent()
                endControlFlow()
            }

            is MultiLineString -> {
                beginControlFlow("multiLineString {")
                indent()
                geometry.coordinates.map { lineString ->
                    LineString(lineString)
                }.forEach { lineString ->
                    add(geometryBlock(lineString).build())
                }
                unindent()
                endControlFlow()
            }

            is MultiPoint -> {
                beginControlFlow("multiPoint {")
                indent()
                geometry.coordinates.forEach { position ->
                    add(position)
                }
                unindent()
                endControlFlow()
            }

            is MultiPolygon -> {
                beginControlFlow("multiPolygon {")
                indent()
                geometry.coordinates
                    .map { polygon -> Polygon(polygon) }
                    .forEach {
                        add(geometryBlock(it).build())
                    }
                unindent()
                endControlFlow()
            }

            is Point -> {
                val coordinates = geometry.coordinates
                add(
                    "point(%L, %L, %L, %L)",
                    coordinates.latitude,
                    coordinates.longitude,
                    coordinates.altitude,
                    id
                )
            }

            is Polygon -> {
                beginControlFlow("polygon {")
                indent()
                beginControlFlow("ring {")
                geometry.coordinates.forEach { linestring ->
                    add(geometryBlock(LineString(linestring)).build())
                }
                add("complete()")
                unindent()
                endControlFlow()
                unindent()
                endControlFlow()
            }
        }
    }

}

private fun CodeBlock.Builder.add(position: Position) {
    add(
        "point(%L, %L, %L)\n",
        position.latitude,
        position.longitude,
        position.altitude,
    )
}

private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


private fun List<String>.camelCase(onFirstElement: (String) -> String = { it }) = mapIndexed { i: Int, text: String ->
    if (i == 0) onFirstElement(text) else text.capitalize()
}.joinToString("")

