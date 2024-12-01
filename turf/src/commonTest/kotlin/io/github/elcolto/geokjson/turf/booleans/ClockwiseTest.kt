package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTurfApi::class)
class ClockwiseTest {

    @Test
    fun testCounterClockwise() {
        val lineString = LineString(
            arrayOf(
                doubleArrayOf(0.0, 0.0),
                doubleArrayOf(1.0, 0.0),
                doubleArrayOf(1.0, 1.0),
                doubleArrayOf(0.0, 0.0),
            ),
        )
        assertFalse(clockwise(lineString))
    }

    @Test
    fun testClockwise() {
        val lineString = LineString(
            arrayOf(
                doubleArrayOf(0.0, 0.0),
                doubleArrayOf(1.0, 1.0),
                doubleArrayOf(1.0, 0.0),
                doubleArrayOf(0.0, 0.0),
            ),
        )
        assertTrue(clockwise(lineString))
    }
}
