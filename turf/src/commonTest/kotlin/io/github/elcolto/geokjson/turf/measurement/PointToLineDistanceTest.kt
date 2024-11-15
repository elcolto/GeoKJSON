package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.math.pow
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTurfApi::class)
internal class PointToLineDistanceTest {

    @Test
    fun testCityLine1() {
        testPointToLineDistance("city-line1.geojson", 0.9993988759)
    }

    @Test
    fun testCityLine2() {
        testPointToLineDistance("city-line2.geojson", 3.5827706171)
    }

    @Test
    fun testCitySegmentInside1() {
        testPointToLineDistance("city-segment-inside1.geojson", 1.1444315439)
    }

    @Test
    fun testCitySegmentInside2() {
        testPointToLineDistance("city-segment-inside2.geojson", 0.9974854079)
    }

    @Test
    fun testCitySegmentInside3() {
        testPointToLineDistance("city-segment-inside3.geojson", 3.497089823)
    }

    @Test
    fun testCitySegmentObtuse1() {
        testPointToLineDistance("city-segment-obtuse1.geojson", 2.8573246363)
    }

    @Test
    fun testCitySegmentObtuse2() {
        testPointToLineDistance("city-segment-obtuse2.geojson", 3.3538913334)
    }

    @Test
    fun testCitySegmentProjected1() {
        testPointToLineDistance("city-segment-projected1.geojson", 3.5886611693)
    }

    @Test
    fun testCitySegmentProjected2() {
        testPointToLineDistance("city-segment-projected2.geojson", 4.163469898)
    }

    @Test
    fun testIssue1156() {
        testPointToLineDistance("issue-1156.geojson", 189.0688049313)
    }

    @Test
    fun testLineFiji() {
        testPointToLineDistance("line-fiji.geojson", 26.8418623324)
    }

    @Test
    fun testLineResoluteBay() {
        testPointToLineDistance("line-resolute-bay.geojson", 424.8707545895)
    }

    @Test
    fun testLine1() {
        testPointToLineDistance("line1.geojson", 25.6934928869)
    }

    @Test
    fun testLine2() {
        testPointToLineDistance("line2.geojson", 188.0655411712)
    }

    @Test
    fun testSegmentFiji() {
        testPointToLineDistance("segment-fiji.geojson", 27.3835094108)
    }

    @Test
    fun testSegment1() {
        testPointToLineDistance("segment1.geojson", 69.0934195756)
    }

    @Test
    fun testSegment1a() {
        testPointToLineDistance("segment1a.geojson", 69.0934195756)
    }

    @Test
    fun testSegment2() {
        testPointToLineDistance("segment2.geojson", 69.0934195756)
    }

    @Test
    fun testSegment3() {
        testPointToLineDistance("segment3.geojson", 69.0828960461)
    }

    @Test
    fun testSegment4() {
        testPointToLineDistance("segment4.geojson", 340.7276874254)
    }

    @Test
    fun checkPlanarAndGeodesicResultsAreDifferent() {
        val pos = Position(0.0, 0.0)
        val line = LineString(Position(10.0, 10.0), Position(-1.0, 1.0))
        val geoOut = pointToLineDistance(pos, line, method = DistanceMethod.GEODESIC)
        val planarOut = pointToLineDistance(pos, line, method = DistanceMethod.PLANAR)
        assertNotEquals(geoOut, planarOut)
    }

    @Test
    fun testTurfJsIssue2270() {
        // This point should be about 3.4m from the line. Definitely not 4.3!
        // https://github.com/Turfjs/turf/issues/2270#issuecomment-1073787691
        val pt1 = Position(10.748363481687537, 59.94785299224352)
        val line1 = LineString(
            Position(10.7482034954027, 59.9477463357725),
            Position(10.7484686179823, 59.9480515133037),
        )
        val d1 = pointToLineDistance(pt1, line1, units = Units.Meters)
        assertEquals(d1, 3.4, 0.09, "Point is approx 3.4m from line")

        // This point should be about 1000m from the line. Definitely not 1017!
        // https://github.com/Turfjs/turf/issues/2270#issuecomment-2307907374
        val pt2 = Position(11.991689565382663, 34.00578044047174)
        val line2 = LineString(
            Position(12.0, 34.0),
            Position(11.993027757380355, 33.99311060965808),
        )
        val d2 = round(pointToLineDistance(pt2, line2, units = Units.Meters))
        assertEquals(d2, 1000.0, "Point is approx 1000m from line")
    }

    @Test
    fun testTurfJsIssue1156() {
        // According to issue 1156 the result of pointToLineDistance varies suddenly
        // at a certain longitude. Code below

        // When the error occurs we would expect to see 'd' jump from about 188 to
        // over 800
        // ...
        // [ 11.028347, 41 ] 188.9853459755496 189.00642024172396
        // [ 11.028348, 41 ] 842.5784253401666 189.08988164279026
        //                   ^^^

        // https://github.com/Turfjs/turf/issues/1156#issue-279806209
        val line = LineString(
            Position(10.964832305908203, 41.004681939880314),
            Position(10.977363586425781, 40.99096148527727),
            Position(10.983200073242188, 40.97075154073346),
            Position(11.02834701538086, 40.98372150040732),
            Position(11.02508544921875, 41.00716631272605),
            Position(10.994186401367188, 41.01947819666632),
            Position(10.964832305908203, 41.004681939880314),
        )

        val x0 = 11.02834
        val x1 = 11.02835
        val dx = 0.000001
        var x = x0
        var i = 0
        while (x <= x1) {
            val p = Position(x, 41.0)
            val d = pointToLineDistance(p, line, units = Units.Meters)
            assertTrue(d < 190, "pointToLineDistance never jumps past 190")
            i++
            x = x0 + i * dx
        }
    }

    private fun testPointToLineDistance(path: String, expectedDistance: Double) {
        val featureCollection = FeatureCollection.fromJson(readResource("measurement/pointtolinedistance/in/$path"))
        val properties = (featureCollection.foreignMembers["properties"] as? Map<String, Any>).orEmpty()
        val units = properties["units"]?.let { Units.valueOf((it as String).capitalize()) }
            ?: Units.Kilometers

        val point = featureCollection.first().geometry as Point
        val lineString = featureCollection.features[1].geometry as LineString

        val distance = pointToLineDistance(point, lineString, units)

        val multiplier = 10.0.pow(10)

        assertEquals(
            expectedDistance,
            round(distance * multiplier) / multiplier,
            absoluteTolerance = 0.0000001,
            "distance failed on path $path",
        )
    }
}
