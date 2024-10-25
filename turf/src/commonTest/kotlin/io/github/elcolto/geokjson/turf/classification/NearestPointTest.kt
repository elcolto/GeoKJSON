package io.github.elcolto.geokjson.turf.classification

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.geojson.dsl.featureCollection
import io.github.elcolto.geokjson.geojson.dsl.lineString
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.utils.asInstance
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalTurfApi::class)
class NearestPointTest {

    @Test
    fun testNearestPoint() {
        val fc = FeatureCollection.fromJson(readResource("classification/nearest-point/in/points.json"))
        val target = fc.foreignMembers["properties"].asInstance<Map<String, List<Double>>>()?.get("targetPoint")
        val targetPoint = Point(target!!.toDoubleArray())
        val nearestPoint = nearestPointFeature(targetPoint, fc, units = Units.Miles)

        val out = FeatureCollection.fromJson(readResource("classification/nearest-point/out/points.json"))
        val result = out.find { it.properties["featureIndex"] == 14 }

        val geometry = result?.geometry
        assertEquals(geometry, nearestPoint.geometry)
    }

    @Test
    fun testDistanceNearestPoint() {
        val pt1 = Point(doubleArrayOf(40.0, 50.0))
        val pt2 = Point(doubleArrayOf(20.0, -10.0))

        val distanceInKilometers = nearestPointFeature(
            Point(doubleArrayOf(0.0, 0.0)),
            listOf(pt1, pt2),
        ).properties[NearestPoint.DISTANCE_TO_POINT].asInstance<Double>()!!

        val distanceInMeters = nearestPointFeature(
            Point(doubleArrayOf(0.0, 0.0)),
            listOf(pt1, pt2),
            units = Units.Meters,
        ).properties[NearestPoint.DISTANCE_TO_POINT].asInstance<Double>()!!

        assertEquals(distanceInKilometers, distanceInMeters / 1000)
    }

    @Test
    fun testRequiredNonEmptyInput() {
        val target = Position(0.0, 0.0)
        assertFailsWith<IllegalArgumentException> {
            nearestPoint(target, emptyList())
        }

        assertFailsWith<IllegalArgumentException> {
            nearestPoint(Point(target), emptyList())
        }
    }

    @Test
    fun testNonSupportedFeatureCollection() {
        val fc = featureCollection {
            lineString {
                point(0.0, 0.1)
                point(10.0, 1.0)
            }

            lineString {
                point(5.0, 0.1)
                point(15.0, 1.0)
            }
        }
        assertFailsWith<IllegalArgumentException> {
            nearestPoint(Point(doubleArrayOf(50.0, 40.0)), fc)
        }
    }
}
