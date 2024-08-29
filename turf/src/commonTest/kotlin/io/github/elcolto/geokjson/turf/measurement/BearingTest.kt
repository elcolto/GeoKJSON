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

    @Test
    fun testRhumbBearing() {
        val start = Position(-75.0, 45.0)
        val end = Position(20.0, 60.0)

        val initialBearing = rhumbBearing(start, end)
        val finalBearing = rhumbBearing(start, end, isFinal = true)

        assertDoubleEquals(75.28061364784332, initialBearing, epsilon = 0.00000001)
        assertDoubleEquals(-104.7193863521567, finalBearing, epsilon = 0.00000001)
    }
}
