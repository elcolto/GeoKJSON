package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.math.pow
import kotlin.math.round
import kotlin.test.Test

@ExperimentalTurfApi
class NearestPointOnLineTest {

    @Test
    fun testNearestPointOnMultiLine1() {
        testNearestPointOnMultiLine(
            "misc/nearestPointOnLine/in/multiLine1.geojson",
            "misc/nearestPointOnLine/out/multiLine1.geojson",
        )
    }

    @Test
    fun testNearestPointOnMultiLine2() {
        testNearestPointOnMultiLine(
            "misc/nearestPointOnLine/in/multiLine2.geojson",
            "misc/nearestPointOnLine/out/multiLine2.geojson",
        )
    }

    @Test
    fun testNearestPointOnMultiLine3() {
        testNearestPointOnMultiLine(
            "misc/nearestPointOnLine/in/multiLine3.geojson",
            "misc/nearestPointOnLine/out/multiLine3.geojson",
        )
    }

    private fun testNearestPointOnMultiLine(input: String, output: String) {
        val (multiLine, point) =
            FeatureCollection.fromJson(readResource(input)).features
        val expected = FeatureCollection.fromJson(readResource(output)).features.last() as Feature<Point>
        val nearestPointFeature = nearestPointOnLine(
            multiLine.geometry as MultiLineString,
            (point.geometry as Point).coordinates,
        )
        assertDoubleEquals(
            (expected.geometry as Point).coordinates.longitude,
            (nearestPointFeature.geometry as Point).coordinates.longitude,
            0.000001,
        )
        assertDoubleEquals(
            (expected.geometry as Point).coordinates.latitude,
            (nearestPointFeature.geometry as Point).coordinates.latitude,
            0.000001,
        )
        assertDoubleEquals(
            expected.nearestPointDistance,
            tunc(nearestPointFeature.nearestPointDistance, 6),
            0.000001,
            message = "distance check failed on input $input",
        )
        assertDoubleEquals(
            expected.nearestPointLocation,
            tunc(nearestPointFeature.nearestPointLocation, 6),
            0.000001,
            message = "location check failed on input $input",
        )
    }

    @Test
    fun testNearestPointOnLine1() {
        testNearestPointOnLine(
            "misc/nearestPointOnLine/in/line1.geojson",
            "misc/nearestPointOnLine/out/line1.geojson",
        )
    }

    @Test
    fun testNearestPointOnLineNorthernLatitude344() {
        testNearestPointOnLine(
            "misc/nearestPointOnLine/in/line-northern-latitude-344.geojson",
            "misc/nearestPointOnLine/out/line-northern-latitude-344.geojson",
        )
    }

    @Test
    fun testNearestPointOnLineRoute1() {
        testNearestPointOnLine(
            "misc/nearestPointOnLine/in/route1.geojson",
            "misc/nearestPointOnLine/out/route1.geojson",
        )
    }

    @Test
    fun testNearestPointOnLineRoute2() {
        testNearestPointOnLine(
            "misc/nearestPointOnLine/in/route2.geojson",
            "misc/nearestPointOnLine/out/route2.geojson",
        )
    }

    private fun testNearestPointOnLine(input: String, output: String) {
        val (line, point) =
            FeatureCollection.fromJson(readResource(input)).features
        val expected = FeatureCollection.fromJson(readResource(output)).features
            .last() as Feature<Point>
        val nearestPointFeature = nearestPointOnLine(
            line.geometry as LineString,
            (point.geometry as Point).coordinates,
        )
        assertDoubleEquals(
            (expected.geometry as Point).coordinates.longitude,
            (nearestPointFeature.geometry as Point).coordinates.longitude,
            0.000001,
            message = "longitude check failed on input $input",
        )
        assertDoubleEquals(
            (expected.geometry as Point).coordinates.latitude,
            (nearestPointFeature.geometry as Point).coordinates.latitude,
            0.000001,
            message = "latitude check failed on input $input",
        )
        assertDoubleEquals(
            expected.nearestPointDistance,
            nearestPointFeature.nearestPointDistance,
            0.000001,
            message = "distance check failed on input $input",
        )
        assertDoubleEquals(
            expected.nearestPointLocation,
            nearestPointFeature.nearestPointLocation,
            0.000001,
            message = "location check failed on input $input",
        )
    }

    private companion object {
        fun tunc(number: Double, precision: Int): Double {
            val multiplier = 10.0.pow(precision)
            return round(number * multiplier) / multiplier
        }
    }
}
