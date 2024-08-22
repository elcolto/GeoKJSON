package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalTurfApi
class TouchesTest {

    @Test
    fun testPointTrue() {
        listOf(
            "booleans/touches/true/Point/LineString/PointOnEndLine.geojson",
            "booleans/touches/true/Point/LineString/PointOnStartLine.geojson",
            "booleans/touches/true/Point/MultiLineString/MpOnEndLine.geojson",
            "booleans/touches/true/Point/MultiLineString/MpOnStartLine.geojson",
            "booleans/touches/true/Point/MultiPolygon/PointTouchesMultipolygon.geojson",
            "booleans/touches/true/Point/MultiPolygon/PointTouchesMultipolygonHole.geojson",
            "booleans/touches/true/Point/Polygon/PointOnEdgePolygon.geojson",
            "booleans/touches/true/Point/Polygon/PointOnHole.geojson",
            "booleans/touches/true/Point/Polygon/PointOnVerticePolygon.geojson",
        ).forEach { path ->
            val (point, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(point!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testPointFalse() {
        listOf(
            "booleans/touches/false/Point/LineString/PointIsNotTouchLine.geojson",
            "booleans/touches/false/Point/LineString/PointOnMidLinestring.geojson",
            "booleans/touches/false/Point/MultiLineString/MpNotTouchMidLineString.geojson",
            "booleans/touches/false/Point/MultiLineString/MpOnMidLineString.geojson",
            "booleans/touches/false/Point/MultiPolygon/PointNotTouchMultipolygon.geojson",
            "booleans/touches/false/Point/Polygon/PointDoesNotTouchPolygon.geojson",
            "booleans/touches/false/Point/Polygon/PointInsidePolygon.geojson",
        ).forEach { path ->
            val (point, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(point!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiPointTrue() {
        listOf(
            "booleans/touches/true/MultiPoint/MultiPolygon/multipoint-touches-multipolygon.geojson",
            "booleans/touches/true/MultiPoint/MultiLineString/MpTouchesSecondMultiLine.geojson",
            "booleans/touches/true/MultiPoint/LineString/MultipointTouchesLine.geojson",
            "booleans/touches/true/MultiPoint/MultiLineString/MpTouchesEndMultiLine.geojson",
            "booleans/touches/true/MultiPoint/Polygon/MultiPointIsWithinPolygon.geojson",
        ).forEach { path ->
            val (multiPoint, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(multiPoint!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiPointFalse() {
        listOf(
            "booleans/touches/false/MultiPoint/LineString/MultipointDoesNotTouchLine.geojson",
            "booleans/touches/false/MultiPoint/LineString/MultiPointTouchesInsideLine.geojson",
            "booleans/touches/false/MultiPoint/MultiLineString/MpDoesNotTouchMultiLine.geojson",
            "booleans/touches/false/MultiPoint/MultiLineString/MpTouchesInternalMultiLine.geojson",
            "booleans/touches/false/MultiPoint/Polygon/MultiPointInsidePolygon.geojson",
            "booleans/touches/false/MultiPoint/Polygon/MultiPointNoTouchPolygon.geojson",
            "booleans/touches/false/MultiPoint/MultiPolygon/multipoint-inside-multipolygon.geojson",
            "booleans/touches/false/MultiPoint/MultiPolygon/MultiPointDoesNotTouchMultipolygon.geojson",
        ).forEach { path ->
            val (multiPoint, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(multiPoint!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testLineStringTrue() {
        listOf(
            "booleans/touches/true/LineString/LineString/LineTouchesEndpoint.geojson",
            "booleans/touches/true/LineString/MultiLineString/LineStringTouchesEnd.geojson",
            "booleans/touches/true/LineString/MultiLineString/LineStringTouchesStart.geojson",
            "booleans/touches/true/LineString/MultiPoint/MultipointTouchesLine.geojson",
            "booleans/touches/true/LineString/MultiPolygon/LineTouchesMultiPoly.geojson",
            "booleans/touches/true/LineString/MultiPolygon/LineTouchesSecondMultiPoly.geojson",
            "booleans/touches/true/LineString/Polygon/LineTouchesPolygon.geojson",
        ).forEach { path ->
            val (lineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(lineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testLineStringFalse() {
        listOf(
            "booleans/touches/false/LineString/LineString/LinesExactSame.geojson",
            "booleans/touches/false/LineString/LineString/LivesOverlap.geojson",
            "booleans/touches/false/LineString/MultiLineString/LineStringOverlapsMultiLinestring.geojson",
            "booleans/touches/false/LineString/MultiLineString/LineStringSameAsMultiLinestring.geojson",
            "booleans/touches/false/LineString/MultiPoint/LineStringDoesNotTouchMP.geojson",
            "booleans/touches/false/LineString/MultiPoint/LineStringTouchesMultiPointButInternal.geojson",
            "booleans/touches/false/LineString/MultiPolygon/LineDoesNotTouchMultiPoly.geojson",
            "booleans/touches/false/LineString/Polygon/LineCrossesPolygon.geojson",
            "booleans/touches/false/LineString/Polygon/LineDoesNotTouch.geojson",
            "booleans/touches/false/LineString/Polygon/LineWIthinPolygon.geojson",
        ).forEach { path ->
            val (lineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(lineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiLineStringTrue() {
        listOf(
            "booleans/touches/true/MultiLineString/LineString/MultiLineTouchesLine.geojson",
            "booleans/touches/true/MultiLineString/MultiLineString/MultiLineTouchesMultiLine.geojson",
            "booleans/touches/true/MultiLineString/MultiPoint/MultiLineTouchesMultiPoint.geojson",
            "booleans/touches/true/MultiLineString/Point/MultiLineTouchesPoint.geojson",
            "booleans/touches/true/MultiLineString/Polygon/MultiLineTouchesPolygon.geojson",
        ).forEach { path ->
            val (multiLineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(multiLineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiLineStringFalse() {
        listOf(
            "booleans/touches/false/MultiLineString/LineString/MultiLineStringOverlapsLine.geojson",
            "booleans/touches/false/MultiLineString/LineString/MultiLineStringSameAsLine.geojson",
            "booleans/touches/false/MultiLineString/MultiLineString/MultiLineStringsOverlap.geojson",
            "booleans/touches/false/MultiLineString/MultiLineString/MultiLineStringsSame.geojson",
            "booleans/touches/false/MultiLineString/MultiPoint/MpTouchesInternalMultiline.geojson",
            "booleans/touches/false/MultiLineString/MultiPoint/MultiPointNotTouchMultiline.geojson",
            "booleans/touches/false/MultiLineString/MultiPolygon/MultiLineInsideMultipoly.geojson",
            "booleans/touches/false/MultiLineString/Point/PointNotTouchMultiLinestring.geojson",
            "booleans/touches/false/MultiLineString/Point/PointTouchesMidLineString.geojson",
            "booleans/touches/false/MultiLineString/Polygon/MultiLineInsidePoly.geojson",
            "booleans/touches/false/MultiLineString/Polygon/MultiLineNotTouchPoly.geojson",
        ).forEach { path ->
            val (multiLineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(multiLineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testPolygonTrue() {
        listOf(
            "booleans/touches/true/Polygon/LineString/PolygonTouchesLines.geojson",
            "booleans/touches/true/Polygon/MultiLineString/PolygonTouchesMultiline.geojson",
            "booleans/touches/true/Polygon/MultiPoint/PolygonTouchesMultiPoint.geojson",
            "booleans/touches/true/Polygon/MultiPolygon/PolyTouchMultiPolys.geojson",
            "booleans/touches/true/Polygon/Point/PolygonTouchesPoint.geojson",
            "booleans/touches/true/Polygon/Point/PolygonTouchesPointVertice.geojson",
            "booleans/touches/true/Polygon/Polygon/PolygonsTouchVertices.geojson",
            "booleans/touches/true/Polygon/Polygon/PolygonTouchesEdges.geojson",
        ).forEach { path ->
            val (multiLineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(multiLineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testPolygonFalse() {
        listOf(
            "booleans/touches/false/Polygon/LineString/PolyDoesNotTouchLine.geojson",
            "booleans/touches/false/Polygon/MultiLineString/PolyNotTouchMultiLine.geojson",
            "booleans/touches/false/Polygon/MultiLineString/PolyOverlapMultiLine.geojson",
            "booleans/touches/false/Polygon/MultiPoint/PolygonNoTouchMultiPoint.geojson",
            "booleans/touches/false/Polygon/MultiPoint/PolygonOverlapsMultiPoint.geojson",
            "booleans/touches/false/Polygon/MultiPolygon/PolyNotTouchMultipoly.geojson",
            "booleans/touches/false/Polygon/Point/PolygonDoesNotTouchPoint.geojson",
            "booleans/touches/false/Polygon/Point/PolygonOverlapsPoint.geojson",
            "booleans/touches/false/Polygon/Polygon/PolygonsDontTouch.geojson",
            "booleans/touches/false/Polygon/Polygon/PolygonsOverlap.geojson",
        ).forEach { path ->
            val (multiLineString, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(multiLineString!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiPolygonTrue() {
        listOf(
            "booleans/touches/true/MultiPolygon/MultiLineString/MultiLineTouchesMultiPoly.geojson",
            "booleans/touches/true/MultiPolygon/MultiPoint/MultiPolyTouchesMultiPoint.geojson",
            "booleans/touches/true/MultiPolygon/MultiPolygon/MultiPolyTouchesMultiPoly.geojson",
            "booleans/touches/true/MultiPolygon/Point/MpTouchesPoint.geojson",
            "booleans/touches/true/MultiPolygon/Polygon/MultiPolyTouchesPoly.geojson",
        ).forEach { path ->
            val (multiPolygon, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertTrue(
                touches(multiPolygon!!, other!!),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testMultiPolygonFalse() {
        listOf(
            "booleans/touches/false/MultiPolygon/LineString/MultiPolyNotTouchLineString.geojson",
            "booleans/touches/false/MultiPolygon/MultiLineString/MultiPolyOverlapsMultiLine.geojson",
            "booleans/touches/false/MultiPolygon/MultiPoint/MultiPolyNotTouchMultiPoint.geojson",
            "booleans/touches/false/MultiPolygon/MultiPolygon/MultiPolysDoNotTouch.geojson",
            "booleans/touches/false/MultiPolygon/MultiPolygon/MultiPolysOverlap.geojson",
            "booleans/touches/false/MultiPolygon/Point/MultiPolyNotTouchPoint.geojson",
        ).forEach { path ->
            val (multiPolygon, other) = FeatureCollection.fromJson(readResource(path))
                .features.map { it.geometry }

            assertFalse(
                touches(multiPolygon!!, other!!),
                "assertion failed for path $path",
            )
        }
    }
}
