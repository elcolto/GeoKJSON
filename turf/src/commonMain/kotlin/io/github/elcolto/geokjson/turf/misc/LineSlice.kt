package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi

/**
 * Takes a [LineString], a start and a stop [Position] and returns a subsection of the line
 * between those points. The start and stop points do not need to fall exactly on the line.
 *
 * @param start Start position
 * @param stop Stop position
 * @param line The line string to slice
 * @return The sliced subsection of the line
 */
@ExperimentalTurfApi
public fun lineSlice(start: Position, stop: Position, line: LineString): LineString {
    val startVertex = nearestPointOnLine(line, start)
    val stopVertex = nearestPointOnLine(line, stop)

    val startIndex = startVertex.nearestPointIndex
    val endIndex = stopVertex.nearestPointIndex

    var reverse = false
    val (startPos, endPos) =
        if (startIndex <= endIndex) {
            startVertex.geometry!! to stopVertex.geometry!!
        } else {
            reverse = true
            stopVertex.geometry!! to startVertex.geometry!!
        }

    val positions = mutableListOf(startPos)
    for (i in (if (reverse) endIndex else startIndex) + 1 until (if (reverse) startIndex else endIndex) + 1) {
        positions.add(line.points[i])
    }
    positions.add(endPos)

    return LineString(positions.map { it.coordinates })
}
