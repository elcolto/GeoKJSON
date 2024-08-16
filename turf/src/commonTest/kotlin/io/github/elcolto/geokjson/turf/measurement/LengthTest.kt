package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class LengthTest {
    @Test
    fun testLength() {
        val geometry = LineString.fromJson(readResource("measurement/length/lineString.json"))

        assertEquals(42.560767589197006, length(geometry, Units.Kilometers))
    }
}
