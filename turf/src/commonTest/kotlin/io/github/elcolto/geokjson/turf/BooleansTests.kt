package io.github.elcolto.geokjson.turf

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.dsl.point
import io.github.elcolto.geokjson.geojson.dsl.polygon
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalSerializationApi
class BooleansTests {

    @Test
    fun testFeatureCollection() {
        // test for a simple polygon
        val poly = polygon {
            ring {
                point(0.0, 0.0)
                point(0.0, 100.0)
                point(100.0, 0.0)
                complete()
            }
        }
        val ptIn = point(50.0, 50.0)
        val ptOut = point(140.0, 150.0)

        assertTrue(booleanPointInPolygon(ptIn, poly), "point inside simple polygon")
        assertFalse(booleanPointInPolygon(ptOut, poly), "point outside simple polygon")

        // test for a concave polygon
        val concavePoly = polygon {
            ring {
                point(0.0, 0.0)
                point(50.0, 50.0)
                point(0.0, 100.0)
                point(100.0, 100.0)
                point(100.0, 0.0)
                complete()
            }
        }
        val ptConcaveIn = point(75.0, 75.0)
        val ptConcaveOut = point(25.0, 50.0)

        assertTrue(
            booleanPointInPolygon(ptConcaveIn, concavePoly),
            "point inside concave polygon",
        )
        assertFalse(
            booleanPointInPolygon(ptConcaveOut, concavePoly),
            "point outside concave polygon",
        )
    }

    @Test
    fun testPolyWithHole() {
        val ptInHole = point(-86.69208526611328, 36.20373274711739)
        val ptInPoly = point(-86.72229766845702, 36.20258997094334)
        val ptOutsidePoly = point(-86.75079345703125, 36.18527313913089)
        val polyHole = Feature.fromJson(readResource("booleans/in/poly-with-hole.geojson")).geometry as Polygon

        assertFalse(booleanPointInPolygon(ptInHole, polyHole))
        assertTrue(booleanPointInPolygon(ptInPoly, polyHole))
        assertFalse(booleanPointInPolygon(ptOutsidePoly, polyHole))
    }

    @Test
    fun testMultipolygonWithHole() {
        val ptInHole = point(-86.69208526611328, 36.20373274711739)
        val ptInPoly = point(-86.72229766845702, 36.20258997094334)
        val ptInPoly2 = point(-86.75079345703125, 36.18527313913089)
        val ptOutsidePoly = point(-86.75302505493164, 36.23015046460186)
        val multiPolyHole =
            Feature.fromJson(readResource("booleans/in/multipoly-with-hole.geojson")).geometry as MultiPolygon

        assertFalse(booleanPointInPolygon(ptInHole, multiPolyHole))
        assertTrue(booleanPointInPolygon(ptInPoly, multiPolyHole))
        assertTrue(booleanPointInPolygon(ptInPoly2, multiPolyHole))
        assertTrue(booleanPointInPolygon(ptInPoly, multiPolyHole))
        assertFalse(booleanPointInPolygon(ptOutsidePoly, multiPolyHole))
    }

