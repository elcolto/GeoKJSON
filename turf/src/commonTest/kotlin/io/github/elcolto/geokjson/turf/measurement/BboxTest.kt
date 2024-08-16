package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.geojson.dsl.polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalTurfApi
class BboxTest {

    @Test
    fun testBbox() {
        val point = Point.fromJson(readResource("measurement/bbox/point.json"))
        assertEquals(
            BoundingBox(point.coordinates, point.coordinates),
            bbox(point),
        )

        val lineString = LineString.fromJson(readResource("measurement/bbox/lineString.json"))
        assertEquals(
            BoundingBox(-79.376220703125, 43.65197548731187, -73.58642578125, 45.4986468234261),
            bbox(lineString),
        )

        val polygon = Polygon.fromJson(readResource("measurement/bbox/polygon.json"))
        assertEquals(
            BoundingBox(-64.44580078125, 45.9511496866914, -61.973876953125, 47.07012182383309),
            bbox(polygon),
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
}
