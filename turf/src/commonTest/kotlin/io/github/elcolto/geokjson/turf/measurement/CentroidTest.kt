package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test

@OptIn(ExperimentalTurfApi::class)
class CentroidTest {

    @Test
    fun testCollection() {
        val fc = FeatureCollection.fromJson(readResource("measurement/centroid/in/feature-collection.geojson"))
        val geometryCollection = GeometryCollection(fc.features.mapNotNull { it.geometry })

        val pos = centroid(geometryCollection).coordinates
        assertDoubleEquals(4.8336222767829895, pos.longitude, epsilon = 0.000001)
        assertDoubleEquals(45.76051644154402, pos.latitude, epsilon = 0.000001)
    }

    @Test
    fun testImbalancePolygon() {
        val polygon = Feature.fromJson<Polygon>(
            readResource("measurement/centroid/in/imbalanced-polygon.geojson"),
        ).getGeometry()

        val pos = centroid(polygon).coordinates
        assertDoubleEquals(4.851791984156558, pos.longitude, epsilon = 0.000001)
        assertDoubleEquals(45.78143055383553, pos.latitude, epsilon = 0.000001)
    }

    @Test
    fun testLineString() {
        val lineString = Feature.fromJson<LineString>(
            readResource("measurement/centroid/in/linestring.geojson"),
        ).getGeometry()

        val pos = centroid(lineString).coordinates
        assertDoubleEquals(4.860076904296875, pos.longitude, epsilon = 0.000001)
        assertDoubleEquals(45.75919915723537, pos.latitude, epsilon = 0.000001)
    }

    @Test
    fun testPoint() {
        val point = Feature.fromJson<Point>(readResource("measurement/centroid/in/point.geojson")).getGeometry()

        val pos = centroid(point).coordinates
        assertDoubleEquals(4.831961989402771, pos.longitude, epsilon = 0.000001)
        assertDoubleEquals(45.75764678012361, pos.latitude, epsilon = 0.000001)
    }

    @Test
    fun testPolygon() {
        val polygon = Feature.fromJson<Polygon>(readResource("measurement/centroid/in/polygon.geojson")).getGeometry()

        val pos = centroid(polygon).coordinates
        assertDoubleEquals(4.841194152832031, pos.longitude, epsilon = 0.000001)
        assertDoubleEquals(45.75807143030368, pos.latitude, epsilon = 0.000001)
    }
}
