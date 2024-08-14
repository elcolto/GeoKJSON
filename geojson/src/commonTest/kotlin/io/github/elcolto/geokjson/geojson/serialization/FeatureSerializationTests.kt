package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class FeatureSerializationTests {

    @Test
    fun testSerializeFeature() {
        val geometry = Point(Position(12.3, 45.6))
        val feature = Feature(
            geometry,
            mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
            "001",
            BoundingBox(11.6, 45.1, 12.7, 45.7),
        )

        val json =
            """{"type":"Feature","bbox":[11.6,45.1,12.7,45.7],"geometry":{"type":"Point","coordinates":[12.3,45.6]},
                |"id":"001","properties":{"size":45.1,"name":"Nowhere"}}
            """.trimMargin().replace("\n", "")

        assertEquals(json, feature.json(), "Feature (fast)")
        assertEquals(json, Json.encodeToString(feature), "Feature (kotlinx)")
    }

    @Test
    fun testDeserializeFeature() {
        val geometry = Point(Position(12.3, 45.6))
        val feature = Feature(
            geometry,
            properties = mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
            id = "001",
            bbox = BoundingBox(11.6, 45.1, 12.7, 45.7),
        )

        val fromJson = Feature.fromJson<Point>(
            """{"type":"Feature",
                |"bbox":[11.6,45.1,12.7,45.7],
                |"geometry":{
                    |"type":"Point",
                    |"coordinates":[12.3,45.6]},
                |"id":"001",
                |"properties":{
                    |"size":45.1,
                    |"name":"Nowhere"
                |}}
            """.trimMargin().replace("\n", ""),
        )
        assertEquals(
            feature,
            fromJson,
        )
    }
}
