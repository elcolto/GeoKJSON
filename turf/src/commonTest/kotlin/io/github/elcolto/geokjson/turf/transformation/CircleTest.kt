package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class CircleTest {

    @Test
    fun testCircle() {
        val point = Feature.fromJson<Point>(readResource("transformation/circle/in/circle1.json"))
        val expectedOut = FeatureCollection.fromJson(readResource("transformation/circle/out/circle1.json"))

        val (_, expectedCircle) = expectedOut.features

        val circle = circle(
            center = point.geometry as Point,
            radius = point.properties["radius"] as Double,
        )

        val circleCoordinates = circle.coordAll()
        expectedCircle.geometry?.coordAll()?.forEachIndexed { index, position ->
            val (expectedLat, expectedLon) = position
            val (lat, lon) = circleCoordinates[index]
            // apple targets precision is less
            assertEquals(expectedLat, lat, absoluteTolerance = 0.0000000000001)
            assertEquals(expectedLon, lon, absoluteTolerance = 0.0000000000001)
        }
        assertEquals(expectedCircle.geometry?.bbox, circle.bbox)
    }
}
