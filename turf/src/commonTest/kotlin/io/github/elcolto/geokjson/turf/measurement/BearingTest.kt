package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import kotlin.test.Test

@ExperimentalTurfApi
class BearingTest {
    @Test
    fun testBearing() {
        val start = Position(-75.0, 45.0)
        val end = Position(20.0, 60.0)

        assertDoubleEquals(37.75, bearing(start, end), 0.01, "Initial Bearing")
        assertDoubleEquals(120.01, bearing(start, end, final = true), 0.01, "Final Bearing")
    }
}
