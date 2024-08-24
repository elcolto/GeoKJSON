package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.dsl.geometryCollection
import io.github.elcolto.geokjson.geojson.dsl.lineString
import io.github.elcolto.geokjson.geojson.dsl.multiLineString
import io.github.elcolto.geokjson.geojson.dsl.multiPoint
import io.github.elcolto.geokjson.geojson.dsl.multiPolygon
import io.github.elcolto.geokjson.geojson.dsl.point
import io.github.elcolto.geokjson.geojson.dsl.polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTurfApi::class)
class WithinTest {

    @Test
    fun testTrue() {
        listOf(
            "/booleans/within/true/Polygon/Polygon/PolygonIsWIthinPolygon.geojson",
            "/booleans/within/true/Polygon/Polygon/PolygonsExactSameShape.geojson",
            "/booleans/within/true/Polygon/MultiPolygon/polygon-within-multipolygon.geojson",
            "/booleans/within/true/Point/Polygon/PointIsWithinPolygon.geojson",
            "/booleans/within/true/Point/LineString/PointIsWithinLine.geojson",
            "/booleans/within/true/Point/MultiPoint/PointIsWithinMultiPoint.geojson",
            "/booleans/within/true/Point/MultiPolygon/point-within-multipolygon.geojson",
            "/booleans/within/true/MultiLineString/MultiPolygon/skip-multilinestring-within-multipolygon.geojson",
            "/booleans/within/true/LineString/Polygon/LineIsContainedByPolygonWithNoInternalVertices.geojson",
            "/booleans/within/true/LineString/Polygon/LineIsContainedByPolygon.geojson",
            "/booleans/within/true/LineString/LineString/LineIsWithinLine.geojson",
            "/booleans/within/true/LineString/LineString/LinesExactSame.geojson",
            "/booleans/within/true/MultiPoint/Polygon/MultiPointSingleIsWithinPolygon.geojson",
            "/booleans/within/true/MultiPoint/Polygon/MultiPointIsWithinPolygon.geojson",
            "/booleans/within/true/MultiPoint/LineString/MultipointsIsWithinLine.geojson",
            "/booleans/within/true/MultiPoint/MultiPoint/MultiPointsWithinMultiPoints.geojson",
            "/booleans/within/true/MultiPoint/MultiPolygon/multipoint-within-multipolygon.geojson",
            "/booleans/within/true/MultiPolygon/MultiPolygon/skip-multipolygon-within-multipolygon.geojson",
        ).forEach { path ->
            // tests containing skip in their name are testing features that are not implemented yet
            if (path.contains("skip")) {
                return@forEach
            }
            val fc = FeatureCollection.fromJson(readResource(path))
            val geometry1 = fc.features.first().geometry ?: error("geometry not found")
            val geometry2 = fc.last().geometry ?: error("geometry not found")

            assertTrue(
                within(geometry1, geometry2),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testFalse() {
        listOf(
            "/booleans/within/false/Polygon/Polygon/Polygon-Polygon.geojson",
            "/booleans/within/false/Polygon/MultiPolygon/polygon-not-within-multipolygon.geojson",
            "/booleans/within/false/Point/Polygon/PointIsNotWithinPolygon.geojson",
            "/booleans/within/false/Point/Polygon/PointOnPolygonBoundary.geojson",
            "/booleans/within/false/Point/LineString/PointIsNotWithinLine.geojson",
            "/booleans/within/false/Point/LineString/PointIsNotWithinLineBecauseOnEnd.geojson",
            "/booleans/within/false/Point/LineString/PointOnEndIsWithinLinestring.geojson",
            "/booleans/within/false/Point/MultiPoint/PointIsNotWithinMultiPoint.geojson",
            "/booleans/within/false/Point/MultiPolygon/point-not-within-multipolygon.geojson",
            "/booleans/within/false/MultiLineString/MultiPolygon/skip-multilinestring-not-within-multipolygon.geojson",
            "/booleans/within/false/LineString/Polygon/LineIsNotWIthinPolygonBoundary.geojson",
            "/booleans/within/false/LineString/Polygon/LineIsNotWIthinPolygon.geojson",
            "/booleans/within/false/LineString/LineString/LineIsNotWithinLine.geojson",
            "/booleans/within/false/MultiPoint/Polygon/MultiPointAllOnBoundaryIsNotWithinPolygon.geojson",
            "/booleans/within/false/MultiPoint/Polygon/MultiPointIsNotWithinPolygon.geojson",
            "/booleans/within/false/MultiPoint/LineString/MultiPointsOnLineEndsIsNotWIthinLine.geojson",
            "/booleans/within/false/MultiPoint/LineString/MultiPointsIsNotWIthinLine.geojson",
            "/booleans/within/false/MultiPoint/MultiPoint/MultiPointIsNotWithinMultiPoint.geojson",
            "/booleans/within/false/MultiPoint/MultiPolygon/multipoint-not-within-multipolygon.geojson",
        ).forEach { path ->
            // tests containing skip in their name are testing features that are not implemented yet
            if (path.contains("skip")) {
                return@forEach
            }
            val fc = FeatureCollection.fromJson(readResource(path))
            val geometry1 = fc.features.first().geometry ?: error("geometry not found")
            val geometry2 = fc.last().geometry ?: error("geometry not found")

            assertFalse(
                within(geometry1, geometry2),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testError() {
        val point = point(0.1, 0.2)
        val multiPoint = multiPoint {
            +point
            +point
        }
        val lineString = lineString {
            +point
            +point
        }
        val multiLineString = multiLineString {
            +lineString
        }
        val polygon = polygon {
            ring {
                +lineString
                complete()
            }
        }
        val multiPolygon = multiPolygon {
            +polygon
        }
        val geometryCollection = geometryCollection {
            +point
            +multiPoint
            +lineString
            +multiLineString
            +polygon
            +multiPolygon
        }

        assertFailsWith<IllegalStateException> { within(point, point) }
        assertFailsWith<IllegalStateException> { within(point, multiLineString) }
        assertFailsWith<IllegalStateException> { within(point, geometryCollection) }
        assertFailsWith<IllegalStateException> { within(multiPoint, point) }
        assertFailsWith<IllegalStateException> { within(multiPoint, multiLineString) }
        assertFailsWith<IllegalStateException> { within(multiPoint, geometryCollection) }
        assertFailsWith<IllegalStateException> { within(lineString, point) }
        assertFailsWith<IllegalStateException> { within(lineString, multiPoint) }
        assertFailsWith<IllegalStateException> { within(lineString, multiLineString) }
        assertFailsWith<IllegalStateException> { within(lineString, geometryCollection) }
        assertFailsWith<IllegalStateException> { within(polygon, point) }
        assertFailsWith<IllegalStateException> { within(polygon, multiPoint) }
        assertFailsWith<IllegalStateException> { within(polygon, lineString) }
        assertFailsWith<IllegalStateException> { within(polygon, multiLineString) }
        assertFailsWith<IllegalStateException> { within(polygon, geometryCollection) }
        assertFailsWith<IllegalStateException> { within(polygon, geometryCollection) }
        val allGeometries =
            listOf(point, multiPoint, lineString, multiLineString, polygon, multiPolygon, geometryCollection)

        allGeometries.forEach { geometry ->
            assertFailsWith<IllegalStateException> { within(multiLineString, geometry) }
        }
        allGeometries.forEach { geometry ->
            assertFailsWith<IllegalStateException> { within(multiPolygon, geometry) }
        }
        allGeometries.forEach { geometry ->
            assertFailsWith<IllegalStateException> { within(geometryCollection, geometry) }
        }
    }
}
