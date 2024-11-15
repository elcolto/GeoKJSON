package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class PolygonTangentsTest {

    @Test
    fun testPolygonTangentsConcave() {
        runTest(
            "measurement/polygontangents/in/concave.geojson",
            "measurement/polygontangents/out/concave.geojson",
        )
    }

    @Test
    fun testPolygonTangentsHigh() {
        runTest(
            "measurement/polygontangents/in/high.geojson",
            "measurement/polygontangents/out/high.geojson",
        )
    }

    @Test
    fun testPolygonTangentsTurfJsIssue785() {
        runTest(
            "measurement/polygontangents/in/issue785.geojson",
            "measurement/polygontangents/out/issue785.geojson",
        )
    }

    @Test
    fun testPolygonTangentsTurfJsIssue1032() {
        runTest(
            "measurement/polygontangents/in/issue1032.geojson",
            "measurement/polygontangents/out/issue1032.geojson",
        )
    }

    @Test
    fun testPolygonTangentsTurfJsIssue1050() {
        runTest(
            "measurement/polygontangents/in/issue1050.geojson",
            "measurement/polygontangents/out/issue1050.geojson",
        )
    }

    @Test
    fun testPolygonTangentsMultipolygon() {
        runTest(
            "measurement/polygontangents/in/multipolygon.geojson",
            "measurement/polygontangents/out/multipolygon.geojson",
        )
    }

    @Test
    fun testPolygonTangentsPolygonWithHole() {
        runTest(
            "measurement/polygontangents/in/polygonWithHole.geojson",
            "measurement/polygontangents/out/polygonWithHole.geojson",
        )
    }

    @Test
    fun testPolygonTangentsSquare() {
        runTest(
            "measurement/polygontangents/in/square.geojson",
            "measurement/polygontangents/out/square.geojson",
        )
    }

    private fun runTest(inputPath: String, expectedPath: String) {
        val (polygon, point) = FeatureCollection.fromJson(readResource(inputPath)).features.mapNotNull { it.geometry }
        val (first, second) = FeatureCollection.fromJson(readResource(expectedPath)).features.mapNotNull { it.geometry }
            .filterIsInstance<Point>()

        val (firstTan, secondTan) = polygonTangents((point as Point).coordinates, polygon)

        assertEquals(first, firstTan)
        assertEquals(second, secondTan)
    }
}
