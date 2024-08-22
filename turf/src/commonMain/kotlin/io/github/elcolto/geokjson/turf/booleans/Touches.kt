@file:Suppress("CyclomaticComplexMethod")

package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll

/**
 * @return true if none of the points common to both geometries intersect the interiors of both geometries.
 */
@ExperimentalTurfApi
public fun touches(geometry1: Geometry, geometry2: Geometry): Boolean {
    if (geometry1.coordAll().isEmpty() || geometry2.coordAll().isEmpty()) return false

    return when (geometry1) {
        is Point -> pointTouchesOther(geometry2, geometry1)
        is MultiPoint -> multiPointTouchesOther(geometry2, geometry1)
        is LineString -> lineTouchesOther(geometry1, geometry2)
        is MultiLineString -> multiLineTouchesOther(geometry1, geometry2)
        is Polygon -> polygonTouchesOther(geometry2, geometry1)
        is MultiPolygon -> multiPolygonTouchesOther(geometry2, geometry1)
        else -> error("type of geometry1 is not supported")
    }
}

@ExperimentalTurfApi
private fun pointTouchesOther(geometry2: Geometry, geometry1: Point) = when (geometry2) {
    is LineString -> isPointOnLineEnd(geometry1, geometry2)
    is MultiLineString -> geometry2.lines.any { line -> touches(geometry1, line) }
    is MultiPoint -> geometry2.coordinates.any { geometry1.coordinates == it }
    is MultiPolygon -> geometry2.polygons.any { touches(geometry1, it) }
    is Point -> geometry1.coordinates == geometry2.coordinates
    is Polygon -> geometry2.lines.any { pointOnLine(geometry1, it) }
    else -> error("${geometry2::class.simpleName} as type of geometry2 is not supported ")
}

@ExperimentalTurfApi
private fun multiPointTouchesOther(geometry2: Geometry, geometry1: MultiPoint) = when (geometry2) {
    is LineString -> geometry1.points.any { point -> isPointOnLineEnd(point, geometry2) } &&
        geometry1.points.none { point -> pointOnLine(point, geometry2, true) }

    is MultiLineString -> geometry2.lines.any { line -> touches(geometry1, line) }

    is Point,
    is MultiPoint,
    -> geometry1.coordAll().intersect(geometry2.coordAll().toSet()).isNotEmpty()

    is Polygon -> geometry1.points.any { point -> pointOnLine(point, geometry2.lines.first()) } &&
        geometry1.points.none { point -> pointInPolygon(point, geometry2, true) }

    is MultiPolygon -> geometry2.polygons.any { touches(geometry1, it) }
    else -> error("${geometry2::class.simpleName} as type of geometry2 is not supported ")
}

@ExperimentalTurfApi
private fun lineTouchesOther(geometry1: LineString, geometry2: Geometry) = when (geometry2) {
    is LineString -> (
        isPointOnLineEnd(geometry1.coordinates.first(), geometry2) ||
            isPointOnLineEnd(geometry1.coordinates.last(), geometry2)
        ) &&
        geometry1.coordinates.none { pointOnLine(it, geometry2, ignoreEndVertices = true) }

    is MultiLineString -> geometry2.lines.any { line ->
        (
            isPointOnLineEnd(geometry1.coordinates.first(), line) ||
                isPointOnLineEnd(geometry1.coordinates.last(), line)
            ) &&
            geometry1.coordinates.none { position -> pointOnLine(position, line, true) }
    }

    is MultiPolygon -> geometry1.coordinates.any { position ->
        geometry2.polygons.any { pointOnLine(position, it.lines.first()) } &&
            geometry2.polygons.none { pointInPolygon(position, it, true) }
    }

    is Point -> isPointOnLineEnd(geometry2.coordinates, geometry1)
    is MultiPoint -> geometry2.coordinates.fold(false) { foundTouchingPoint, position ->
        isPointOnLineEnd(position, geometry1) || !pointOnLine(position, geometry1, true) && foundTouchingPoint
    }

    is Polygon -> geometry1.coordinates.fold(false) { foundTouchingPoint, position ->
        when {
            !foundTouchingPoint && pointOnLine(position, geometry2.lines.first()) -> true
            pointInPolygon(position, geometry2, true) -> false
            else -> foundTouchingPoint
        }
    }

    else -> error("${geometry2::class.simpleName} as type of geometry2 is not supported ")
}

