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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okio.Path.Companion.toPath

internal object GeoJsonDslBuilder {

    fun buildFileSpec(
        testContent: String,
        name: String,
        path: String
    ): FileSpec {
        val geoJson: GeoJson = when (Json.parseToJsonElement(testContent)
            .jsonObject["type"]?.jsonPrimitive?.content) {
            "Feature" -> Feature.fromJson(testContent)
            "FeatureCollection" -> FeatureCollection.fromJson(testContent)
            else -> error("not applicable")
        }

        val file = FileSpec.builder("io.github.elcolto.turf.test", name)
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
        return file
    }

   private fun geoJsonToFunSpec(geoJson: GeoJson): FunSpec.Builder {

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


}
