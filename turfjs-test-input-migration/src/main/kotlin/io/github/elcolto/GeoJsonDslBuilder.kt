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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object GeoJsonDslBuilder {

    @Throws(IllegalStateException::class)
    internal fun geoJsonFromFile(testContent: String): GeoJson {
        val content = Json.parseToJsonElement(testContent).jsonObject["type"]?.jsonPrimitive?.content
        val geoJson: GeoJson = when (content) {
            "Feature" -> Feature.fromJson<Geometry>(testContent)
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

            is Feature<*> -> featureToFunSpec(geoJson)
            is Geometry -> (geometryBlock(geoJson))
            else -> error("not applicable")
        }

        return FunSpec.getterBuilder().addStatement(
            "return %L", featureBlocks.build()
        )
    }

    private fun featureToFunSpec(feature: Feature<*>): CodeBlock.Builder {
        val geometryBlock = feature.geometry?.let { geometryBlock(it) }

        val propertyBlock = feature.properties.takeIf { it.isNotEmpty() }?.let { map ->
            CodeBlock.builder().apply {
                map.mapNotNull { (key, value) ->
                    when (value) {
                        is String -> addStatement("put(%S, %S)", key, value)
                        is Collection<*> -> if (value.isNotEmpty()) addStatement(
                            "put(%S, %L)", key, listLiteral(value)
                        ) else null

                        is Map<*, *> -> {
                            addStatement("put(")
                            indent()
                            add("%S, ", key)
                            indent()
                            addStatement("mapOf(")
                            value.forEach {
                                addStatement("%S to %S,", it.key, it.value)
                            }
                            unindent()
                            addStatement(")")
                            unindent()
                            addStatement(")")
                        }

                        else -> addStatement("put(%S, %L)", key, value)
                    }
                }
            }.build()
        }

        return CodeBlock.builder().beginControlFlow("feature(geometry = ${geometryBlock?.build()}) {").indent()
            //properties
            .apply {
                propertyBlock?.let {
                    add(it)
                }
            }
            .unindent()
            .endControlFlow()
    }

    private fun listLiteral(value: Collection<*>): String {
        val literal = if (value.all { it is Collection<*> }) {
            value.filterNotNull().joinToString { listLiteral(it as Collection<*>) }
        } else value.joinToString()
        return "listOf($literal)"
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
                    geometry.coordinates.map { polygon -> Polygon(polygon) }.forEach {
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
