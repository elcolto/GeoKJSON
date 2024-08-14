package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import kotlin.test.Test

@ExperimentalTurfApi
class MidpointTest {

    @Test
    fun testMidpoint() {
        val point1 = Position(-79.3801, 43.6463)
        val point2 = Position(-74.0071, 40.7113)

        val midpoint = midpoint(point1, point2)

        assertDoubleEquals(-76.6311, midpoint.longitude, 0.0001)
        assertDoubleEquals(42.2101, midpoint.latitude, 0.0001)
    }
}
