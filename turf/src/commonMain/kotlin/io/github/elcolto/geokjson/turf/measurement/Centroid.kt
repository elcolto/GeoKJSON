package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.unwrappedCoordinates

/**
 * Computes the centroid as the mean of all vertices within the object.
 *
 * @param geometry [Geometry] to be centered
 * @return [Point] the centroid of the input object
 */
@ExperimentalTurfApi
public fun centroid(geometry: Geometry): Point {
    // wrapping coordinate of Polygon must be ignored
    val coordinates = geometry.unwrappedCoordinates()
    val (ySum, xSum) = coordinates.fold(Pair(0.0, 0.0)) { latLon, pos ->
        val (lat, lon) = latLon
        lat + pos.latitude to lon + pos.longitude
    }
    return Point(
        Position(
            latitude = ySum / coordinates.size,
            longitude = xSum / coordinates.size,
        ),
    )
}
