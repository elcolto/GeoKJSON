package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.asInstance
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

        val feature = nearestPointOnLine(
            multiLine.geometry.asInstance<MultiLineString>()!!,
            point.geometry.asInstance<Point>()!!.coordinates,
        )
        assertDoubleEquals(123.924613, feature.geometry.asInstance<Point>()?.coordinates?.longitude, 0.00001)
        assertDoubleEquals(-19.025117, feature.geometry.asInstance<Point>()?.coordinates?.latitude, 0.00001)
        assertDoubleEquals(120.886021, feature.nearestPointDistance, 0.00001)
        assertDoubleEquals(214.548785, feature.nearestPointLocation, 0.00001)
        assertEquals(0, feature.nearestPointIndex)
    }
}
