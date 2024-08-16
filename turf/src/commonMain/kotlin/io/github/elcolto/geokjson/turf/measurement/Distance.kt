package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.radians
import io.github.elcolto.geokjson.turf.radiansToLength
import kotlin.jvm.JvmOverloads
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
public fun distance(from: Position, to: Position, units: Units = Units.Kilometers): Double {
    val dLat = radians(to.latitude - from.latitude)
    val dLon = radians(to.longitude - from.longitude)
    val lat1 = radians(from.latitude)
    val lat2 = radians(to.latitude)

    val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)
    return radiansToLength(2 * atan2(sqrt(a), sqrt(1 - a)), units)
}
