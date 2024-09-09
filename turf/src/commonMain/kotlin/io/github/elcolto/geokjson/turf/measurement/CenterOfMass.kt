package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.dsl.point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.transformation.convex

/**
 * Takes any [Geometry] and returns its [center of mass](https://en.wikipedia.org/wiki/Center_of_mass) using this
 * formula: [Centroid of Polygon](https://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon).
 *
 * @param geometry Geometry to be centered
 * @return the center of mass
 * @example
 * ```kotlin
 * val polygon = polygon(arrayOf(arrayOf(
 *     Position(-81.0, 41.0),
 *     Position(-88.0, 36.0),
 *     Position(-84.0, 31.0),
 *     Position(-80.0, 33.0),
 *     Position(-77.0, 39.0),
 *     Position(-81.0, 41.0)
 * )))
 *
 * val center = centerOfMass(polygon)
 *
 * //addToMap
 * val addToMap = listOf(polygon, center)
 * ```
 */
@ExperimentalTurfApi
internal fun centerOfMass(geometry: Geometry): Point {
    return when (geometry) {
        is Point -> geometry
        is Polygon -> {
            // First, we neutralize the feature (set it around coordinates [0,0]) to prevent rounding errors
            // We take any point to translate all the points around 0
            val center = centroid(geometry)
            val translation = center.coordinates

            // sx and sy are the sums used to compute the final coordinates
            // sArea is the sum used to compute the signed area
            val (sx, sy, sArea) = geometry.coordAll()
                .zipWithNext { currentPoint, nextPoint ->
                    val x1 = currentPoint.longitude - translation.longitude
                    val y1 = currentPoint.latitude - translation.latitude
                    val x2 = nextPoint.longitude - translation.longitude
                    val y2 = nextPoint.latitude - translation.latitude

                    // a is the common factor to compute the signed area and the final coordinates
                    val a = x1 * y2 - x2 * y1
                    Triple((x1 + x2) * a, (y1 + y2) * a, a)
                }
                .fold(Triple(0.0, 0.0, 0.0)) { acc, (sx, sy, a) ->
                    Triple(
                        acc.first + sx,
                        acc.second + sy,
                        acc.third + a,
                    )
                }

            // Shape has no area: fallback on turf.centroid
            if (sArea == 0.0) {
                center
            } else {
                // Compute the signed area, and factorize 1/6A
                val area = sArea * 0.5

                @Suppress("MagicNumber")
                val areaFactor = 1 / (6 * area)

                // Compute the final coordinates, adding back the values that have been neutralized
                point(
                    translation.longitude + areaFactor * sx,
                    translation.latitude + areaFactor * sy,
                )
            }
        }

        else -> {
            // Not a polygon: Compute the convex hull and work with that
            val hull = convex(geometry)

            if (hull != null) {
                centerOfMass(hull)
            } else {
                // Hull is empty: fallback on the centroid
                centroid(geometry)
            }
        }
    }
}
