package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class CenterOfMassTest {

    @Test
    fun should_calculate_center_of_mass_for_feature_collection() {
        val input = "measurement/centerofmass/in/feature-collection.geojson"
        val expected = "measurement/centerofmass/out/feature-collection.geojson"

        val fc = FeatureCollection.fromJson(readResource(input))
        val expectedPoint = FeatureCollection.fromJson(readResource(expected))
            .features.first().getGeometry() as Point
        val geometryCollection = fc.features.map { it.getGeometry() }.let { GeometryCollection(it) }
        val centerOfMass = centerOfMass(geometryCollection)

        assertEquals(expectedPoint.coordinates.longitude, centerOfMass.coordinates.longitude, 0.0001)
        assertEquals(expectedPoint.coordinates.latitude, centerOfMass.coordinates.latitude, 0.0001)
    }

    @Test
    fun should_calculate_center_of_mass_for_imbalanced_polygon() {
        val input = "measurement/centerofmass/in/imbalanced-polygon.geojson"
        val expected = "measurement/centerofmass/out/imbalanced-polygon.geojson"
        testCenterOfMass<Polygon>(input, expected)
    }

    @Test
    fun should_calculate_center_of_mass_for_linestring() {
        val input = "measurement/centerofmass/in/linestring.geojson"
        val expected = "measurement/centerofmass/out/linestring.geojson"
        testCenterOfMass<LineString>(input, expected)
    }

    @Test
    fun should_calculate_center_of_mass_for_point() {
        val input = "measurement/centerofmass/in/point.geojson"
        val expected = "measurement/centerofmass/out/point.geojson"
        testCenterOfMass<Point>(input, expected)
    }

    @Test
    fun should_calculate_center_of_mass_for_polygon() {
        val input = "measurement/centerofmass/in/polygon.geojson"
        val expected = "measurement/centerofmass/out/polygon.geojson"
        testCenterOfMass<Polygon>(input, expected)
    }

    private inline fun <reified T : Geometry> testCenterOfMass(input: String, expected: String) {
        val feature = Feature.fromJson<T>(readResource(input))
        val expectedGeometry = FeatureCollection.fromJson(readResource(expected)).features.first().getGeometry()
        val centerOfMass = centerOfMass(feature.getGeometry())

        assertEquals(expectedGeometry, centerOfMass)
    }
}
