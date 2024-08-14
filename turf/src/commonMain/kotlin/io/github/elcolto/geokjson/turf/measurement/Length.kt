package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units

/**
 * Calculates the length of the given [LineString] in the given [Units].
 *
 * @param lineString The geometry to measure
 * @param units The unit of measurement to return the length in
 * @return The length of the geometry in [units].
 */
@ExperimentalTurfApi
public fun length(lineString: LineString, units: Units): Double = length(lineString.coordinates, units)

/**
 * Calculates the combined length of all [LineString]s from the given [MultiLineString] in the given [Units].
 *
 * @param multiLineString The geometry to measure
 * @param units The unit of measurement to return the length in
 * @return The length of the geometry in [units].
 */
@ExperimentalTurfApi
public fun length(multiLineString: MultiLineString, units: Units): Double =
    multiLineString.coordinates.fold(0.0) { acc, coords -> acc + length(coords, units) }

/**
 * Calculates the length of perimeter the given [Polygon] in the given [Units].
 * Any holes in the polygon will be included in the length.
 *
 * @param polygon The geometry to measure
 * @param units The unit of measurement to return the length in
 * @return The length of the geometry in [units].
 */
@ExperimentalTurfApi
public fun length(polygon: Polygon, units: Units): Double =
    polygon.coordinates.fold(0.0) { acc, ring -> acc + length(ring, units) }

/**
 * Calculates the combined length of perimeter the [Polygon]s in the [MultiPolygon] in the given [Units].
 * Any holes in the polygons will be included in the length.
 *
 * @param multiPolygon The geometry to measure
 * @param units The unit of measurement to return the length in
 * @return The length of the geometry in [units].
 */
@ExperimentalTurfApi
public fun length(multiPolygon: MultiPolygon, units: Units): Double =
    multiPolygon.coordinates.fold(0.0) { total, polygon ->
        total + polygon.fold(0.0) { acc, ring ->
            acc + length(
                ring,
                units,
            )
        }
    }

@ExperimentalTurfApi
private fun length(coords: List<Position>, units: Units): Double = coords
    .zipWithNext()
    .sumOf { (prev, next) -> distance(prev, next, units) }
