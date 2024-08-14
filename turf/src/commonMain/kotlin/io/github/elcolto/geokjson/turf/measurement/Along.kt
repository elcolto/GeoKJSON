package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import kotlin.jvm.JvmOverloads

/**
 * Takes a [LineString] and returns a [position][Position] at a specified distance along the line.
 *
 * @param line input line
 * @param distance distance along the line
 * @param units units of [distance]
 * @return A position [distance] [units] along the line
 */
@JvmOverloads
@ExperimentalTurfApi
public fun along(line: LineString, distance: Double, units: Units = Units.Kilometers): Position {
    var travelled = 0.0

    line.coordinates.forEachIndexed { i, coordinate ->
        when {
            distance >= travelled && i == line.coordinates.size - 1 -> {}

            travelled >= distance -> {
                val overshot = distance - travelled
                return if (overshot == 0.0) {
                    coordinate
                } else {
                    val direction = bearing(coordinate, line.coordinates[i - 1]) - 180
                    destination(coordinate, overshot, direction, units)
                }
            }

            else -> travelled += distance(coordinate, line.coordinates[i + 1], units)
        }
    }
    return line.coordinates.last()
}
