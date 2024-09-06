package io.github.elcolto.geokjson.turf.coordinatemutation

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

class CleanCoordinatesTest {

    @Test
    fun testCleanSegment() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/clean-segment.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/clean-segment.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testClosedLineString() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/closed-linestring.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/closed-linestring.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testGeometry() {
        val polygon = Polygon.fromJson(readResource("coordinatemutation/cleancoordinates/in/geometry.geojson"))
        val geometry = cleanCoordinates(polygon)
        val expectedPolygon = Polygon.fromJson(readResource("coordinatemutation/cleancoordinates/out/geometry.geojson"))

        assertEquals(expectedPolygon, geometry)
    }

    @Test
    fun testLine3Coords() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/line-3-coords.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/line-3-coords.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testMultiLine() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/multiline.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/multiline.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testMultiPoint() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/multipoint.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/multipoint.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testMultiPolygon() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/multipolygon.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/multipolygon.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testPoint() {
        val feature = Feature.fromJson<Geometry>(readResource("coordinatemutation/cleancoordinates/in/point.geojson"))
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/point.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testPolygon() {
        val feature = Feature.fromJson<Geometry>(readResource("coordinatemutation/cleancoordinates/in/polygon.geojson"))
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/polygon.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testPolygonWithHole() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/polygon-with-hole.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/polygon-with-hole.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testSegment() {
        val feature = Feature.fromJson<Geometry>(readResource("coordinatemutation/cleancoordinates/in/segment.geojson"))
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/segment.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testSimpleLine() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/simple-line.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/simple-line.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testTriangle() {
        val feature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/in/triangle.geojson"),
        )
        val geometry = cleanCoordinates(feature.geometry!!)
        val expectedFeature = Feature.fromJson<Geometry>(
            readResource("coordinatemutation/cleancoordinates/out/triangle.geojson"),
        )

        assertEquals(expectedFeature.geometry, geometry)
    }

    @Test
    fun testTriplicateIssue1255() {
        val polygon = Polygon.fromJson(
            readResource("coordinatemutation/cleancoordinates/in/triplicate-issue1255.geojson"),
        )
        val geometry = cleanCoordinates(polygon)
        val expectedPolygon = Polygon.fromJson(
            readResource("coordinatemutation/cleancoordinates/out/triplicate-issue1255.geojson"),
        )

        assertEquals(expectedPolygon, geometry)
    }
}
