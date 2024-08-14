package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test

@ExperimentalTurfApi
class CenterTest {

    @Test
    fun testCenterFromFeature() {
        val geometry = Polygon.fromJson(readResource("measurement/area/other.json"))

        val centerPoint = center(Feature(geometry))

        assertDoubleEquals(-75.71805238723755, centerPoint.coordinates.longitude, 0.0001)
        assertDoubleEquals(45.3811030151199, centerPoint.coordinates.latitude, 0.0001)
    }

    @Test
    fun testCenterFromGeometry() {
        val geometry = Polygon.fromJson(readResource("measurement/area/other.json"))

        val centerPoint = center(geometry)

        assertDoubleEquals(-75.71805238723755, centerPoint.coordinates.longitude, 0.0001)
        assertDoubleEquals(45.3811030151199, centerPoint.coordinates.latitude, 0.0001)
    }
}
