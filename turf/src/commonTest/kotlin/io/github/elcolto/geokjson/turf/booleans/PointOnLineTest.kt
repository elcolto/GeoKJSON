package io.github.elcolto.geokjson.turf.booleans

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.asInstance
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalTurfApi
class PointOnLineTest {

    @Test
    fun testDefaultValues() {
        val start = Position(54.0, 10.0)
        val end = Position(53.0, 10.0)
        val lineString = LineString(start, end)
        assertTrue(pointOnLine(start, lineString))
        assertTrue(pointOnLine(Point(start), lineString))
    }

    @Test
    fun testTrue() {
        listOf(
            "booleans/pointOnLine/true/LineWithOnly1Segment.geojson",
            "booleans/pointOnLine/true/LineWithOnly1SegmentOnStart.geojson",
            "booleans/pointOnLine/true/PointOnFirstSegment.geojson",
            "booleans/pointOnLine/true/PointOnLastSegment.geojson",
            "booleans/pointOnLine/true/PointOnLineEnd.geojson",
            "booleans/pointOnLine/true/PointOnLineMidpoint.geojson",
            "booleans/pointOnLine/true/PointOnLineMidVertice.geojson",
            "booleans/pointOnLine/true/PointOnLineStart.geojson",
            "booleans/pointOnLine/true/PointOnLineWithEpsilon.geojson",
        ).forEach { path ->
            val fc = FeatureCollection.fromJson(readResource(path))
            val foreignMembers = fc.foreignMembers["properties"].asInstance<Map<String, Any>>()
            val ignoreEndVertices = foreignMembers?.get("ignoreEndVertices").asInstance<Boolean>() ?: false
            val absoluteTolerance = foreignMembers?.get("epsilon").asInstance<Double>()
            val point = fc.features.first().geometry as Point
            val lineString = fc.last().geometry as LineString

            assertTrue(
                pointOnLine(point, lineString, ignoreEndVertices, absoluteTolerance),
                "assertion failed for path $path",
            )
        }
    }

    @Test
    fun testFalse() {
        listOf(
            "booleans/pointOnLine/false/LineWithOnly1SegmentIgnoreBoundary.geojson",
            "booleans/pointOnLine/false/LineWithOnly1SegmentIgnoreBoundaryEnd.geojson",
            "booleans/pointOnLine/false/notOnLine.geojson",
            "booleans/pointOnLine/false/PointIsOnLineButFailsWithoutEpsilonForBackwardsCompatibility.geojson",
            "booleans/pointOnLine/false/PointIsOnLineButFailsWithSmallEpsilonValue.geojson",
            "booleans/pointOnLine/false/PointOnEndFailsWhenIgnoreEndpoints.geojson",
            "booleans/pointOnLine/false/PointOnStartFailsWhenIgnoreEndpoints.geojson",
        ).forEach { path ->
            val fc = FeatureCollection.fromJson(readResource(path))
            val foreignMembers = fc.foreignMembers["properties"].asInstance<Map<String, Any>>()
            val ignoreEndVertices = foreignMembers?.get("ignoreEndVertices").asInstance<Boolean>() ?: false
            val absoluteTolerance = foreignMembers?.get("epsilon").asInstance<Double>()
            val point = fc.features.first().geometry as Point
            val lineString = fc.last().geometry as LineString

            assertFalse(
                pointOnLine(point, lineString, ignoreEndVertices, absoluteTolerance),
                "assertion failed for path $path",
            )
        }
    }
}
