package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.dsl.geometryCollection
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertDoubleEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test

@ExperimentalTurfApi
class AreaTest {

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
}
