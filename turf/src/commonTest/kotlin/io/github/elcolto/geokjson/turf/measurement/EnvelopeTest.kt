package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.geojson.dsl.featureCollection
import io.github.elcolto.geokjson.geojson.dsl.lineString
import io.github.elcolto.geokjson.geojson.dsl.point
import io.github.elcolto.geokjson.geojson.dsl.polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalTurfApi
class EnvelopeTest {
    @Test
    fun envelopeProcessesFeatureCollection() {
        val fc = featureCollection {
            feature(
                geometry = point(102.0, 0.5),
            )
            feature(
                geometry = lineString {
                    point(102.0, -10.0)
                    point(103.0, 1.0)
                    point(104.0, 0.0)
                    point(130.0, 4.0)
                },
            )
            feature(
                geometry = polygon {
                    ring {
                        point(102.0, -10.0)
                        point(103.0, 1.0)
                        point(104.0, 0.0)
                        point(130.0, 4.0)
                        point(20.0, 0.0)
                        point(101.0, 0.0)
                        point(101.0, 1.0)
                        point(100.0, 1.0)
                        point(100.0, 0.0)
                    }
                },
            )
        }

        val polygon = envelope(fc)

        assertIs<Polygon>(polygon, "geometry type should be Polygon")
        assertEquals(
            listOf(
                listOf(
                    Position(20.0, -10.0),
                    Position(130.0, -10.0),
                    Position(130.0, 4.0),
                    Position(20.0, 4.0),
                    Position(20.0, -10.0),
                ),
            ),
            polygon.coordinates,
            "positions should be correct",
        )
    }
}
