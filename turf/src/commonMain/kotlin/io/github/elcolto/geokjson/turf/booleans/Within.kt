@file:Suppress("TooManyFunctions")

package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.measurement.computeBbox
import io.github.elcolto.geokjson.turf.measurement.midpoint

/**
 * [within] returns true if [geometry1] is completely within [geometry2].
 * The interiors of both geometries must intersect and, the interior and boundary of the primary (geometry a) must not
 * intersect the exterior of the secondary (geometry b).
 * [within] returns the exact opposite result of [contains].
 *
 * @throws IllegalStateException when the geometries are not comparable to each other.
 */
@Suppress("CyclomaticComplexMethod")
@ExperimentalTurfApi
@Throws(IllegalStateException::class)
public fun within(geometry1: Geometry, geometry2: Geometry): Boolean = when (geometry1) {
    is Point -> when (geometry2) {
        is MultiPoint -> isPointInMultiPoint(geometry1, geometry2)
        is LineString -> pointOnLine(geometry1, geometry2, ignoreEndVertices = true)
        is Polygon -> pointInPolygon(geometry1, geometry2, ignoreBoundary = true)
        is MultiPolygon -> pointInPolygon(geometry1, geometry2, ignoreBoundary = true)
        is GeometryCollection,
        is MultiLineString,
        is Point,
        -> error("geometry2 ${geometry2::class.simpleName} is not supported")
    }

    is MultiPoint -> when (geometry2) {
        is MultiPoint -> isMultiPointInMultiPoint(geometry1, geometry2)
        is LineString -> isMultiPointOnLine(geometry1, geometry2)
        is Polygon -> isMultiPointInPoly(geometry1, geometry2)
        is MultiPolygon -> isMultiPointInMultiPoly(geometry1, geometry2)
        is MultiLineString,
        is GeometryCollection,
        is Point,
        -> error("geometry2 ${geometry2::class.simpleName} is not supported")
    }

    is LineString -> when (geometry2) {
        is LineString -> isLineOnLine(geometry1, geometry2)
        is Polygon -> isLineInPoly(geometry1, geometry2)
        is MultiPolygon -> isLineInMultiPoly(geometry1, geometry2)
        is GeometryCollection,
        is MultiLineString,
        is MultiPoint,
        is Point,
        -> error("geometry2 ${geometry2::class.simpleName} is not supported")
    }

    is Polygon -> when (geometry2) {
        is Polygon -> isPolyInPoly(geometry1, geometry2)
        is MultiPolygon -> isPolyInMultiPoly(geometry1, geometry2)
        is GeometryCollection,
        is LineString,
        is MultiLineString,
        is MultiPoint,
        is Point,
        -> error("geometry2 ${geometry2::class.simpleName} is not supported")
    }

    is GeometryCollection,
    is MultiLineString,
    is MultiPolygon,
    -> error("geometry1 ${geometry1::class.simpleName} is not supported")
}

private fun isPointInMultiPoint(point: Point, multiPoint: MultiPoint) =
    multiPoint.coordinates.any { position -> position == point.coordinates }

private fun isMultiPointInMultiPoint(multiPoint1: MultiPoint, multiPoint2: MultiPoint) =
    multiPoint2.coordinates.containsAll(multiPoint1.coordinates)

@OptIn(ExperimentalTurfApi::class)
private fun isMultiPointOnLine(multiPoint: MultiPoint, lineString: LineString): Boolean =
    multiPoint.points.all { pointOnLine(it, lineString) } &&
        multiPoint.points.any { point -> pointOnLine(point, lineString, true) }

@OptIn(ExperimentalTurfApi::class)
private fun isMultiPointInPoly(multiPoint: MultiPoint, polygon: Polygon): Boolean =
    multiPoint.coordinates.all { pointInPolygon(it, polygon) } &&
        multiPoint.coordinates.any { pointInPolygon(it, polygon, ignoreBoundary = true) }

