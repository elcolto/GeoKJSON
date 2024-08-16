package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class FeatureCollectionSerializationTests {

    @Test
    fun testSerializeFeatureCollection() {
        val geometry = Point(Position(12.3, 45.6))
        val feature = Feature(
            geometry,
            mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
        )
        val collection = FeatureCollection(feature, feature)

        val json =
            """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":
                |[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}},{"type":"Feature","geometry":{"type":"Point",
                |"coordinates":[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}}]}"""
                .trimMargin()
                .replace("\n", "")

        assertEquals(json, collection.json(), "FeatureCollection (fast)")
        assertEquals(json, Json.encodeToString(collection), "FeatureCollection (kotlinx)")
    }

    @Test
    fun testSerializePolymorphicFeatureCollection() {
        val position = Position(12.3, 45.6)
        val point = Point(position)
        val featurePoint = Feature(
            point,
            mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
        )
        val line = LineString(coordinates = listOf(position, Position(position.component2(), position.component1())))
        val featureLine = Feature(line)
        val collection = FeatureCollection(featurePoint, featureLine)
        val json =
            """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":
            |[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}},{"type":"Feature","geometry":{"type":
            |"LineString","coordinates":[[12.3,45.6],[45.6,12.3]]},"properties":{}}]}"""
                .trimMargin()
                .replace("\n", "")
        assertEquals(json, collection.json(), "FeatureCollection (fast)")
        assertEquals(json, Json.encodeToString(collection), "FeatureCollection (kotlinx)")
    }

    @Test
    fun testDeserializeFeatureCollection() {
        val geometry = Point(Position(12.3, 45.6))
        val feature = Feature(
            geometry,
            properties = mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
        )
        val collection = FeatureCollection(feature, feature)

        assertEquals(
            collection,
            FeatureCollection.fromJson(
                """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":
                |[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}},{"type":"Feature","geometry":{"type":"Point",
                |"coordinates":[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}}]}"""
                    .trimMargin()
                    .replace("\n", ""),
            ),
        )
    }

    @Test
    fun testDeserializePolymorphicFeatureCollection() {
        val position = Position(12.3, 45.6)
        val point = Point(position)
        val featurePoint = Feature(
            geometry = point,
            properties = mapOf(
                "size" to 45.1,
                "name" to "Nowhere",
            ),
        )
        val line = LineString(coordinates = listOf(position, Position(position.component2(), position.component1())))
        val featureLine = Feature(line)
        val collection = FeatureCollection(featurePoint, featureLine)
        assertEquals(
            collection,
            FeatureCollection.fromJson(
                """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":
                |[12.3,45.6]},"properties":{"size":45.1,"name":"Nowhere"}},{"type":"Feature","geometry":{"type":
                |"LineString","coordinates":[[12.3,45.6],[45.6,12.3]]},"properties":{}}]}"""
                    .trimMargin()
                    .replace("\n", ""),
            ),
        )
    }
}
