package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class AlongTest {

    @Test
    fun testAlong() {
        val geometry = LineString.fromJson(readResource("measurement/along/lineString.json"))

        assertEquals(Position(-79.4179672644524, 43.636029126566484), along(geometry, 1.0))
        assertEquals(Position(-79.39973865844715, 43.63797943080659), along(geometry, 2.5))
        assertEquals(Position(-79.37493324279785, 43.64470906117713), along(geometry, 100.0))
        assertEquals(geometry.coordinates.last(), along(geometry, 100.0))
    }
}
