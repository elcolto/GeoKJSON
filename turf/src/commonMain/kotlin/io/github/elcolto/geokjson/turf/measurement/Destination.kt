package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.degrees
import io.github.elcolto.geokjson.turf.lengthToRadians
import io.github.elcolto.geokjson.turf.radians
import kotlin.jvm.JvmOverloads
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Takes a [position][origin] and calculates the location of a destination position given a distance in
 * degrees, radians, miles, or kilometers; and bearing in degrees.
 * This uses the Haversine formula to account for global curvature.
 *
 * @param origin starting point
 * @param distance distance from the origin point
 * @param bearing ranging from -180 to 180
 * @param units Unit of [distance]
 * @return destination position
 *
 * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
 */
@JvmOverloads
@ExperimentalTurfApi
public fun destination(origin: Position, distance: Double, bearing: Double, units: Units = Units.Kilometers): Position {
    val longitude1 = radians(origin.longitude)
    val latitude1 = radians(origin.latitude)
    val bearingRad = radians(bearing)
    val radians = lengthToRadians(distance, units)

    val latitude2 = asin(sin(latitude1) * cos(radians) + cos(latitude1) * sin(radians) * cos(bearingRad))
    val longitude2 = longitude1 + atan2(
        sin(bearingRad) * sin(radians) * cos(latitude1),
        cos(radians) - sin(latitude1) * sin(latitude2),
    )

    return Position(
        degrees(longitude2),
        degrees(latitude2),
    )
}
