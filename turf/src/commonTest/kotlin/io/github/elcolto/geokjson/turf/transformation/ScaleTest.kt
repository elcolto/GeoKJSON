package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.asInstance
import io.github.elcolto.geokjson.turf.utils.assertGeometryEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test

@ExperimentalTurfApi
class ScaleTest {

    @Test
    fun testScaleIssue1059() {
        testScale("transformation/scale/in/issue-#1059.geojson", "transformation/scale/out/issue-#1059.geojson")
    }

    @Test
    fun testScaleLine() {
        testScale("transformation/scale/in/line.geojson", "transformation/scale/out/line.geojson")
    }

    @Test
    fun testScaleMultiLine() {
        testScale("transformation/scale/in/multiLine.geojson", "transformation/scale/out/multiLine.geojson")
    }

    @Test
    fun testScaleMultiPoint() {
        testScale("transformation/scale/in/multiPoint.geojson", "transformation/scale/out/multiPoint.geojson")
    }

    @Test
    fun testScaleMultiPolygon() {
        testScale("transformation/scale/in/multiPolygon.geojson", "transformation/scale/out/multiPolygon.geojson")
    }

    @Test
    fun testScaleNoScale() {
        testScale("transformation/scale/in/no-scale.geojson", "transformation/scale/out/no-scale.geojson")
    }

    @Test
    fun testScaleOriginInsideBbox() {
        testScale(
            "transformation/scale/in/origin-inside-bbox.geojson",
            "transformation/scale/out/origin-inside-bbox.geojson",
        )
    }

    @Test
    fun testScaleOriginInsideFeature() {
        testScale(
            "transformation/scale/in/origin-inside-feature.geojson",
            "transformation/scale/out/origin-inside-feature.geojson",
        )
    }

    @Test
    fun testScaleOriginOutsideBbox() {
        testScale(
            "transformation/scale/in/origin-outside-bbox.geojson",
            "transformation/scale/out/origin-outside-bbox.geojson",
        )
    }

    @Test
    fun testScalePoint() {
        testScale("transformation/scale/in/point.geojson", "transformation/scale/out/point.geojson")
    }

    @Test
    fun testScalePolyDouble() {
        testScale("transformation/scale/in/poly-double.geojson", "transformation/scale/out/poly-double.geojson")
    }

    @Test
    fun testScalePolyHalf() {
        testScale("transformation/scale/in/poly-half.geojson", "transformation/scale/out/poly-half.geojson")
    }

    @Test
    fun testScalePolygon() {
        testScale("transformation/scale/in/polygon.geojson", "transformation/scale/out/polygon.geojson")
    }

    @Test
    fun testScalePolygonFiji() {
        testScale("transformation/scale/in/polygon-fiji.geojson", "transformation/scale/out/polygon-fiji.geojson")
    }

    @Test
    fun testScalePolygonResoluteBay() {
        testScale(
            "transformation/scale/in/polygon-resolute-bay.geojson",
            "transformation/scale/out/polygon-resolute-bay.geojson",
        )
    }

    @Test
    fun testScalePolygonWithHole() {
        testScale(
            "transformation/scale/in/polygon-with-hole.geojson",
            "transformation/scale/out/polygon-with-hole.geojson",
        )
    }

    @Test
    fun testScaleZScaling() {
        testScale("transformation/scale/in/z-scaling.geojson", "transformation/scale/out/z-scaling.geojson")
    }

    private fun testScale(path: String, expectedPath: String) {
        val feature = Feature.fromJson<Geometry>(readResource(path))
        val expectedFc = FeatureCollection.fromJson(readResource(expectedPath))
        val factor = feature.properties["factor"].asInstance<Number>() ?: 2.0
        val originString = feature.properties["origin"].asInstance<String>()
        val originPosition = feature.properties["origin"].asInstance<List<Double>>()
            ?.let { Position(it[0], it[1]) }
        val scaleOrigin = originString?.let { stringToScaledOrigin(it, null) }
            ?: originPosition?.let { stringToScaledOrigin("coordinates", it) }

        val scaledGeometry = scale(feature.geometry!!, factor.toDouble(), scaleOrigin ?: ScaleOrigin.Centroid)

        assertGeometryEquals(expectedFc.features.first().geometry!!, scaledGeometry, 0.000001)
    }

    private fun stringToScaledOrigin(value: String, position: Position?): ScaleOrigin = when (value) {
        "sw", "southwest", "bottomleft" -> ScaleOrigin.SouthWest
        "se", "southeast", "bottomright" -> ScaleOrigin.SouthEast
        "nw", "northwest", "topleft" -> ScaleOrigin.NorthWest
        "ne", "northeast", "topright" -> ScaleOrigin.NorthEast
        "center" -> ScaleOrigin.Center
        "centroid" -> ScaleOrigin.Centroid
        "coordinates" -> ScaleOrigin.Coordinates(position!!)
        else -> error("$value not applicable")
    }
}
