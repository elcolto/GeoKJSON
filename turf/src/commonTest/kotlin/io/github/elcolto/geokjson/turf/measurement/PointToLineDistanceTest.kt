package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.math.pow
import kotlin.math.round
import kotlin.test.Test

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

    private fun testPointToLineDistance(path: String, expectedDistance: Double) {
        val featureCollection = FeatureCollection.fromJson(readResource("measurement/pointtolinedistance/in/$path"))
        val properties = (featureCollection.foreignMembers["properties"] as? Map<String, Any>).orEmpty()
        val units = properties["units"]?.let { Units.valueOf((it as String).capitalize()) }
            ?: Units.Kilometers

        val point = featureCollection.first().geometry as Point
        val lineString = featureCollection.features[1].geometry as LineString

        val distance = pointToLineDistance(point, lineString, units)

        val multiplier = 10.0.pow(10)

        assertDoubleEquals(
            expectedDistance,
            round(distance * multiplier) / multiplier,
            epsilon = 0.0000001,
            "distance failed on path $path",
        )
    }
}
