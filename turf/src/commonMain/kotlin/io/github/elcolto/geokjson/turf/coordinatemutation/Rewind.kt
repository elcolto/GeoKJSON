package io.github.elcolto.geokjson.turf.coordinatemutation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.booleans.clockwise

/**
 * Rewind LineString, MultiLineString, Polygon or MultiPolygon outer ring counterclockwise and inner rings clockwise
 * (Uses Shoelace Formula).
 * Point and MultiPoint geometries are not affected.
 *
 * @param geometry input [Geometry]
 * @param reverse enable reverse winding
 * @return rewind Geometry
 */
@ExperimentalTurfApi
public fun rewind(geometry: Geometry, reverse: Boolean = false): Geometry {
    return when (geometry) {
        is GeometryCollection -> {
            val geometries = geometry.geometries.map { rewind(it, reverse) }
            geometry.copy(geometries = geometries)
        }

        is LineString -> {
            val coordinates = rewindLineString(geometry.coordinates, reverse)
            geometry.copy(coordinates = coordinates)
        }

        is MultiLineString -> {
            val coordinates = geometry.coordinates.map { line -> rewindLineString(line, reverse) }
            geometry.copy(coordinates = coordinates)
        }

        is Polygon -> {
            val coordinates = rewindPolygon(geometry.coordinates, reverse)
            geometry.copy(coordinates = coordinates)
        }

        is MultiPolygon -> {
            val coordinates = geometry.coordinates.map { polygon -> rewindPolygon(polygon, reverse) }
            geometry.copy(coordinates = coordinates)
        }

        else -> geometry
    }
}

@ExperimentalTurfApi
private fun rewindLineString(line: List<Position>, reverse: Boolean): List<Position> =
    if (reverse == clockwise(line)) line.reversed() else line

@ExperimentalTurfApi
private fun rewindPolygon(polygon: List<List<Position>>, reverse: Boolean): List<List<Position>> =
    polygon.mapIndexed { index, ring ->
        val isFirstRing = index == 0
        if ((isFirstRing xor reverse) && clockwise(ring)) ring.reversed() else ring
    }

@ExperimentalTurfApi
public fun <T : Geometry> rewind(feature: Feature<T>, reverse: Boolean = false): Feature<T> {
    val rewoundGeometry = feature.geometry?.let { rewind(it, reverse) as T }
    return feature.copy(geometry = rewoundGeometry)
}

@ExperimentalTurfApi
public fun rewind(featureCollection: FeatureCollection, reverse: Boolean = false): FeatureCollection {
    val features = featureCollection.features.map { rewind(it, reverse) }
    return featureCollection.copy(features = features)
}
