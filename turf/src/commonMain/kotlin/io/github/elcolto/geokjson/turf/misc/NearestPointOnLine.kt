package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.measurement.distance

public object NearestPointOnLine {

    /**
     * Identifier to access distance to target within `Feature.properties`
     */
    public const val DISTANCE_TO_POINT: String = "dist"

    /**
     * Identifier to access distance along the line from the start to target within `Feature.properties`
     */
    public const val LOCATION: String = "location"

    /**
     * Identifier to access index of target within `Feature.properties`
     */
    public const val INDEX: String = "index"
}

/**
 * Finds the closest `Position` along a `LineString` to a given position
 *
 * @param line The `LineString` to find a position along
 * @param point The `Position` given to find the closest point along the [line]
 * @return `Feature` with `Feature.properties`:
 *  - `distance`: Distance between the input position and `Feature.geometry`
 *  - `location`: Distance along the [line] from the start to the `Feature.geometry`
 *  - `index`: Index of the segment of the line on which [point] lies
 */
@ExperimentalTurfApi
public fun nearestPointOnLine(line: LineString, point: Position, units: Units = Units.Kilometers): Feature<Point> =
    nearestPointOnLine(listOf(line), point, units)

/**
 * Finds the closest `Position` along a `MultiLineString` to a given position
 *
 * @param lines The `MultiLineString` to find a position along
 * @param point The `Position` given to find the closest point along the [lines]
 * @return `Feature` with `Feature.properties`:
 *  - `distance`: Distance between the input position and `Feature.geometry`
 *  - `location`: Distance along the [lines] from the start to the `Feature.geometry`
 *  - `index`: Index of the segment of the line on which [point] lies
 */
@ExperimentalTurfApi
public fun nearestPointOnLine(
    lines: MultiLineString,
    point: Position,
    units: Units = Units.Kilometers,
): Feature<Point> {
    return nearestPointOnLine(lines.lines, point, units)
}

@ExperimentalTurfApi
internal fun nearestPointOnLine(
    lines: List<LineString>,
    point: Position,
    units: Units = Units.Kilometers,
): Feature<Point> {
    require(lines.isNotEmpty()) { "lines must not be empty" }
    require(!point.latitude.isNaN() && !point.longitude.isNaN()) { "point must be valid" }

    var closestPoint = feature(
        position = Position(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
        distance = Double.POSITIVE_INFINITY,
        location = -1.0,
        index = -1,
    )
    var currentLocation = 0.0

    lines.flatMap { line ->
        line.coordinates.zipWithNext().mapIndexed { segmentIndex, (startPos, stopPos) ->
            Triple(segmentIndex, startPos, stopPos)
        }
    }.forEach { (segmentIndex, startPos, stopPos) ->
        val sectionLength = distance(startPos, stopPos, units)

        val (intersectPos, _, wasEnd) = when {
            startPos == point -> Triple(startPos, false, false)
            stopPos == point -> Triple(stopPos, false, true)
            else -> nearestPointOnSegment(startPos, stopPos, point)
        }

        val intersectDistance = distance(point, intersectPos, units)

        if (intersectDistance < closestPoint.nearestPointDistance) {
            val location = currentLocation + distance(startPos, intersectPos, units)
            closestPoint = feature(
                position = intersectPos,
                distance = intersectDistance,
                location = location,
                index = if (wasEnd) segmentIndex + 1 else segmentIndex,
            )
        }

        currentLocation += sectionLength
    }
    return closestPoint
}

@ExperimentalTurfApi
private fun feature(position: Position, distance: Double, location: Double, index: Int) = Feature(
    geometry = Point(position),
    properties = mapOf(
        NearestPointOnLine.DISTANCE_TO_POINT to distance,
        NearestPointOnLine.LOCATION to location,
        NearestPointOnLine.INDEX to index,
    ),
)

/**
 * `distance` on `Feature.properties` calculated with [nearestPointOnLine]. Will throw a `NoSuchElementException` when
 * property is not available. For a safe cal access `properties` with key [NearestPointOnLine.DISTANCE_TO_POINT]
 */
public val Feature<Point>.nearestPointDistance: Double
    get() = properties.getValue(NearestPointOnLine.DISTANCE_TO_POINT) as Double

/**
 * `location` on `Feature.properties` calculated with [nearestPointOnLine]. Will throw a `NoSuchElementException` when
 * property is not available. For a safe cal access `properties` with key `NearestPointOnLine.LOCATION`
 */
public val Feature<Point>.nearestPointLocation: Double
    get() = properties.getValue(NearestPointOnLine.LOCATION) as Double

/**
 * `index` on `Feature.properties` calculated with [nearestPointOnLine]. Will throw a `NoSuchElementException` when
 * property is not available. For a safe cal access `properties` with key `NearestPointOnLine.INDEX`
 */
public val Feature<Point>.nearestPointIndex: Int
    get() = properties.getValue(NearestPointOnLine.INDEX) as Int
