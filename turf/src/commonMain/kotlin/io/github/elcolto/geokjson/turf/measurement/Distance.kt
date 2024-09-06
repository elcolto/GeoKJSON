package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.EARTH_RADIUS
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.compensateAntiMeridianLongitude
import io.github.elcolto.geokjson.turf.convertLength
import io.github.elcolto.geokjson.turf.radians
import io.github.elcolto.geokjson.turf.radiansToLength
import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Calculates the distance between two positions.
 * This uses the Haversine formula to account for global curvature.
 *
 * @param from origin point
 * @param to destination point
 * @param units units of returned distance
 * @return distance between the two points in [units]
 *
 * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
 */
@JvmOverloads
@ExperimentalTurfApi
@Suppress("MagicNumber")
public fun distance(from: Position, to: Position, units: Units = Units.Kilometers): Double {
    val dLat = radians(to.latitude - from.latitude)
    val dLon = radians(to.longitude - from.longitude)
    val lat1 = radians(from.latitude)
    val lat2 = radians(to.latitude)

    val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)
    return radiansToLength(2 * atan2(sqrt(a), sqrt(1 - a)), units)
}

/**
 * Calculates the distance along a rhumb line between two points in degrees, radians, miles, or kilometers.
 *
 * @param from origin point
 * @param to destination point
 * @param units units of returned distance. Can be degrees, radians, miles, or kilometers (default "kilometers")
 * @return distance between the two points in [units]
 *
 */
@JvmOverloads
@ExperimentalTurfApi
public fun rhumbDistance(from: Position, to: Position, units: Units = Units.Kilometers): Double {
    val destination = Position(compensateAntiMeridianLongitude(from, to), to.latitude)
    val distanceInMeters = calculateRhumbDistance(from, destination)
    return convertLength(distanceInMeters, Units.Meters, units)
}

@Suppress("MagicNumber")
private fun calculateRhumbDistance(origin: Position, destination: Position, radius: Double = EARTH_RADIUS): Double {
    val phi1 = (origin.latitude * PI) / 180
    val phi2 = (destination.latitude * PI) / 180
    val deltaPhi = phi2 - phi1
    var deltaLambda = (abs(destination.longitude - origin.longitude) * PI) / 180
    // if dLon over 180° take shorter rhumb line across the anti-meridian:
    if (deltaLambda > PI) {
        deltaLambda -= 2 * PI
    }

    // on Mercator projection, longitude distances shrink by latitude; q is the 'stretch factor'
    // q becomes ill-conditioned along E-W line (0/0); use empirical tolerance to avoid it
    val deltaPsi = ln(tan(phi2 / 2 + PI / 4) / tan(phi1 / 2 + PI / 4))
    val q = if (abs(deltaPsi) > 10e-12) deltaPhi / deltaPsi else cos(phi1)

    // distance is pythagoras on 'stretched' Mercator projection
    val delta = sqrt(deltaPhi * deltaPhi + q * q * deltaLambda * deltaLambda) // angular distance in radians
    return delta * radius
}
