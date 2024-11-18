package io.github.elcolto.geokjson.turf.coordinatemutation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class RewindTest {

    @Test
    fun testFeatureCollection() {
        val inputFc = FeatureCollection.fromJson(
            readResource("coordinatemutation/rewind/in/feature-collection.geojson"),
        )
        val expectedFc = FeatureCollection.fromJson(
            readResource("coordinatemutation/rewind/out/feature-collection.geojson"),
        )
        val actualFc = rewind(inputFc)
        assertEquals(expectedFc, actualFc)
    }

    @Test
    fun testLineClockwise() {
        val inputFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/in/line-clockwise.geojson"),
        )
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/out/line-clockwise.geojson"),
        )
        val actual = rewind(inputFeature)
        assertEquals(expectedFeature, actual)
    }

    @Test
    fun testLineCounterClockwise() {
        val inputFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/in/line-counter-clockwise.geojson"),
        )
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/out/line-counter-clockwise.geojson"),
        )
        val actual = rewind(inputFeature)
        assertEquals(expectedFeature, actual)
    }

    @Test
    fun testPolygonClockwise() {
        val inputFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/in/polygon-clockwise.geojson"),
        )
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/out/polygon-clockwise.geojson"),
        )
        val actual = rewind(inputFeature)
        assertEquals(expectedFeature, actual)
    }

    @Test
    fun testPolygonCounterClockwise() {
        val inputFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/in/polygon-counter-clockwise.geojson"),
        )
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/rewind/out/polygon-counter-clockwise.geojson"),
        )
        val actual = rewind(inputFeature)
        assertEquals(expectedFeature, actual)
    }
}
