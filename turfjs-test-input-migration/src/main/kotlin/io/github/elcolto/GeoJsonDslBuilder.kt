package io.github.elcolto

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

internal object GeoJsonDslBuilder {

    @Throws(IllegalStateException::class)
    internal fun geoJsonFromFile(testContent: String): GeoJson {
        val content = Json.parseToJsonElement(testContent)
            .jsonObject["type"]
            ?.jsonPrimitive?.content
        val geoJson: GeoJson = when (content) {
            "Feature" -> Feature.fromJson(testContent)
            "FeatureCollection" -> FeatureCollection.fromJson(testContent)
            else -> error("not applicable")
        }
        return geoJson
    }

    internal fun geoJsonToFunSpec(geoJson: GeoJson): FunSpec.Builder {

        val featureBlocks = when (geoJson) {
            is FeatureCollection -> CodeBlock.builder()
                .beginControlFlow("featureCollection {")
                .indent()
                .apply {
                    geoJson.features.forEach { feature ->
                        add(featureToFunSpec(feature).build())
                    }
                }
                .unindent()
                .endControlFlow()


            is Feature -> (featureToFunSpec(geoJson))
            is Geometry -> (geometryBlock(geoJson))
            else -> error("not applicable")
        }

        return FunSpec.getterBuilder()
            .addStatement(
                "return %L",
                featureBlocks.build()
            )

    }

    private fun featureToFunSpec(feature: Feature): CodeBlock.Builder {
        val geometryBlock = feature.geometry?.let { geometryBlock(it) }

        val propertyBlock = feature.properties.takeIf { it.isNotEmpty() }?.let { map ->
            CodeBlock.builder().apply {
                map.map { (key, value) ->
                    val valueToPut = when (value) {
                        is JsonArray -> value.jsonArray.toString()
                        is JsonObject -> value.jsonObject.toString()
                        is JsonPrimitive -> {
                            val primitive = value.jsonPrimitive
                            when {
                                primitive.isString -> primitive.content
                                primitive.booleanOrNull != null -> primitive.boolean
                                primitive.intOrNull != null -> primitive.int
                                primitive.longOrNull != null -> primitive.long
                                primitive.floatOrNull != null -> primitive.float
                                primitive.doubleOrNull != null -> primitive.double
                                else -> null
                            }
                        }

                        JsonNull -> null
                        else -> error("${value.javaClass.simpleName} is not handled")
                    }
                    when (valueToPut) {
                        is String -> addStatement("put(%S, %S)", key, valueToPut)
                        null -> addStatement("putKey(%S)", key)
                        else -> addStatement("put(%S, %L)", key, valueToPut)
                    }
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

    private fun geometryBlock(geometry: Geometry): CodeBlock.Builder {
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
                    addStatement(
                        "point(%L, %L, %L)",
                        coordinates.latitude,
                        coordinates.longitude,
                        coordinates.altitude,
                    )
                }

                is Polygon -> {
                    beginControlFlow("polygon {")
                    indent()
                    beginControlFlow("ring {")
                    indent()
                    geometry.coordinates.forEach { linestring ->
                        add(geometryBlock(LineString(linestring)).build())
                    }
                    addStatement("complete()")
                    unindent()
                    endControlFlow()
                    unindent()
                    endControlFlow()
                }
            }
        }

    }

    private fun CodeBlock.Builder.add(position: Position) {
        addStatement(
            "point(%L, %L, %L)",
            position.latitude,
            position.longitude,
            position.altitude,
        )
    }


}
