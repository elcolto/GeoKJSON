package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.classification.nearestPointFeature
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.misc.nearestPointIndex

/**
 * Finds the tangents of a [Polygon|MultiPolygon] from a [Point].
 *
 * @param point to calculate the tangent points from
 * @param polygon to get tangents from
 * @returns Pair containing the two tangent points
 */
@ExperimentalTurfApi
public fun polygonTangents(point: Point, polygon: Geometry): List<Point> = polygonTangents(point.coordinates, polygon)

/**
 * Finds the tangents of a [Polygon|MultiPolygon] from a [Position].
 *
 * @param position to calculate the tangent points from
 * @param polygon to get tangents from
 * @returns List containing the two tangent points
 */
@ExperimentalTurfApi
public fun polygonTangents(position: Position, polygon: Geometry): List<Point> {
    require(polygon is Polygon || polygon is MultiPolygon) { "Parameter polygon must be Polygon or MultiPolygon" }

    val bbox = bbox(polygon)
    val nearestPtIndex = if (position in bbox) {
        nearestPointFeature(position, polygon.coordAll().map { Point(it) }).nearestPointIndex
    } else {
        0
    }

    fun processRings(
        rings: List<List<Position>>,
        initialRtan: Position,
        initialLtan: Position,
    ): Pair<Position, Position> {
        val firstRing = rings.first()
        val eprev = isLeft(firstRing.first(), firstRing.last(), position)
        return rings.fold(initialRtan to initialLtan) { (rtan, ltan), ring ->
            processPolygon(ring, position, eprev, rtan, ltan)
        }
    }

    return when (polygon) {
        is Polygon -> {
            val rings = polygon.coordinates
            val initialRightTan = rings.first()[nearestPtIndex]
            val initialLeftTan =
                if (initialRightTan.latitude < position.latitude) initialRightTan else rings.first().first()

            val (finalRightTan, finalLeftTan) = processRings(rings, initialRightTan, initialLeftTan)
            listOf(Point(finalRightTan), Point(finalLeftTan))
        }

        is MultiPolygon -> {
            val initialRightTan = polygon.coordinates.flatten().flatten()[nearestPtIndex]
            val firstPosition = polygon.coordinates.first().first().first()

            val initialLeftTan =
                if (initialRightTan.latitude < position.latitude) initialRightTan else firstPosition

            val (finalRightTan, finalLeftTan) = polygon.coordinates.fold(
                initialRightTan to initialLeftTan,
            ) { (rightTan, leftTan), rings ->
                processRings(rings, rightTan, leftTan)
            }
            listOf(Point(finalRightTan), Point(finalLeftTan))
        }

        else -> error("Type not applicable") // Should not be reached because of require
    }
}

private fun processPolygon(
    polygonCoords: List<Position>,
    ptCoords: Position,
    eprev: Double,
    rtan: Position,
    ltan: Position,
): Pair<Position, Position> = polygonCoords.fold(Triple(eprev, rtan, ltan)) { acc, currentPosition ->
    val nextPosition = polygonCoords[(polygonCoords.indexOf(currentPosition) + 1) % polygonCoords.size]
    val enext = isLeft(currentPosition, nextPosition, ptCoords)

    val nextRightTan = if (acc.first <= 0 && enext > 0 && !isBelow(ptCoords, currentPosition, acc.second)) {
        currentPosition
    } else {
        acc.second
    }

    val nextLeftTan = if (acc.first > 0 && enext <= 0 && !isAbove(ptCoords, currentPosition, acc.third)) {
        currentPosition
    } else {
        acc.third
    }

    Triple(enext, nextRightTan, nextLeftTan)
}
    .let { it.second to it.third }

private fun isAbove(point1: Position, point2: Position, point3: Position): Boolean = isLeft(point1, point2, point3) > 0

private fun isBelow(point1: Position, point2: Position, point3: Position): Boolean = isLeft(point1, point2, point3) < 0

private fun isLeft(point1: Position, point2: Position, point3: Position): Double =
    (point2.longitude - point1.longitude) * (point3.latitude - point1.latitude) -
        (point3.longitude - point1.longitude) * (point2.latitude - point1.latitude)
