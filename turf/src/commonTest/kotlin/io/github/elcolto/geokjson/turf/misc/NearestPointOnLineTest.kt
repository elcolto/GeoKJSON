package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class NearestPointOnLineTest {

    @Test
    fun testNearestPointOnLine() {
        val (multiLine, point) =
            FeatureCollection.fromJson(readResource("misc/nearestPointOnLine/multiLine.json")).features

        val result = nearestPointOnLine(multiLine.geometry as MultiLineString, (point.geometry as Point).coordinates)
        assertDoubleEquals(123.924613, result.point.longitude, 0.00001)
        assertDoubleEquals(-19.025117, result.point.latitude, 0.00001)
        assertDoubleEquals(120.886021, result.distance, 0.00001)
        assertDoubleEquals(214.548785, result.location, 0.00001)
        assertEquals(0, result.index)
    }
}
