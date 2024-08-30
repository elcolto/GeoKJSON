package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.GeoJson
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.measurement.center
import io.github.elcolto.geokjson.turf.measurement.centroid
import io.github.elcolto.geokjson.turf.measurement.computeBbox
import io.github.elcolto.geokjson.turf.measurement.rhumbBearing
import io.github.elcolto.geokjson.turf.measurement.rhumbDestination
import io.github.elcolto.geokjson.turf.measurement.rhumbDistance

/**
 * Scale a [GeoJson] from a given point by a factor of scaling (ex: factor=2 would make the [GeoJson] 200% larger).
 * If a [GeometryCollection] is provided, the origin point will be calculated based on each individual [Feature].
 * @param geometry GeoJson to be scaled
 * @param factor of scaling, positive values greater than 0. Numbers between 0 and 1 will shrink the geojson, numbers
 * greater than 1 will expand it, a factor of 1 will not change the geojson.
 * @param origin Point from which the scaling will occur (string options: sw/se/nw/ne/center/centroid)
 * @return scaled [Geometry]
 */
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun <T : Geometry> scale(geometry: T, factor: Double, origin: ScaleOrigin = ScaleOrigin.Centroid): T {
    require(factor > 0.0) { "invalid scaling factor. Must be a positive value" }
    if (factor == 1.0) return geometry
    val originPosition = defineOrigin(geometry, origin)

    fun scaledPosition(pos: Position): Position {
        val originalDistance = rhumbDistance(originPosition, pos)
        val bearing = rhumbBearing(originPosition, pos)
        val newDistance = originalDistance * factor
        return rhumbDestination(origin = originPosition, distance = newDistance, bearing = bearing)
    }

    val newGeometry: Geometry = when (geometry) {
        is Point -> geometry
        is MultiPoint -> MultiPoint(
            geometry.coordinates.map(::scaledPosition),
        )

        is LineString -> LineString(geometry.coordinates.map(::scaledPosition))
        is MultiLineString -> MultiLineString(
            geometry.coordinates.map { line -> line.map(::scaledPosition) },
        )

        is Polygon -> Polygon(
            geometry.coordinates.map { line -> line.map(::scaledPosition) },
        )

        is MultiPolygon -> MultiPolygon(
            geometry.coordinates.map { polygon -> polygon.map { line -> line.map(::scaledPosition) } },
        )

        else -> error("type ${geometry::class.simpleName} is not supported to scale")
    }
    return newGeometry as T
}

@OptIn(ExperimentalTurfApi::class)
private fun <T : Geometry> defineOrigin(geometry: T, origin: ScaleOrigin): Position {
    val boundingBox = geometry.bbox ?: computeBbox(geometry.coordAll())
    val (east, north) = boundingBox.northeast
    val (west, south) = boundingBox.southwest

    return when (origin) {
        ScaleOrigin.SouthWest -> Position(west, south)
        ScaleOrigin.SouthEast -> Position(east, south)
        ScaleOrigin.NorthWest -> Position(west, north)
        ScaleOrigin.NorthEast -> Position(east, north)
        ScaleOrigin.Center -> center(geometry).coordinates
        ScaleOrigin.Centroid -> centroid(geometry).coordinates
        is ScaleOrigin.Coordinates -> origin.position
    }
}

/**
 * Option to define an origin, at which point of an origin geometry the scaling will occur
 */
public sealed class ScaleOrigin {
    public data object SouthWest : ScaleOrigin()
    public data object SouthEast : ScaleOrigin()
    public data object NorthWest : ScaleOrigin()
    public data object NorthEast : ScaleOrigin()
    public data object Center : ScaleOrigin()
    public data object Centroid : ScaleOrigin()
    public data class Coordinates(public val position: Position) : ScaleOrigin()
}
