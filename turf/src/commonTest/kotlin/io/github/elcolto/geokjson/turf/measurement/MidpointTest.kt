package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class MidpointTest {

    @Test
    fun testMidpoint() {
        val point1 = Position(-79.3801, 43.6463)
        val point2 = Position(-74.0071, 40.7113)

        val midpoint = midpoint(point1, point2)

        assertEquals(-76.6311, midpoint.longitude, 0.0001)
        assertEquals(42.2101, midpoint.latitude, 0.0001)
    }
}
