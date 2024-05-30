@file:Suppress("MagicNumber")

package io.github.elcolto.geokjson.turf

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.geojson.dsl.geometryCollection
import io.github.elcolto.geokjson.geojson.dsl.polygon
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

@ExperimentalTurfApi
class TurfMeasurementTest {

    @Test
    fun testAlong() {
        val geometry = LineString.fromJson(readResource("measurement/along/lineString.json"))

        assertEquals(Position(-79.4179672644524, 43.636029126566484), along(geometry, 1.0))
        assertEquals(Position(-79.39973865844715, 43.63797943080659), along(geometry, 2.5))
        assertEquals(Position(-79.37493324279785, 43.64470906117713), along(geometry, 100.0))
        assertEquals(geometry.coordinates.last(), along(geometry, 100.0))
    }

    @Test
    fun testArea() {
        val geometry = Polygon.fromJson(readResource("measurement/area/polygon.json"))
        assertDoubleEquals(236446.506, area(geometry), 0.001, "Single polygon")

        val other = Polygon.fromJson(readResource("measurement/area/other.json"))
        val collection = geometryCollection {
            +geometry
            +other
        }
        assertDoubleEquals(4173831.866, area(collection), 0.001, "Geometry Collection")
    }

    @Test
    fun testBbox() {
        val point = Point.fromJson(readResource("measurement/bbox/point.json"))
        assertEquals(
            BoundingBox(point.coordinates, point.coordinates),
            bbox(point)
        )

        val lineString = LineString.fromJson(readResource("measurement/bbox/lineString.json"))
        assertEquals(
            BoundingBox(-79.376220703125, 43.65197548731187, -73.58642578125, 45.4986468234261),
            bbox(lineString)
        )

        val polygon = Polygon.fromJson(readResource("measurement/bbox/polygon.json"))
        assertEquals(
            BoundingBox(-64.44580078125, 45.9511496866914, -61.973876953125, 47.07012182383309),
            bbox(polygon)
        )
    }

    @Test
    fun testBboxPolygon() {
        val bbox = BoundingBox(12.1, 34.3, 56.5, 78.7)

        val polygon = polygon {
            ring {
                +Position(12.1, 34.3)
                +Position(56.5, 34.3)
                +Position(56.5, 78.7)
                +Position(12.1, 78.7)
                complete()
            }
        }

        assertEquals(polygon, bboxPolygon(bbox))
    }

    @Test
    fun testBearing() {
        val start = Position(-75.0, 45.0)
        val end = Position(20.0, 60.0)

        assertDoubleEquals(37.75, bearing(start, end), 0.01, "Initial Bearing")
        assertDoubleEquals(120.01, bearing(start, end, final = true), 0.01, "Final Bearing")
    }

    @Test
    fun testDestination() {
        val point0 = Position(-75.0, 38.10096062273525)
        val (longitude, latitude) = destination(point0, 100.0, 0.0)

        assertDoubleEquals(-75.0, longitude, 0.1)
        assertDoubleEquals(39.000281, latitude, 0.000001)
    }

    @Test
    fun testDistance() {
        val a = Position(-73.67, 45.48)
        val b = Position(-79.48, 43.68)

        assertEquals(501.64563403765925, distance(a, b))
    }

    @Test
    fun testLength() {
        val geometry = LineString.fromJson(readResource("measurement/length/lineString.json"))

        assertEquals(42.560767589197006, length(geometry, Units.Kilometers))
    }

    @Test
    fun testMidpoint() {
        val point1 = Position(-79.3801, 43.6463)
        val point2 = Position(-74.0071, 40.7113)

        val midpoint = midpoint(point1, point2)

        assertDoubleEquals(-76.6311, midpoint.longitude, 0.0001)
        assertDoubleEquals(42.2101, midpoint.latitude, 0.0001)
    }

    @Test
    fun testCenterFromFeature() {
        val geometry = Polygon.fromJson(readResource("measurement/area/other.json"))

        val centerPoint = center(Feature(geometry))

        assertDoubleEquals(-75.71805238723755, centerPoint.coordinates.longitude, 0.0001)
        assertDoubleEquals(45.3811030151199, centerPoint.coordinates.latitude, 0.0001)
    }

    @Test
    fun testCenterFromGeometry() {
        val geometry = Polygon.fromJson(readResource("measurement/area/other.json"))

        val centerPoint = center(geometry)

        assertDoubleEquals(-75.71805238723755, centerPoint.coordinates.longitude, 0.0001)
        assertDoubleEquals(45.3811030151199, centerPoint.coordinates.latitude, 0.0001)
    }

    @Test
    fun testGreatCircleBasic() {
        val startLon = -122.0
        val startLat = 48.0
        val endLon = 28.125
        val endLat = 43.32517767999296

        val start = Position(startLon, startLat)
        val end = Position(endLon, endLat)

        val greatCircle = greatCircle(start, end, pointCount = 99)

        assertEquals(99, greatCircle.coordAll().size)
        assertIs<LineString>(greatCircle)
    }

    @Test
    fun testGreatCirclePassesAntimeridian() {
        val startLon = -122.349358
        val startLat = 47.620422
        val endLon = 77.036560
        val endLat = 38.897957

        val start = Position(startLon, startLat)
        val end = Position(endLon, endLat)
        val geometry = greatCircle(start, end, pointCount = 100)
        assertIs<MultiLineString>(geometry)
    }

    @Test
    fun testGreatCircleAntipodals() {
        val startLat = 47.620422

        val start = Position(-122.349358, startLat)
        val antipodal = Position(106.33, startLat)

        assertFails {
            greatCircle(start, antipodal)
        }
    }
}
