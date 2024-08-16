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

    val (startPos, endPos) =
        if (startVertex.index <= stopVertex.index) startVertex to stopVertex else stopVertex to startVertex

    val positions = mutableListOf(startPos.point)
    for (i in startPos.index + 1 until endPos.index + 1) {
        positions.add(line.coordinates[i])
    }
    positions.add(endPos.point)

    return LineString(positions)
}