    @Test
    fun testBoundaryTest() {
        val poly1 = polygon {
            ring {
                point(10.0, 10.0)
                point(30.0, 20.0)
                point(50.0, 10.0)
                point(30.0, 0.0)
                point(10.0, 10.0)
            }
        }
        val poly2 = polygon {
            ring {
                point(10.0, 0.0)
                point(30.0, 20.0)
                point(50.0, 0.0)
                point(30.0, 10.0)
                point(10.0, 0.0)
            }
        }
        val poly3 = polygon {
            ring {
                point(10.0, 0.0)
                point(30.0, 20.0)
                point(50.0, 0.0)
                point(30.0, -20.0)
                point(10.0, 0.0)
            }
        }
        val poly4 = polygon {
            ring {
                point(0.0, 0.0)
                point(0.0, 20.0)
                point(50.0, 20.0)
                point(50.0, 0.0)
                point(40.0, 0.0)
                point(30.0, 10.0)
                point(30.0, 0.0)
                point(20.0, 10.0)
                point(10.0, 10.0)
                point(10.0, 0.0)
                point(0.0, 0.0)
            }
        }
        val poly5 = polygon {
            ring {
                point(0.0, 20.0)
                point(20.0, 40.0)
                point(40.0, 20.0)
                point(20.0, 0.0)
                point(0.0, 20.0)
            }
            ring {
                point(10.0, 20.0)
                point(20.0, 30.0)
                point(30.0, 20.0)
                point(20.0, 10.0)
                point(10.0, 20.0)
            }
        }

        fun runTest(ignoreBoundary: Boolean) {
            val isBoundaryIncluded = !ignoreBoundary
            val tests = arrayOf(
                // 0
                Triple(poly1, point(10.0, 10.0), isBoundaryIncluded),
                Triple(poly1, point(30.0, 20.0), isBoundaryIncluded),
                Triple(poly1, point(50.0, 10.0), isBoundaryIncluded),
                Triple(poly1, point(30.0, 10.0), true),
                Triple(poly1, point(0.0, 10.0), false),
                Triple(poly1, point(60.0, 10.0), false),
                Triple(poly1, point(30.0, -10.0), false),
                Triple(poly1, point(30.0, 30.0), false),
                Triple(poly2, point(30.0, 0.0), false),
                Triple(poly2, point(0.0, 0.0), false),
                // 10
                Triple(poly2, point(60.0, 0.0), false),
                Triple(poly3, point(30.0, 0.0), true),
                Triple(poly3, point(0.0, 0.0), false),
                Triple(poly3, point(60.0, 0.0), false),
                Triple(poly4, point(0.0, 20.0), isBoundaryIncluded),
                Triple(poly4, point(10.0, 20.0), isBoundaryIncluded),
                Triple(poly4, point(50.0, 20.0), isBoundaryIncluded),
                Triple(poly4, point(0.0, 10.0), isBoundaryIncluded),
                Triple(poly4, point(5.0, 10.0), true),
                Triple(poly4, point(25.0, 10.0), true),
                // 20
                Triple(poly4, point(35.0, 10.0), true),
                Triple(poly4, point(0.0, 0.0), isBoundaryIncluded),
                Triple(poly4, point(20.0, 0.0), false),
                Triple(poly4, point(35.0, 0.0), false),
                Triple(poly4, point(50.0, 0.0), isBoundaryIncluded),
                Triple(poly4, point(50.0, 10.0), isBoundaryIncluded),
                Triple(poly4, point(5.0, 0.0), isBoundaryIncluded),
                Triple(poly4, point(10.0, 0.0), isBoundaryIncluded),
                Triple(poly5, point(20.0, 30.0), isBoundaryIncluded),
                Triple(poly5, point(25.0, 25.0), isBoundaryIncluded),
                // 30
                Triple(poly5, point(30.0, 20.0), isBoundaryIncluded),
                Triple(poly5, point(25.0, 15.0), isBoundaryIncluded),
                Triple(poly5, point(20.0, 10.0), isBoundaryIncluded),
                Triple(poly5, point(15.0, 15.0), isBoundaryIncluded),
                Triple(poly5, point(10.0, 20.0), isBoundaryIncluded),
                Triple(poly5, point(15.0, 25.0), isBoundaryIncluded),
                Triple(poly5, point(20.0, 20.0), false),
            )

            val testTitle =
                "Boundary " + (if (ignoreBoundary) "ignored " else "") + "test number "
            tests.forEachIndexed { i, item ->
                assertEquals(
                    booleanPointInPolygon(item.second, item.first, ignoreBoundary),
                    item.third,
                    testTitle + i,
                )
            }
        }
        runTest(false)
        runTest(true)
    }

    // https://github.com/Turfjs/turf-inside/issues/15
    @Test
    fun testIssue15() {
        val pt1 = point(-9.9964077, 53.8040989)
        val poly = Polygon(
            arrayOf(
                arrayOf(
                    doubleArrayOf(5.080336744095521, 67.89398938540765),
                    doubleArrayOf(0.35070899909145403, 69.32470003971179),
                    doubleArrayOf(-24.453622256504122, 41.146696777884564),
                    doubleArrayOf(-21.6445524714804, 40.43225902006474),
                    doubleArrayOf(5.080336744095521, 67.89398938540765),
                ),
            ),
        )

        assertTrue(booleanPointInPolygon(pt1, poly))
    }
}
