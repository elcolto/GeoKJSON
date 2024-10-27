package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.booleans.pointInPolygon
import io.github.elcolto.geokjson.turf.classification.nearestPoint
import io.github.elcolto.geokjson.turf.coordAll
import kotlin.math.sqrt

/**
 * Takes a Feature or FeatureCollection and returns a [Point] guaranteed to be on the surface of the feature.
 *
 * * Given a [Polygon], the point will be in the area of the polygon
 * * Given a [LineString], the point will be along the string
 * * Given a [Point], the point will the same as the input
 *
 * @param geometry any Geometry
 * @return a point on the surface of `input`
 * @example
 * ```kotlin
 * val polygon = polygon(arrayOf(arrayOf(
 *   Position(116.0, -36.0),
 *   Position(131.0, -32.0),
 *   Position(146.0, -43.0),
 *   Position(155.0, -25.0),
 *   Position(133.0, -9.0),
 *   Position(111.0, -22.0),
 *   Position(116.0, -36.0)
 * )))
 *
 * val pointOnPolygon = pointOnFeature(polygon)
 *
 * //addToMap
 * val addToMap = listOf(polygon, pointOnPolygon)
 * ```
 */
@ExperimentalTurfApi
public fun pointOnFeature(geometry: Geometry): Point {
    val center = center(geometry)
    return if (isPointOnSurface(geometry, center)) {
        center
    } else {
        nearestPoint(
            target = center,
            points = geometry.coordAll().map { Point(it) },
        )
    }
}

@OptIn(ExperimentalTurfApi::class)
private fun isPointOnSurface(geometry: Geometry, centroid: Point): Boolean = when (geometry) {
    is Point ->
        geometry.coordinates.longitude == centroid.coordinates.longitude &&
            geometry.coordinates.latitude == centroid.coordinates.latitude

    is MultiPoint -> geometry.coordinates.any {
        it.longitude == centroid.coordinates.longitude && it.latitude == centroid.coordinates.latitude
    }

    is LineString -> geometry.coordinates.zipWithNext().any { (p1, p2) ->
        pointOnSegment(
            centroid.coordinates.longitude,
            centroid.coordinates.latitude,
            p1.longitude,
            p1.latitude,
            p2.longitude,
            p2.latitude,
        )
    }

    is MultiLineString -> geometry.coordinates.any { line ->
        line.zipWithNext().any { (p1, p2) ->
            pointOnSegment(
                centroid.coordinates.longitude,
                centroid.coordinates.latitude,
                p1.longitude,
                p1.latitude,
                p2.longitude,
                p2.latitude,
            )
        }
    }

    is Polygon -> pointInPolygon(centroid, geometry)
    is MultiPolygon -> pointInPolygon(centroid, geometry)
    is GeometryCollection -> geometry.any { isPointOnSurface(it, centroid) }
}

private fun pointOnSegment(x: Double, y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Boolean {
    val ab = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    val ap = sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1))
    val pb = sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y))
    return ab == ap + pb
}
