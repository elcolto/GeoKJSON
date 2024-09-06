package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.asInstance
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
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

    @Test
    fun testRhumbDestination() {
        mapOf(
            "measurement/rhumbdestination/in/fiji-east-west.geojson" to Position(-180.43794519555667, -16.5),
            "measurement/rhumbdestination/in/fiji-east-west-539-lng.geojson" to Position(-540.4379451955566, -16.5),
            "measurement/rhumbdestination/in/fiji-west-east.geojson" to Position(
                180.72058412338447,
                -17.174490272793403,
            ),
            "measurement/rhumbdestination/in/point-0.geojson" to Position(-75.0, 39.00028098645979),
            "measurement/rhumbdestination/in/point-90.geojson" to Position(-73.84279091917494, 39.0),
            "measurement/rhumbdestination/in/point-180.geojson" to Position(-75.0, 38.10067963627546),
            "measurement/rhumbdestination/in/point-way-far-away.geojson" to Position(18.117374548567227, 39.0),
        ).forEach { (path, expectedPosition) ->
            val feature = Feature.fromJson<Point>(readResource(path))
            val bearing = feature.properties["bearing"].asInstance<Int>()?.toDouble() ?: 180.0
            val distance = feature.properties["dist"].asInstance<Int>()?.toDouble() ?: 100.0
            val units = feature.properties["units"].asInstance<String>()?.let { Units.valueOf(it.capitalize()) }
                ?: Units.Kilometers
            val origin = feature.getGeometry().coordinates

            val destination = rhumbDestination(origin, distance, bearing, units)

            assertDoubleEquals(
                expectedPosition.longitude,
                destination.longitude,
                epsilon = 0.0000001,
                "longitude failed on path $path",
            )
            assertDoubleEquals(
                expectedPosition.latitude,
                destination.latitude,
                epsilon = 0.0000001,
                "latitude failed on path $path",
            )
        }
    }
}
