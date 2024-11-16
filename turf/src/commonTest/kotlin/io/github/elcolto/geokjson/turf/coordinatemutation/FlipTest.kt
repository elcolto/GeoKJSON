package io.github.elcolto.geokjson.turf.coordinatemutation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class FlipTest {

    @Test
    fun testFeatureCollection() {
        val inputFc = FeatureCollection.fromJson(
            readResource("coordinatemutation/flip/in/feature-collection-points.geojson"),
        )
        val expectedFc = FeatureCollection.fromJson(
            readResource("coordinatemutation/flip/out/feature-collection-points.geojson"),
        )
        val actualFc = flip(inputFc)
        assertEquals(expectedFc, actualFc)
    }

    @Test
    fun testFlipLineString() {
        val input = Feature.fromJson<Geometry>(readResource("coordinatemutation/flip/in/linestring.geojson"))
        val expected = Feature.fromJson<Geometry>(readResource("coordinatemutation/flip/out/linestring.geojson"))
        val actual = flip(input.getGeometry())
        assertEquals(expected.getGeometry(), actual)
    }

    @Test
    fun testFlipPointWithElevation() {
        val input = Feature.fromJson<Geometry>(readResource("coordinatemutation/flip/in/point-with-elevation.geojson"))
        val expected = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/flip/out/point-with-elevation.geojson"),
        )
        val actual = flip(input.getGeometry())
        assertEquals(expected.getGeometry(), actual)
    }

    @Test
    fun testFlipPolygon() {
        val input = Feature.fromJson<Geometry>(readResource("coordinatemutation/flip/in/polygon.geojson"))
        val expected = Feature.fromJson<Geometry>(readResource("coordinatemutation/flip/out/polygon.geojson"))
        val actual = flip(input.getGeometry())
        assertEquals(expected.getGeometry(), actual)
    }
}
