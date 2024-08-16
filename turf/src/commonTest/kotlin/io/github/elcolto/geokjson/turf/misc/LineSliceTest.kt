package io.github.elcolto.geokjson.turf.misc

import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.utils.assertPositionEquals
import io.github.elcolto.geokjson.turf.utils.readResource
import kotlin.test.Test

@ExperimentalTurfApi
class LineSliceTest {

    @Test
    fun testLineSlice() {
        val features = FeatureCollection.fromJson(readResource("misc/lineSlice/route.json"))
        val slice = LineString.fromJson(readResource("misc/lineSlice/slice.json"))

        val (lineString, start, stop) = features.features

        val result = lineSlice(
            (start.geometry as Point).coordinates,
            (stop.geometry as Point).coordinates,
            lineString.geometry as LineString,
        )
        slice.coordinates.forEachIndexed { i, position ->
            assertPositionEquals(position, result.coordinates[i])
        }
    }
}
