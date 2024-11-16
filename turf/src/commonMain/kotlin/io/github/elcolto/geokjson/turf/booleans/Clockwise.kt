package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi

/**
 * Takes a ring and returns true or false whether the ring is clockwise or counter-clockwise.
 *
 * @param line to be evaluated
 * @return true if clockwise, false if counter-clockwise
 */
@ExperimentalTurfApi
public fun clockwise(line: LineString): Boolean {
    val ring = line.coordinates
    return ring.zipWithNext { cur, next -> (next.longitude - cur.longitude) * (next.latitude + cur.latitude) }
        .sum()
        .plus(
            (ring.last().longitude - ring.first().longitude) * (ring.last().latitude + ring.first().latitude),
        ) > 0
}
