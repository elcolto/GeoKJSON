package io.github.elcolto.geokjson.turf.coordinatemutation

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.booleans.pointOnLine

/**
 * Removes redundant coordinates from any GeoJSON Geometry.
 *
 * @param geometry input Geometry
 * @return the cleaned input Geometry
 * @example
 * ```kotlin
 * val line = LineString(arrayOf(
 *     Position(0.0, 0.0),
 *     Position(0.0, 2.0),
 *     Position(0.0, 5.0),
 *     Position(0.0, 8.0),
 *     Position(0.0, 8.0),
 *     Position(0.0, 10.0)
 * ))
 * val multiPoint = MultiPoint(arrayOf(
 *     Position(0.0, 0.0),
 *     Position(0.0, 0.0),
 *     Position(2.0, 2.0)
 * ))
 *
 * val cleanedLine = cleanCoordinates(line)
 * //= {
 * //= "type": "LineString",
 * //= "coordinates": [ [0.0, 0.0], [0.0, 10.0] ]
 * //= }
 *
 * val cleanedMultiPoint = cleanCoordinates(multiPoint)
 * //= {
 * //= "type": "MultiPoint",
 * //= "coordinates": [ [0.0, 0.0], [2.0, 2.0] ]
 * //= }
 * ```
 */
@Suppress("MagicNumber")
public fun <T : Geometry> cleanCoordinates(geometry: T): T = when (geometry) {
    is LineString -> LineString(cleanLine(geometry.coordinates))
    is MultiLineString -> geometry.coordinates.map { cleanLine(it) }.let { MultiLineString(it) }
    is Polygon -> geometry.coordinates.map { cleanLine(it) }.also { cleanedCoordinates ->
        require(cleanedCoordinates.first().size >= 4) { "Invalid Polygon: A Polygon must have at least 4 positions." }
    }.let { Polygon(it) }

    is MultiPolygon -> geometry.coordinates.map { polygon ->
        polygon.map { ring -> cleanLine(ring) }
    }.onEach { polygon ->
        require(polygon.first().size >= 4) { "Invalid MultiPolygon: Each Polygon must have at least 4 positions." }
    }.let { MultiPolygon(it) }

    is Point -> geometry
    is MultiPoint -> {
        val existing = mutableSetOf<String>()
        geometry.coordinates.filter { cleanedCoordinates ->
            val key = "${cleanedCoordinates.longitude}-${cleanedCoordinates.latitude}"
            (key !in existing).also { existing.add(key) }
        }.let { MultiPoint(it) }
    }

    else -> error("${geometry::class.simpleName} geometry not supported")
} as T

@OptIn(ExperimentalTurfApi::class)
@Suppress("MagicNumber")
private fun cleanLine(line: List<Position>): List<Position> {
    // handle "clean" segment
    if (line.size == 2 && line[0] != line[1]) return line

    val newPoints = line.fold(mutableListOf<Position>()) { acc, position ->
        acc.apply {
            add(position)
            if (size > 2 && pointOnLine(
                    this[size - 2],
                    LineString(this[size - 3], this[size - 1]),
                )
            ) {
                removeAt(size - 2)
            }
        }
    }

    return if (newPoints.size > 2 && pointOnLine(
            newPoints[newPoints.size - 2],
            LineString(newPoints[newPoints.size - 3], newPoints.last()),
        )
    ) {
        newPoints.dropLast(1)
    } else {
        newPoints
    }
}
