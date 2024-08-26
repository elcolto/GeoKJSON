package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi

/**
 * Takes a bounding box and calculates the minimum square bounding box that would contain the input.
 * @return [BoundingBox] a square surrounding [boundingBox]
 */
@ExperimentalTurfApi
public fun square(boundingBox: BoundingBox): BoundingBox {
    val (east, north) = boundingBox.northeast
    val (west, south) = boundingBox.southwest

    val horizontalDistance = distance(Position(west, south), Position(east, south))
    val verticalDistance = distance(Position(west, south), Position(west, north))
    return if (horizontalDistance >= verticalDistance) {
        val verticalMidpoint = (south + north) / 2
        BoundingBox(
            west = west,
            south = verticalMidpoint - (east - west) / 2,
            east = east,
            north = verticalMidpoint + (east - west) / 2,
        )
    } else {
        val horizontalMidpoint = (west + east) / 2
        BoundingBox(
            west = horizontalMidpoint - (north - south) / 2,
            south = south,
            east = horizontalMidpoint + (north - south) / 2,
            north = north,
        )
    }
}
