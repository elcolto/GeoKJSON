package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class LineIntersectTest {

    @Test
    fun testLineIntersect() {
        val features = FeatureCollection.fromJson(readResource("misc/lineIntersect/twoPoints.json"))
        val intersect =
            lineIntersect(features.features[0].geometry as LineString, features.features[1].geometry as LineString)

        assertEquals(Position(-120.93653884065287, 51.287945374086675), intersect[0])
    }
}
