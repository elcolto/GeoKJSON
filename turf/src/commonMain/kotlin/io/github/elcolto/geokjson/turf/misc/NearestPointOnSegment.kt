@file:Suppress("MagicNumber")

package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.degrees
import io.github.elcolto.geokjson.turf.measurement.distance
import io.github.elcolto.geokjson.turf.radians
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private typealias Vector = DoubleArray

private fun dot(v1: Vector, v2: Vector): Double {
    require(v1.size == 3 && v2.size == 3) { "Vectors must have 3 components" }
    return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2]
}

private fun cross(v1: Vector, v2: Vector): Vector {
    require(v1.size == 3 && v2.size == 3) { "Vectors must have 3 components" }
    return doubleArrayOf(
        v1[1] * v2[2] - v1[2] * v2[1],
        v1[2] * v2[0] - v1[0] * v2[2],
        v1[0] * v2[1] - v1[1] * v2[0],
    )
}

private fun magnitude(v: Vector): Double {
    return sqrt(v[0].pow(2) + v[1].pow(2) + v[2].pow(2))
}

private fun angle(v1: Vector, v2: Vector): Double {
    val theta = dot(v1, v2) / (magnitude(v1) * magnitude(v2))
    return acos(max(-1.0, min(theta, 1.0)))
}

private fun lngLatToVector(a: Position): Vector {
    val lat = radians(a.latitude)
    val lng = radians(a.longitude)
    return doubleArrayOf(
        cos(lat) * cos(lng),
        cos(lat) * sin(lng),
        sin(lat),
    )
}

private fun vectorToLngLat(v: Vector): Position {
    require(v.size == 3) { "Vector must have 3 components" }
    val lat = degrees(asin(v[2]))
    val lng = degrees(atan2(v[1], v[0]))
    return Position(lng, lat)
}

@ExperimentalTurfApi
internal fun nearestPointOnSegment(
    start: Position,
    end: Position,
    point: Position,
): Triple<Position, Boolean, Boolean> {
    // Based heavily on this article on finding cross track distance to an arc:
    // https://gis.stackexchange.com/questions/209540/projecting-cross-track-distance-on-great-circle

    // Convert spherical (lng, lat) to cartesian vector coords (x, y, z)
    // In the below https://tikz.net/spherical_1/ we convert lng (𝜙) and lat (𝜃)
    // into vectors with x, y, and z components with a length (r) of 1.
    val a = lngLatToVector(start) // the vector from 0,0,0 to posA
    val b = lngLatToVector(end) // ... to posB
    val c = lngLatToVector(point) // ... to posC

    // Components of target point.
    val (cx, cy, cz) = c

    // Calculate coefficients.
    val (d, e, f) = cross(a, b)
    val g = e * cz - f * cy
    val h = f * cx - d * cz
    val i = d * cy - e * cx

    val j = i * e - h * f
    val k = g * f - i * d
    val l = h * d - g * e

    val t = 1 / sqrt(j.pow(2) + k.pow(2) + l.pow(2))

    // Vectors to the two points these great circles intersect.
    val i1 = doubleArrayOf(j * t, k * t, l * t)
    val i2 = doubleArrayOf(-1 * j * t, -1 * k * t, -1 * l * t)

    // Figure out which is the closest intersection to this segment of the great
    // circle.
    val angleAB = angle(a, b)
    val angleAI1 = angle(a, i1)
    val angleBI1 = angle(b, i1)
    val angleAI2 = angle(a, i2)
    val angleBI2 = angle(b, i2)

    @Suppress("ComplexCondition")
    val intersection =
        if ((angleAI1 < angleAI2 && angleAI1 < angleBI2) || (angleBI1 < angleAI2 && angleBI1 < angleBI2)) {
            i1
        } else {
            i2
        }

    // intersection is the closest intersection to the segment, though might not actually be
    // ON the segment.

    // If angle AI or BI is greater than angleAB, intersection lies on the circle *beyond* A
    // and B so use the closest of A or B as the intersection
    return if (angle(a, intersection) > angleAB || angle(b, intersection) > angleAB) {
        if (distance(vectorToLngLat(intersection), vectorToLngLat(a)) <= distance(
                vectorToLngLat(intersection),
                vectorToLngLat(b),
            )
        ) {
            Triple(vectorToLngLat(a), true, false)
        } else {
            Triple(vectorToLngLat(b), false, true)
        }
    } else {
        // As angleAI nor angleBI don't exceed angleAB, intersection is on the segment
        Triple(vectorToLngLat(intersection), false, false)
    }
}
