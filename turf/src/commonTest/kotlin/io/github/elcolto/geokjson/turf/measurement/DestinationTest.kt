package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import kotlin.test.Test

@ExperimentalTurfApi
class DestinationTest {
    @Test
    fun testDestination() {
        val point0 = Position(-75.0, 38.10096062273525)
        val (longitude, latitude) = destination(point0, 100.0, 0.0)

        assertDoubleEquals(-75.0, longitude, 0.1)
        assertDoubleEquals(39.000281, latitude, 0.000001)
    }
}
