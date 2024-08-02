package io.github.elcolto.geokjson.turf

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class TransformationTest {

    @Test
    fun testBezierSplineIn() {
        val feature = Feature.fromJson(readResource("transformation/bezierspline/in/bezierIn.json"))
        val expectedOut = Feature.fromJson(readResource("transformation/bezierspline/out/bezierIn.json"))

        assertEquals(expectedOut.geometry, bezierSpline(feature.geometry as LineString))
    }

    @Test
    fun testBezierSplineSimple() {
        val feature = Feature.fromJson(readResource("transformation/bezierspline/in/simple.json"))
        val expectedOut = Feature.fromJson(readResource("transformation/bezierspline/out/simple.json"))

        assertEquals(expectedOut.geometry, bezierSpline(feature.geometry as LineString))
    }

    /**
     * This test is designed to draw a bezierSpline across the 180th Meridian
     *
     * @see <a href="https://github.com/Turfjs/turf/issues/1063">
     */
    @Test
    fun testBezierSplineAcrossPacific() {
        val feature = Feature.fromJson(readResource("transformation/bezierspline/in/issue1063.json"))
        val expectedOut = Feature.fromJson(readResource("transformation/bezierspline/out/issue1063.json"))

        assertEquals(expectedOut.geometry, bezierSpline(feature.geometry as LineString))
    }

    @Test
    fun testCircle() {
        val point = Feature.fromJson(readResource("transformation/circle/in/circle1.json"))
        val expectedOut = FeatureCollection.fromJson(readResource("transformation/circle/out/circle1.json"))

        val (_, expectedCircle) = expectedOut.features

        val circle = circle(
            center = point.geometry as Point,
            radius = point.properties["radius"]?.jsonPrimitive?.double ?: 0.0,
        )

        val circleCoordinates = circle.coordAll()
        expectedCircle.geometry?.coordAll()?.forEachIndexed { index, position ->
            val (expectedLat, expectedLon) = position
            val (lat, lon) = circleCoordinates[index]
            // apple targets precision is less
            assertDoubleEquals(expectedLat, lat, epsilon = 0.0000000000001)
            assertDoubleEquals(expectedLon, lon, epsilon = 0.0000000000001)
        }
        assertEquals(expectedCircle.geometry?.bbox, circle.bbox)
    }
}
