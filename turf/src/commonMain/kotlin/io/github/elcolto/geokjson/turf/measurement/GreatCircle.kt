package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ANTIMERIDIAN_NEG
import io.github.elcolto.geokjson.turf.ANTIMERIDIAN_POS
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.degrees
import io.github.elcolto.geokjson.turf.radians
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculate great circles routes as [LineString]. Raises error when [start] and [end] are antipodes.
 *
 * @param start source position
 * @param end destination position
 * @param pointCount number of positions on the arc (including [start] and [end])
 * @param antimeridianOffset from antimeridian in degrees (default long. = +/- 10deg, geometries within 170deg to
 * -170deg will be split)
 *
 */
@Suppress("CyclomaticComplexMethod")
@Throws(IllegalArgumentException::class)
@ExperimentalTurfApi
public fun greatCircle(
    start: Position,
    end: Position,
    pointCount: Int = 100,
    antimeridianOffset: Double = 10.0,
): Geometry {
    val deltaLongitude = start.longitude - end.longitude
    val deltaLatitude = start.latitude - end.latitude

    // check antipodal positions
    require(abs(deltaLatitude) != 0.0 && abs(deltaLongitude % 360) - ANTIMERIDIAN_POS != 0.0) {
        "Input $start and $end are diametrically opposite, thus there is no single route but rather infinite"
    }

    val distance = distance(start, end, Units.Radians)

    /**
     * Calculates the intermediate point on a great circle line
     *         http://www.edwilliams.org/avform.htm#Intermediate
     */
    fun intermediateCoordinate(fraction: Double): Position {
        val lon1 = radians(start.longitude)
        val lon2 = radians(end.longitude)
        val lat1 = radians(start.latitude)
        val lat2 = radians(end.latitude)

        val a = sin((1 - fraction) * distance) / sin(distance)
        val b = sin(fraction * distance) / sin(distance)
        val x = a * cos(lat1) * cos(lon1) + b * cos(lat2) * cos(lon2)
        val y = a * cos(lat1) * sin(lon1) + b * cos(lat2) * sin(lon2)
        val z = a * sin(lat1) + b * sin(lat2)

        val lat = degrees(atan2(z, sqrt(x.pow(2) + y.pow(2))))
        val lon = degrees(atan2(y, x))
        return Position(lon, lat)
    }

    @Suppress("LongMethod")
    fun createCoordinatesAntimeridianAttended(
        plainArc: List<Position>,
        antimeridianOffset: Double,
    ): List<List<Position>> {
        val borderEast = ANTIMERIDIAN_POS - antimeridianOffset
        val borderWest = ANTIMERIDIAN_NEG + antimeridianOffset

        val diffSpace = 360.0 - antimeridianOffset

        val passesAntimeridian = plainArc.zipWithNext { a, b ->
            val diff = abs(a.longitude - b.longitude)
            (
                diff > diffSpace &&
                    (
                        (a.longitude > borderEast && b.longitude < borderWest) ||
                            (b.longitude > borderEast && a.longitude < borderWest)
                        )
                )
        }.any()

        val maxSmallDiffLong = plainArc.zipWithNext { a, b -> abs(a.longitude - b.longitude) }
            .filter { it <= diffSpace } // Filter differences less than or equal to diffSpace
            .maxByOrNull { it }?.toDouble() ?: 0.0

        val poMulti = mutableListOf<List<Position>>()
        if (passesAntimeridian && maxSmallDiffLong < antimeridianOffset) {
            var poNewLS = mutableListOf<Position>()
            plainArc.forEachIndexed { k, currentPosition ->
                if (k > 0 && abs(currentPosition.longitude - plainArc[k - 1].longitude) > diffSpace) {
                    val previousPosition = plainArc[k - 1]
                    var lon1 = previousPosition.longitude
                    var lat1 = previousPosition.latitude
                    var lon2 = currentPosition.longitude
                    var lat2 = currentPosition.latitude

                    @Suppress("ComplexCondition")
                    if (lon1 in (ANTIMERIDIAN_NEG + 1..<borderWest) &&
                        lon2 == ANTIMERIDIAN_POS &&
                        k + 1 < plainArc.size
                    ) {
                        poNewLS.add(Position(ANTIMERIDIAN_NEG, currentPosition.latitude))
                        poNewLS.add(Position(plainArc[k + 1].longitude, plainArc[k + 1].latitude))
                        return@forEachIndexed
                    } else if (
                        lon1 > borderEast &&
                        lon1 < ANTIMERIDIAN_POS &&
                        lon2 == ANTIMERIDIAN_POS &&
                        k + 1 < plainArc.size
                    ) {
                        poNewLS.add(Position(ANTIMERIDIAN_POS, currentPosition.latitude))
                        poNewLS.add(Position(plainArc[k + 1].longitude, plainArc[k + 1].latitude))
                        return@forEachIndexed
                    }

                    if (lon1 < borderWest && lon2 > borderEast) {
                        val tmpX = lon1
                        lon1 = lon2
                        lon2 = tmpX
                        val tmpY = lat1
                        lat1 = lat2
                        lat2 = tmpY
                    }
                    if (lon1 > borderEast && lon2 < borderWest) {
                        lon2 += 360.0
                    }

                    if (ANTIMERIDIAN_POS in lon1..lon2 && lon1 < lon2) {
                        val ratio = (ANTIMERIDIAN_POS - lon1) / (lon2 - lon1)
                        val lat = ratio * lat2 + (1 - ratio) * lat1
                        poNewLS.add(
                            if (previousPosition.longitude > borderEast) {
                                Position(ANTIMERIDIAN_POS, lat)
                            } else {
                                Position(ANTIMERIDIAN_NEG, lat)
                            },
                        )
                        poMulti.add(poNewLS.toList())
                        poNewLS = mutableListOf() // Clear poNewLS instead of replacing it with an empty list
                        poNewLS.add(
                            if (previousPosition.longitude > borderEast) {
                                Position(ANTIMERIDIAN_NEG, lat)
                            } else {
                                Position(ANTIMERIDIAN_POS, lat)
                            },
                        )
                    } else {
                        poNewLS = mutableListOf() // Clear poNewLS instead of replacing it with an empty list
                        poMulti.add(poNewLS.toList())
                    }
                }
                poNewLS.add(currentPosition) // Adding current position to poNewLS
            }
            poMulti.add(poNewLS.toList()) // Adding the last remaining poNewLS to poMulti
        } else {
            poMulti.add(plainArc)
        }
        return poMulti
    }

    val arc = buildList {
        add(start)
        for (i in 1..<pointCount - 1)
            add(intermediateCoordinate((i + 1).toDouble() / (pointCount - 2 + 1)))
        add(end)
    }

    val coordinates = createCoordinatesAntimeridianAttended(arc, antimeridianOffset)
    return if (coordinates.size == 1) {
        LineString(
            coordinates = coordinates[0],
            bbox = computeBbox(coordinates[0]),
        )
    } else {
        MultiLineString(
            coordinates = coordinates,
            bbox = computeBbox(coordinates.flatten()),
        )
    }
}
