package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.calculation.concavehull.ConcaveHull
import io.github.elcolto.geokjson.turf.coordAll

/**
 * Takes a [Geometry] and returns a convex hull [Polygon].
 *
 * Internally this implements a
 * [monotone chain hull](http://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain).
 *
 * @param geometry input Feature or FeatureCollection
 * @param concavity 1 - thin shape. `Integer.MAX_VALUE` - convex hull.
 * @return a convex hull. `null`, when input is empty or a hull can not be calculated
 * @example
 * ```kotlin
 * val points = featureCollection(listOf(
 *   point(Position(10.195312, 43.755225)),
 *   point(Position(10.404052, 43.8424511)),
 *   point(Position(10.579833, 43.659924)),
 *   point(Position(10.360107, 43.516688)),
 *   point(Position(10.14038, 43.588348)),
 *   point(Position(10.195312, 43.755225))
 * ))
 *
 * val hull = convex(points)
 *
 * //addToMap
 * val addToMap = listOf(points, hull)
 * ```
 */
@ExperimentalTurfApi
public fun convex(geometry: Geometry, concavity: Int = Int.MAX_VALUE): Polygon? {
    val positions = geometry.coordAll()
    if (positions.isEmpty()) {
        return null
    }

    val convexHull = ConcaveHull.calculateConcaveHull(
        // drop altitude
        positions.map { Point(it.coordinates.take(2).toDoubleArray()) },
        concavity,
    )

    // Convex hull should have at least 3 different vertices in order to create a valid polygon
    return Polygon(convexHull.map { it.coordinates }).takeIf { convexHull.size > MIN_COORDINATE_SIZE_HULL }
}

private const val MIN_COORDINATE_SIZE_HULL = 3
