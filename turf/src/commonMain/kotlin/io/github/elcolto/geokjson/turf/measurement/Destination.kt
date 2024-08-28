package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.EARTH_RADIUS
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.convertLength
import io.github.elcolto.geokjson.turf.degrees
import io.github.elcolto.geokjson.turf.lengthToRadians
import io.github.elcolto.geokjson.turf.radians
import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan

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

/**
 * Returns the destination Point having travelled the given distance along a Rhumb line from the [origin] Position with
 * the (variant) given bearing.
 *
 * @param distance distance from the starting point
 * @param units can be [Units.Degrees], [Units.Radians], [Units.Miles] or [Units.Kilometers].
 * @param bearing variant bearing angle ranging from -180 to 180 degrees from north
 *
 * @return destination
 */
@JvmOverloads
@ExperimentalTurfApi
public fun rhumbDestination(
    origin: Position,
    distance: Double,
    bearing: Double,
    units: Units = Units.Kilometers,
): Position {
    val wasNegativeDistance = distance < 0
    var distanceInMeters = convertLength(abs(distance), from = units, to = Units.Meters)
    if (wasNegativeDistance) {
        distanceInMeters = -abs(distanceInMeters)
    }

    val destination = calculateRhumbDestination(origin, distanceInMeters, bearing)

    val longitude = when {
        destination.longitude - origin.longitude > 180 -> destination.longitude - 360
        origin.longitude - destination.longitude > 180 -> destination.longitude + 360
        else -> destination.longitude
    }

    return Position(longitude, destination.latitude)
}

@Suppress("MagicNumber")
private fun calculateRhumbDestination(
    origin: Position,
    distance: Double,
    bearing: Double,
    radius: Double = EARTH_RADIUS,
): Position {
    val delta = distance / radius // angular distance in radians
    val lambda1 = (origin.longitude * PI) / 180 // to radians, but without normalize to 𝜋
    val phi1 = radians(origin.latitude)
    val theta = radians(bearing)

    val deltaPhi = delta * cos(theta)

    // check for some daft bugger going past the pole, normalise latitude if so
    val phi2 = (phi1 + deltaPhi).let {
        if (abs(it) > PI / 2) {
            if (it > 0) PI - it else -PI - it
        } else {
            it
        }
    }

    val deltaPsi = ln(
        tan(phi2 / 2 + PI / 4) / tan(phi1 / 2 + PI / 4),
    )

    // E-W course becomes ill-conditioned with 0/0
    val q = if (abs(deltaPsi) > 10e-12) deltaPhi / deltaPsi else cos(phi1)

    val deltaLambda = (delta * sin(theta)) / q

    val lambda2 = lambda1 + deltaLambda

    return Position(
        (((lambda2 * 180) / PI + 540) % 360) - 180,
        (phi2 * 180) / PI,
    )
}
