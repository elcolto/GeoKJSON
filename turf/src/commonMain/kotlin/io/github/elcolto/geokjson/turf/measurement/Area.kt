package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.AREA_EARTH_RADIUS
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.radians
import kotlin.math.abs
import kotlin.math.sin

/**
 * Takes a geometry and returns its area in square meters.
 *
 * @param geometry input geometry
 * @return area in square meters
 */
@ExperimentalTurfApi
public fun area(geometry: Geometry): Double {
    return when (geometry) {
        is GeometryCollection -> geometry.geometries.fold(0.0) { acc, geom ->
            acc + area(geom)
        }

        else -> calculateArea(geometry)
    }
}

private fun calculateArea(geometry: Geometry): Double {
    return when (geometry) {
        is Polygon -> polygonArea(geometry.coordinates)
        is MultiPolygon -> geometry.coordinates.fold(0.0) { acc, coords ->
            acc + polygonArea(coords)
        }

        else -> 0.0
    }
}

private fun polygonArea(coordinates: List<List<Position>>): Double {
    return if (coordinates.isNotEmpty()) {
        coordinates.drop(1)
            .fold(abs(ringArea(coordinates[0]))) { sum, polygon ->
                sum - abs(ringArea(polygon))
            }
    } else {
        0.0
    }
}

/**
 * Calculates the approximate area of the [polygon][coordinates] were it projected onto the earth.
 * Note that this area will be positive if ring is oriented clockwise, otherwise it will be negative.
 *
 * Reference:
 * Robert. G. Chamberlain and William H. Duquette, "Some Algorithms for Polygons on a Sphere",
 * JPL Publication 07-03, Jet Propulsion
 * Laboratory, Pasadena, CA, June 2007 https://trs.jpl.nasa.gov/handle/2014/40409
 */
@Suppress("MagicNumber")
private fun ringArea(coordinates: List<Position>): Double {
    if (coordinates.size < 3) return 0.0

    val sinLatitudes = coordinates.associateWith { sin(radians(it.latitude)) }

    return coordinates
        .plus(coordinates.take(2)) // adding the first two elements to the tail of the list
        .windowed(3, 1)
        .sumOf { (p1, p2, p3) ->
            (radians(p3.longitude) - radians(p1.longitude)) * requireNotNull(sinLatitudes[p2])
        } * AREA_EARTH_RADIUS * AREA_EARTH_RADIUS / 2
}
