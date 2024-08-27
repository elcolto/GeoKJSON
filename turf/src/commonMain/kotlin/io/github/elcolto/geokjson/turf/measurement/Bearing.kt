package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.degrees
import io.github.elcolto.geokjson.turf.radians
import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan

/**
 * Takes two positions ([start], [end]) and finds the geographic bearing between them,
 * i.e. the angle measured in degrees from the north line (0 degrees)
 *
 * @param start starting point
 * @param end ending point
 * @param final calculates the final bearing if true
 * @return bearing in decimal degrees, between -180 and 180 degrees (positive clockwise)
 */
@JvmOverloads
@ExperimentalTurfApi
public fun bearing(start: Position, end: Position, final: Boolean = false): Double {
    if (final) return finalBearing(start, end)

    val lon1 = radians(start.longitude)
    val lon2 = radians(end.longitude)
    val lat1 = radians(start.latitude)
    val lat2 = radians(end.latitude)

    val a = sin(lon2 - lon1) * cos(lat2)
    val b = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)

    return degrees(atan2(a, b))
}

/**
 * Takes two positions and finds the bearing angle between them along a Rhumb line i.e. the angle measured in degrees
 * start the north line (0 degrees)
 *
 * @param isFinal calculates the final bearing if true (default false)
 *
 * @return bearing from north in decimal degrees, between -180 and 180 degrees (positive clockwise)
 */
public fun rhumbBearing(start: Position, end: Position, isFinal: Boolean = false): Double {
    val bearing360 = if (isFinal) calculateRhumbBearing(end, start) else calculateRhumbBearing(start, end)
    return if (bearing360 > 180) -1 * (360 - bearing360) else bearing360
}

private fun calculateRhumbBearing(start: Position, end: Position): Double {
    val phi1 = radians(start.latitude)
    val phi2 = radians(end.latitude)

    var deltaLambda = radians(end.longitude - start.longitude)

    if (deltaLambda > PI) {
        deltaLambda -= 2 * PI
    }
    if (deltaLambda < -PI) {
        deltaLambda += 2 * PI
    }

    @Suppress("MagicNumber")
    val deltaPsi = ln(
        x = tan(phi2 / 2 + PI / 4) / tan(phi1 / 2 + PI / 4),
    )

    val theta = atan2(deltaLambda, deltaPsi)

    return (degrees(theta) + 360) % 360
}

@ExperimentalTurfApi
internal fun finalBearing(start: Position, end: Position): Double = (bearing(end, start) + 180) % 360