@OptIn(ExperimentalTurfApi::class)
private fun isMultiPointInMultiPoly(multiPoint: MultiPoint, polygon: MultiPolygon): Boolean =
    multiPoint.coordinates.all { pointInPolygon(it, polygon) } &&
        multiPoint.coordinates.any { pointInPolygon(it, polygon, ignoreBoundary = true) }

@OptIn(ExperimentalTurfApi::class)
private fun isLineOnLine(lineString1: LineString, lineString2: LineString) =
    lineString1.coordinates.all { position -> pointOnLine(position, lineString2) }

@OptIn(ExperimentalTurfApi::class)
private fun isLineInPoly(linestring: LineString, polygon: Polygon): Boolean {
    val polyBbox = polygon.bbox ?: computeBbox(polygon.coordAll())
    val lineBbox = linestring.bbox ?: computeBbox(linestring.coordAll())

    if (!doBBoxOverlap(polyBbox, lineBbox)) {
        return false
    }

    return linestring.coordinates.all { pointInPolygon(it, polygon) } && (
        linestring.coordinates.any { pointInPolygon(it, polygon, ignoreBoundary = true) } ||
            linestring.coordinates.zipWithNext().any { (start, end) ->
                pointInPolygon(midpoint(start, end), polygon, ignoreBoundary = true)
            }
        )
}

@OptIn(ExperimentalTurfApi::class)
private fun isLineInMultiPoly(linestring: LineString, polygon: MultiPolygon): Boolean {
    val polyBbox = polygon.bbox ?: computeBbox(polygon.coordAll())
    val lineBbox = linestring.bbox ?: computeBbox(linestring.coordAll())

    if (!doBBoxOverlap(polyBbox, lineBbox)) {
        return false
    }

    return linestring.coordinates.all { pointInPolygon(it, polygon) } && (
        linestring.coordinates.any { pointInPolygon(it, polygon, ignoreBoundary = true) } ||
            linestring.coordinates.zipWithNext().any { (start, end) ->
                pointInPolygon(midpoint(start, end), polygon, ignoreBoundary = true)
            }
        )
}

/**
 * Is Polygon1 in Polygon2
 * Only takes into account outer rings
 */
@OptIn(ExperimentalTurfApi::class)
private fun isPolyInPoly(geometry1: Polygon, geometry2: Polygon): Boolean {
    val poly1Bbox = geometry1.bbox ?: computeBbox(geometry1.coordAll())
    val poly2Bbox = geometry2.bbox ?: computeBbox(geometry2.coordAll())

    if (!doBBoxOverlap(poly2Bbox, poly1Bbox)) {
        return false
    }

    return geometry1.coordinates.firstOrNull()?.all { position ->
        pointInPolygon(position, geometry2)
    } ?: false
}

@OptIn(ExperimentalTurfApi::class)
private fun isPolyInMultiPoly(geometry1: Polygon, geometry2: MultiPolygon): Boolean {
    val poly1Bbox = geometry1.bbox ?: computeBbox(geometry1.coordAll())
    val poly2Bbox = geometry2.bbox ?: computeBbox(geometry2.coordAll())

    if (!doBBoxOverlap(poly2Bbox, poly1Bbox)) {
        return false
    }

    return geometry1.coordinates.firstOrNull()?.all { position ->
        pointInPolygon(position, geometry2)
    } ?: false
}

@Suppress("ReturnCount")
private fun doBBoxOverlap(bbox1: BoundingBox, bbox2: BoundingBox): Boolean {
    if (bbox1.coordinates[0] > bbox2.coordinates[0]) return false
    if (bbox1.coordinates[2] < bbox2.coordinates[2]) return false
    if (bbox1.coordinates[1] > bbox2.coordinates[1]) return false
    @Suppress("MagicNumber")
    if (bbox1.coordinates[3] < bbox2.coordinates[3]) return false
    return true
}
