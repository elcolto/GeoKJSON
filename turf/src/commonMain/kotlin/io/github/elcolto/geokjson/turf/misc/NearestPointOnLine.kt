package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.measurement.bearing
import io.github.elcolto.geokjson.turf.measurement.destination
import io.github.elcolto.geokjson.turf.measurement.distance
import kotlin.math.max

/**
 * Result values from [nearestPointOnLine].
 *
 * @property point The point on the line nearest to the input position
 * @property distance Distance between the input position and [point]
 * @property location Distance along the line from the stat to the [point]
 * @property index Index of the segment of the line on which [point] lies.
 */
@ExperimentalTurfApi
public data class NearestPointOnLine(
    val point: Position,
    val distance: Double,
    val location: Double,
    val index: Int,
)

/**
 * Finds the closest [Position] along a [LineString] to a given position
 *
 * @param line The [LineString] to find a position along
 * @param point The [Position] given to find the closest point along the [line]
 * @return The closest position along the line
 */
@ExperimentalTurfApi
public fun nearestPointOnLine(line: LineString, point: Position, units: Units = Units.Kilometers): NearestPointOnLine =
    nearestPointOnLine(listOf(line.coordinates), point, units)

/**
 * Finds the closest [Position] along a [MultiLineString] to a given position
 *
 * @param lines The [MultiLineString] to find a position along
 * @param point The [Position] given to find the closest point along the [lines]
 * @return The closest position along the lines
 */
@ExperimentalTurfApi
public fun nearestPointOnLine(
    lines: MultiLineString,
    point: Position,
    units: Units = Units.Kilometers,
): NearestPointOnLine {
    return nearestPointOnLine(lines.coordinates, point, units)
}

@ExperimentalTurfApi
internal fun nearestPointOnLine(
    lines: List<List<Position>>,
    point: Position,
    units: Units = Units.Kilometers,
): NearestPointOnLine {
    var closest = NearestPointOnLine(
        Position(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
        Double.POSITIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        -1,
    )

    var length = 0.0

    lines.forEach { coords ->
        for (i in 0 until coords.size - 1) {
            val start = coords[i]
            val startDistance = distance(point, coords[i], units = units)
            val stop = coords[i + 1]
            val stopDistance = distance(point, coords[i + 1], units = units)

            val sectionLength = distance(start, stop, units = units)

            val heightDistance = max(startDistance, stopDistance)
            val direction = bearing(start, stop)
            val perpPoint1 = destination(point, heightDistance, direction + 90, units = units)
            val perpPoint2 = destination(point, heightDistance, direction - 90, units = units)

            val intersect = lineIntersect(LineString(perpPoint1, perpPoint2), LineString(start, stop)).getOrNull(0)

            if (startDistance < closest.distance) {
                closest = closest.copy(point = start, location = length, distance = startDistance, index = i)
            }

            if (stopDistance < closest.distance) {
                closest = closest.copy(
                    point = stop,
                    location = length + sectionLength,
                    distance = stopDistance,
                    index = i + 1,
                )
            }

            if (intersect != null && distance(point, intersect, units = units) < closest.distance) {
                val intersectDistance = distance(point, intersect, units = units)
                closest = closest.copy(
                    point = intersect,
                    distance = intersectDistance,
                    location = length + distance(start, intersect, units = units),
                    index = i,
                )
            }

            length += sectionLength
        }
    }

    return closest
}