@ExperimentalTurfApi
private fun multiLineTouchesOther(geometry1: MultiLineString, geometry2: Geometry) = when (geometry2) {
    is LineString -> geometry1.lines.any { line ->
        (
            isPointOnLineEnd(line.points.first(), geometry2) ||
                isPointOnLineEnd(line.points.last(), geometry2)
            ) &&
            geometry2.points.all { point -> !pointOnLine(point, line, true) }
    }

    is MultiLineString -> geometry1.lines.any { line -> touches(line, geometry2) }

    is MultiPoint -> geometry1.lines.any { line ->
        geometry2.points.any { point -> isPointOnLineEnd(point, line) } &&
            geometry2.points.all { point -> !pointOnLine(point, line, true) }
    }

    is MultiPolygon -> geometry2.polygons.any { polygon ->
        touches(geometry1, polygon)
    }

    is Point -> geometry1.lines.any { line -> isPointOnLineEnd(geometry2, line) }
    is Polygon -> geometry1.lines.any { line ->
        line.points.any { point -> pointOnLine(point, geometry2.lines.first()) }
    } && geometry1.lines.none { line ->
        line.points.any { point -> pointInPolygon(point, geometry2, true) }
    }

    else -> error("${geometry2::class.simpleName} as type of geometry2 is not supported ")
}

@ExperimentalTurfApi
private fun polygonTouchesOther(geometry2: Geometry, geometry1: Polygon) = when (geometry2) {
    is LineString -> geometry2.points.any { point -> pointOnLine(point, geometry1.lines.first()) } &&
        geometry2.points.none { point -> pointInPolygon(point, geometry1, true) }

    is MultiLineString -> geometry2.lines.flatMap { it.points }.let { points ->
        points.any { pointOnLine(it, geometry1.lines.first()) } &&
            points.none { pointInPolygon(it, geometry1, true) }
    }

    is MultiPolygon -> geometry1.lines.flatMap { it.points }.let { points ->
        geometry2.polygons.map { it.lines.first() }
            .any { line -> points.any { point -> pointOnLine(point, line) } } &&
            geometry2.polygons.none { polygon ->
                points.any { point -> pointInPolygon(point, polygon, true) }
            }
    }

    is Point -> geometry1.lines.any { line -> pointOnLine(geometry2, line) }
    is MultiPoint -> geometry2.points.any { point -> pointOnLine(point, geometry1.lines.first()) } &&
        geometry2.points.none { point -> pointInPolygon(point, geometry1, true) }

    is Polygon -> geometry1.lines.first().points.let { points ->
        points.any { point -> pointOnLine(point, geometry2.lines.first()) } &&
            points.none { point -> pointInPolygon(point, geometry2, true) }
    }

    else -> error("type of geometry2 is not supported")
}

@ExperimentalTurfApi
private fun multiPolygonTouchesOther(geometry2: Geometry, geometry1: MultiPolygon) = when (geometry2) {
    is LineString -> geometry1.polygons.first().lines.any { line ->
        geometry2.points.any { point -> pointOnLine(point, (line)) } &&
            geometry2.points.none { point -> pointInPolygon(point, geometry1.polygons.first(), true) }
    }

    is MultiLineString -> geometry1.polygons.any { polygon ->
        geometry2.lines.any { line ->
            line.points.any { point -> pointOnLine(point, polygon.lines.first()) } &&
                line.points.none { point -> pointInPolygon(point, geometry1.polygons.first()) }
        }
    }

    is MultiPolygon -> geometry2.polygons.let { polygons ->
        geometry1.polygons.first().lines.flatMap { it.points }
            .any { point ->
                polygons.flatMap { it.lines }
                    .any { line -> pointOnLine(point, line) } &&
                    polygons.none { polygon -> pointInPolygon(point, polygon, true) }
            }
    }

    is Point -> geometry1.polygons.first().lines.any { pointOnLine(geometry2, it) }
    is MultiPoint -> geometry1.polygons.first().lines.any { line ->
        geometry2.points.any { point -> pointOnLine(point, (line)) } &&
            geometry2.points.none { point -> pointInPolygon(point, geometry1.polygons.first(), true) }
    }

    is Polygon -> geometry1.polygons.flatMap { it.lines }.flatMap { it.points }.let { points ->
        points.any { pointOnLine(it, geometry2.lines.first()) } &&
            points.none { pointInPolygon(it, geometry2, true) }
    }

    else -> error("${geometry2::class.simpleName} as type of geometry2 is not supported ")
}

private fun isPointOnLineEnd(point: Point, lineString: LineString): Boolean =
    isPointOnLineEnd(point.coordinates, lineString)

private fun isPointOnLineEnd(position: Position, lineString: LineString) =
    lineString.coordinates.first() == position || lineString.coordinates.last() == position
