package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.measurement.bearing
import io.github.elcolto.geokjson.turf.measurement.destination
import io.github.elcolto.geokjson.turf.measurement.distance
import kotlin.math.max

public object NearestPointOnLine {

    /**
     * Identifier to access distance to target within [Feature.properties]
     */
    public const val DISTANCE_TO_POINT: String = "distance"

    /**
     * Identifier to access distance along the line from the start to target within [Feature.properties]
     */
    public const val LOCATION: String = "location"

    /**
     * Identifier to access index of target within [Feature.properties]
     */
    public const val INDEX: String = "index"
}

/**
 * Finds the closest [Position] along a [LineString] to a given position
 *
 * @param line The [LineString] to find a position along
 * @param point The [Position] given to find the closest point along the [line]
 * @return [Feature] with [Feature.properties]:
 *  - `distance`: Distance between the input position and [Feature.geometry]
 *  - `location`: Distance along the [line] from the start to the [Feature.geometry]
 *  - `index`: Index of the segment of the line on which [point] lies
 */
@ExperimentalTurfApi
public fun nearestPointOnLine(line: LineString, point: Position, units: Units = Units.Kilometers): Feature<Point> =
    nearestPointOnLine(listOf(line), point, units)

/**
 * Finds the closest [Position] along a [MultiLineString] to a given position
 *
 * @param lines The [MultiLineString] to find a position along
 * @param point The [Position] given to find the closest point along the [lines]
 * @return [Feature] with [Feature.properties]:
 *  - `distance`: Distance between the input position and [Feature.geometry]
 *  - `location`: Distance along the [lines] from the start to the [Feature.geometry]
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
    var closest = feature(
        Position(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
        Double.POSITIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        -1,
    )

    var length = 0.0

    lines.flatMap { it.points }
        .zipWithNext()
        .forEachIndexed { i, (start, stop) ->

            val startDistance = distance(point, start.coordinates, units = units)
            val stopDistance = distance(point, stop.coordinates, units = units)

            val sectionLength = distance(start.coordinates, stop.coordinates, units = units)

            val heightDistance = max(startDistance, stopDistance)
            val direction = bearing(start.coordinates, stop.coordinates)
            val perpPoint1 = destination(point, heightDistance, direction + 90, units = units)
            val perpPoint2 = destination(point, heightDistance, direction - 90, units = units)

            val intersect = lineIntersect(
                LineString(perpPoint1, perpPoint2),
                LineString(start.coordinates, stop.coordinates),
            ).getOrNull(0)

            if (startDistance < closest.nearestPointDistance) {
                closest = feature(position = start.coordinates, location = length, distance = startDistance, index = i)
            }

            if (stopDistance < closest.nearestPointDistance) {
                closest = feature(
                    position = stop.coordinates,
                    location = length + sectionLength,
                    distance = stopDistance,
                    index = i + 1,
                )
            }

            if (intersect != null && distance(point, intersect, units = units) < closest.nearestPointDistance) {
                val intersectDistance = distance(point, intersect, units = units)
                closest = feature(
                    position = intersect,
                    distance = intersectDistance,
                    location = length + distance(start.coordinates, intersect, units = units),
                    index = i,
                )
            }

            length += sectionLength
        }
    return closest
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
 * `distance` on [Feature.properties] calculated with [nearestPointOnLine]. Will throw an [NoSuchElementException] when
 * property is not available. For a safe cal access `properties` with key [NearestPointOnLine.DISTANCE_TO_POINT]
 */
public val Feature<Point>.nearestPointDistance: Double
    get() = properties.getValue(NearestPointOnLine.DISTANCE_TO_POINT) as Double

/**
 * `location` on [Feature.properties] calculated with [nearestPointOnLine]. Will throw an [NoSuchElementException] when
 * property is not available. For a safe cal access `properties` with key [NearestPointOnLine.LOCATION]
 */
public val Feature<Point>.nearestPointLocation: Double
    get() = properties.getValue(NearestPointOnLine.LOCATION) as Double

/**
 * `index` on [Feature.properties] calculated with [nearestPointOnLine]. Will throw an [NoSuchElementException] when
 * property is not available. For a safe cal access `properties` with key [NearestPointOnLine.INDEX]
 */
public val Feature<Point>.nearestPointIndex: Int
    get() = properties.getValue(NearestPointOnLine.INDEX) as Int
