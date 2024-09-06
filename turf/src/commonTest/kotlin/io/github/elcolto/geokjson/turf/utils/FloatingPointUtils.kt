package io.github.elcolto.geokjson.turf.utils

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.asserter

fun assertDoubleEquals(expected: Double, actual: Double?, epsilon: Double, message: String? = null) {
    asserter.assertNotNull(null, actual)
    asserter.assertTrue(
        { (message ?: "") + "Expected <$expected>, actual <$actual>, should differ no more than <$epsilon>." },
        abs(expected - actual!!) <= epsilon,
    )
}

fun assertPositionEquals(expected: Position, actual: Position?, epsilon: Double = 0.0001, message: String? = null) {
    asserter.assertNotNull(null, actual)

    assertDoubleEquals(expected.latitude, actual?.latitude, epsilon, message)
    assertDoubleEquals(expected.longitude, actual?.longitude, epsilon, message)
}

@OptIn(ExperimentalTurfApi::class)
fun assertGeometryEquals(expected: Geometry, actual: Geometry, epsilon: Double = 0.0001, message: String? = null) {
    assertEquals(expected::class, actual::class, message)
    assertEquals(expected.coordAll().size, actual.coordAll().size, message)
    expected.coordAll().forEachIndexed { index, position ->
        assertPositionEquals(
            position,
            actual.coordAll()[index],
            epsilon,
            message,
        )
    }
}
