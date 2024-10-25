package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.asInstance
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class DistanceTest {

    @Test
    fun testDistance() {
        val a = Position(-73.67, 45.48)
        val b = Position(-79.48, 43.68)

        assertEquals(501.64563403765925, distance(a, b))
    }

    @Test
    fun testRhumbDistance() {
        mapOf(
            "measurement/rhumbdistance/in/fiji-539-lng.geojson" to 307.306293,
            "measurement/rhumbdistance/in/points1.geojson" to 97.129239,
            "measurement/rhumbdistance/in/points2.geojson" to 4482.044244,
            "measurement/rhumbdistance/in/points-fiji.geojson" to 213.232075,
        ).forEach { (path, expectedPosition) ->
            val fc = FeatureCollection.fromJson(readResource(path))
            val origin = fc.features.first().geometry.asInstance<Point>()!!
            val destination = fc.features.last().geometry.asInstance<Point>()!!

            val distance = rhumbDistance(origin.coordinates, destination.coordinates)

            assertDoubleEquals(
                expectedPosition,
                distance,
                epsilon = 0.000001,
                "distance failed on path $path",
            )
        }
    }
}
