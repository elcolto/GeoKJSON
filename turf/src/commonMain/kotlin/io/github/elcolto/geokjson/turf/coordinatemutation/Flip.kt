package io.github.elcolto.geokjson.turf.coordinatemutation

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
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi

/**
 * Takes input [Geometry] and flips all of their coordinates from `[longitude, latitude]` to `[latitude, longitude]`.
 */
@ExperimentalTurfApi
public inline fun <reified T : Geometry> flip(geometry: T): T = flipGeometry(geometry) as T

public fun flip(featureCollection: FeatureCollection): FeatureCollection {
    val features = featureCollection.features.map { feature ->
        val geometry = feature.geometry?.let { flipGeometry(it) }
        feature.copy(geometry = geometry)
    }
    return featureCollection.copy(features)
}

@PublishedApi
internal fun flipGeometry(geometry: Geometry): Geometry = when (geometry) {
    is GeometryCollection -> GeometryCollection(geometry.geometries.map(::flipGeometry))
    is LineString -> LineString(geometry.coordinates.map(Position::flip))
    is MultiLineString -> MultiLineString(geometry.coordinates.map { it.map(Position::flip) })
    is MultiPoint -> MultiPoint(geometry.coordinates.map(Position::flip))
    is MultiPolygon -> MultiPolygon(geometry.coordinates.map { polygon -> polygon.map { it.map(Position::flip) } })
    is Point -> Point(geometry.coordinates.flip())
    is Polygon -> Polygon(geometry.coordinates.map { it.map(Position::flip) })
}

@PublishedApi
internal fun Position.flip(): Position = Position(latitude, longitude, altitude)
