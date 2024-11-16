package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class PointOnFeatureTest {

    @Test
    fun testPointOnLine() {
        val fc = FeatureCollection.fromJson(readResource("measurement/pointonfeature/in/lines.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/lines.json"),
        ).features.last()
            .getGeometry() as Point

        val point = fc.features.map { it.getGeometry() }
            .let { GeometryCollection(it) }
            .let { pointOnFeature(it) }

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }

    @Test
    fun testPointOnMultiLineString() {
        val feature = Feature.fromJson<MultiLineString>(readResource("measurement/pointonfeature/in/multiline.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/multiline.json"),
        ).features.last()
            .getGeometry() as Point

        val point = pointOnFeature(feature.getGeometry())

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }

    @Test
    fun testPointOnMultiPoint() {
        val feature = Feature.fromJson<MultiPoint>(readResource("measurement/pointonfeature/in/multipoint.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/multipoint.json"),
        ).features.last()
            .getGeometry() as Point

        val point = pointOnFeature(feature.getGeometry())

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }

    @Test
    fun testPointOnPolygon() {
        val fc = FeatureCollection.fromJson(readResource("measurement/pointonfeature/in/polygons.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/polygons.json"),
        ).features.last()
            .getGeometry() as Point

        val point = fc.features.map { it.getGeometry() }
            .let { GeometryCollection(it) }
            .let { pointOnFeature(it) }

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }

    @Test
    fun testPointOnPolygonInCenter() {
        val fc = FeatureCollection.fromJson(readResource("measurement/pointonfeature/in/polygon-in-center.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/polygon-in-center.json"),
        ).features.last()
            .getGeometry() as Point

        val point = fc.features.map { it.getGeometry() }
            .let { GeometryCollection(it) }
            .let { pointOnFeature(it) }

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }

    @Test
    fun testPointOnMultiPolygon() {
        val feature = Feature.fromJson<MultiPolygon>(readResource("measurement/pointonfeature/in/multipolygon.json"))
        val expectedPoint = FeatureCollection.fromJson(
            readResource("measurement/pointonfeature/out/multipolygon.json"),
        ).features.last()
            .getGeometry() as Point

        val point = pointOnFeature(feature.getGeometry())

        assertEquals(expectedPoint.coordinates.longitude, point.coordinates.longitude, 0.00001)
        assertEquals(expectedPoint.coordinates.latitude, point.coordinates.latitude, 0.00001)
    }
}
