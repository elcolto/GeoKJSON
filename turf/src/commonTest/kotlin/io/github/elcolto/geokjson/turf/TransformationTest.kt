package io.github.elcolto.geokjson.turf

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.turf.utils.readResource
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
}
