package io.github.elcolto.geokjson.turf.grids

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.convertLength
import kotlin.math.abs
import kotlin.math.floor

/**
 * Creates a square grid within a [BoundingBox].
 *
 * @param bbox [BoundingBox] bbox extent
 * @param cellWidth of each cell, in units
 * @param cellHeight of each cell, in units
 * @param units The unit of measurement of the cellSide length
 * @return a [FeatureCollection] grid of polygons
 */
@ExperimentalTurfApi
public fun squareGrid(
    bbox: BoundingBox,
    cellWidth: Double,
    cellHeight: Double,
    units: Units = Units.Kilometers,
): FeatureCollection {
    val west = bbox.southwest.longitude
    val south = bbox.southwest.latitude
    val east = bbox.northeast.longitude
    val north = bbox.northeast.latitude

    val bboxWidth = east - west
    val cellWidthDeg = convertLength(cellWidth, units, Units.Degrees)

    val bboxHeight = north - south
    val cellHeightDeg = convertLength(cellHeight, units, Units.Degrees)

    val columns = floor(abs(bboxWidth) / cellWidthDeg).toInt()
    val rows = floor(abs(bboxHeight) / cellHeightDeg).toInt()

    val deltaX = (bboxWidth - columns * cellWidthDeg) / 2
    val deltaY = (bboxHeight - rows * cellHeightDeg) / 2

    return FeatureCollection(
        (0 until columns).flatMap { col ->
            (0 until rows).map { row ->
                val x = west + deltaX + col * cellWidthDeg
                val y = south + deltaY + row * cellHeightDeg
                Feature(
                    Polygon(
                        listOf(
                            listOf(
                                Position(x, y),
                                Position(x, y + cellHeightDeg),
                                Position(x + cellWidthDeg, y + cellHeightDeg),
                                Position(x + cellWidthDeg, y),
                                Position(x, y),
                            ),
                        ),
                    ),
                )
            }
        },
    )
}
