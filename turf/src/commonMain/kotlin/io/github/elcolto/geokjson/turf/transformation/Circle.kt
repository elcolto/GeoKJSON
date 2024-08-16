package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.measurement.computeBbox
import io.github.elcolto.geokjson.turf.measurement.destination

/**
 * Takes a [Point] and calculates the circle polygon given a radius in degrees, radians, miles, or kilometers; and steps
 * for precision.
 *
 * @param center center point of circle
 * @param radius radius of the circle defined in [units]
 * @param steps number of steps, must be at least four. Default is 64
 * @param units unit of [radius], default is [Units.Kilometers]
 */
@Suppress("MagicNumber")
@ExperimentalTurfApi
public fun circle(center: Point, radius: Double, steps: Int = 64, units: Units = Units.Kilometers): Polygon {
    require(steps >= 4) { "circle needs to have four or more coordinates." }
    require(radius > 0) { "radius must be a positive value" }
    val coordinates = (0..steps).map { step ->
        destination(center.coordinates, radius, (step * -360) / steps.toDouble(), units)
    }
    val ring = coordinates.plus(coordinates.first())
    return Polygon(
        coordinates = listOf(ring),
        bbox = computeBbox(ring),
    )
}
