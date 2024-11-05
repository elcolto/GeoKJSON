package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.convertLength
import io.github.elcolto.geokjson.turf.misc.nearestPointDistance
import io.github.elcolto.geokjson.turf.misc.nearestPointOnLine

/**
 * Calculates the distance between a given point and the nearest point on a
 * line. Sometimes referred to as the cross track distance.
 *
 * @param position given [Position]
 * @param line [LineString] to calculate distance to
 * @param units can be anything supported by turf/convertLength
 * (ex: degrees, radians, miles, or kilometers)
 * @param method whether to calculate the distance based on geodesic (spheroid) or
 * planar (flat) method. Valid options are 'geodesic' or 'planar'.
 * @return distance between point and line
 * @example
 * ```kotlin
 * val pt = point(Position(0.0, 0.0))
 * val line = lineString(arrayOf(Position(1.0, 1.0), Position(-1.0, 1.0)))
 *
 * val distance = pointToLineDistance(pt, line, units = Units.Miles)
 * //=69.11854715938406
 * ```
 */
@ExperimentalTurfApi
public fun pointToLineDistance(
    point: Point,
    line: LineString,
    units: Units = Units.Kilometers,
    method: DistanceMethod = DistanceMethod.GEODESIC,
): Double = pointToLineDistance(point.coordinates, line, units, method)

/**
 * Calculates the distance between a given point and the nearest point on a
 * line. Sometimes referred to as the cross track distance.
 *
 * @param position given [Position]
 * @param line [LineString] to calculate distance to
 * @param units can be anything supported by turf/convertLength
 * (ex: degrees, radians, miles, or kilometers)
 * @param method whether to calculate the distance based on geodesic (spheroid) or
 * planar (flat) method. Valid options are 'geodesic' or 'planar'.
 * @return distance between point and line
 * @example
 * ```kotlin
 * val pt = Position(0.0, 0.0)
 * val line = lineString(arrayOf(Position(1.0, 1.0), Position(-1.0, 1.0)))
 *
 * val distance = pointToLineDistance(pt, line, units = Units.Miles)
 * //=69.11854715938406
 * ```
 */
@ExperimentalTurfApi
public fun pointToLineDistance(
    position: Position,
    line: LineString,
    units: Units = Units.Kilometers,
    method: DistanceMethod = DistanceMethod.GEODESIC,
): Double {
    val distance = line.coordinates
        .zipWithNext { a, b -> distanceToSegment(position, a, b, method) }
        .min()
    return convertLength(distance, Units.Degrees, units)
}

/**
 * Returns the distance between a point P on a segment AB.
 *
 * @param p external point
 * @param a first segment point
 * @param b second segment point
 * @param method the method to use for calculating the distance, either [DistanceMethod.GEODESIC] or [DistanceMethod.PLANAR]
 * @return distance
 */
@ExperimentalTurfApi
private fun distanceToSegment(
    p: Position,
    a: Position,
    b: Position,
    method: DistanceMethod = DistanceMethod.GEODESIC,
): Double = when (method) {
    DistanceMethod.GEODESIC ->
        // Use nearestPointOnLine to properly calculate distances on a spherical Earth.
        nearestPointOnLine(LineString(a, b), p, units = Units.Degrees).nearestPointDistance

    DistanceMethod.PLANAR -> {
        // Perform scalar calculations instead using rhumb lines.
        val v = Position(b.longitude - a.longitude, b.latitude - a.latitude)
        val w = Position(p.longitude - a.longitude, p.latitude - a.latitude)

        val c1 = dot(w, v)
        val c2 = dot(v, v)

        when {
            c1 <= 0 -> rhumbDistance(p, a, units = Units.Degrees)
            c2 <= c1 -> rhumbDistance(p, b, units = Units.Degrees)
            else -> {
                val b2 = c1 / c2
                val pb = Position(a.longitude + b2 * v.longitude, a.latitude + b2 * v.latitude)
                rhumbDistance(p, pb, units = Units.Degrees)
            }
        }
    }
}

/**
 * Calculates the dot product of two 2D vectors.
 *
 * @param u the first vector
 * @param v the second vector
 * @return the dot product of u and v
 */
private fun dot(u: Position, v: Position): Double {
    return u.longitude * v.longitude + u.latitude * v.latitude
}

/**
 * Enum representing the method to use for calculating distances.
 */
public enum class DistanceMethod {
    GEODESIC,
    PLANAR,
}
