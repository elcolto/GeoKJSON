@file:JvmName("TurfMeta")

package io.github.elcolto.geokjson.turf

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import kotlin.jvm.JvmName

@ExperimentalTurfApi
public fun Geometry.coordAll(): List<Position> = when (this) {
    is Point -> listOf(coordinates)
    is MultiPoint -> coordinates
    is LineString -> coordinates
    is MultiLineString -> coordinates.flatten()
    is Polygon -> coordinates.flatten()
    is MultiPolygon -> coordinates.flatMap { it.flatten() }
    is GeometryCollection -> geometries.flatMap { it.coordAll() }
}

@ExperimentalTurfApi
public fun Feature.coordAll(): List<Position>? = geometry?.coordAll()

@ExperimentalTurfApi
public fun FeatureCollection.coordAll(): List<Position> = features.flatMap { it.coordAll() ?: emptyList() }
