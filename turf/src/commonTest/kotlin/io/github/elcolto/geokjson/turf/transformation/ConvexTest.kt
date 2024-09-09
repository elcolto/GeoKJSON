package io.github.elcolto.geokjson.turf.transformation

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.asInstance
import io.github.elcolto.geokjson.turf.coordAll
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalTurfApi::class)
class ConvexTest {

    @Test
    fun testConvexElevation1() {
        val input = "transformation/convex/in/elevation1.geojson"
        val expected = "transformation/convex/out/elevation1.geojson"
        testConvex(input, expected)
    }

    @Test
    fun testConvexElevation2() {
        val input = "transformation/convex/in/elevation2.geojson"
        val expected = "transformation/convex/out/elevation2.geojson"
        testConvex(input, expected)
    }

    @Test
    fun testConvexElevation3() {
        val input = "transformation/convex/in/elevation3.geojson"
        val expected = "transformation/convex/out/elevation3.geojson"
        testConvex(input, expected)
    }

    @Test
    fun testConvexElevation4() {
        val input = "transformation/convex/in/elevation4.geojson"
        val expected = "transformation/convex/out/elevation4.geojson"
        testConvex(input, expected)
    }

    @Test
    fun testConvexElevation5() {
        val input = "transformation/convex/in/elevation5.geojson"
        val expected = "transformation/convex/out/elevation5.geojson"
        testConvex(input, expected)
    }

    private fun testConvex(input: String, expected: String) {
        val fc = FeatureCollection.fromJson(readResource(input))
        val expectedGeometry = FeatureCollection.fromJson(readResource(expected)).features.last().getGeometry()
        val geometryCollection = fc.features.map { it.getGeometry() }.let { GeometryCollection(it) }
        val polygon = convex(geometryCollection)

        val actual = polygon?.coordAll()?.distinct()?.sortedBy { it.longitude }
        val expectedCoordinates = expectedGeometry.coordAll().distinct().sortedBy { it.longitude }

        assertContentEquals(expectedCoordinates, actual) // check coordinates are the same
        assertEquals(expectedGeometry.asInstance()!!, polygon)
    }
}
