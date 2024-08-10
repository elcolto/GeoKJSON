package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.serialization.BoundingBoxSerializer.toJsonArray
import io.github.elcolto.geokjson.geojson.serialization.GeometrySerializer.toJsonObject
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

internal class FeatureSerializer<T : Geometry> : JsonSerializer<Feature<T>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Feature")

    override fun deserialize(input: JsonDecoder): Feature<T> = Feature.fromJson(input.decodeJsonElement().jsonObject)

    override fun serialize(output: JsonEncoder, value: Feature<T>) {
        output.encodeJsonElement(value.toJsonObject())
    }
}

internal fun <T : Geometry> Feature<T>.toJsonObject(): JsonObject = buildJsonObject {
    put("type", "Feature")
    bbox?.let { put("bbox", it.toJsonArray()) }
    geometry?.let { put("geometry", it.toJsonObject()) }
    id?.let { put("id", it) }

    put("properties", JsonObject(properties.mapValues { (_, value) -> anyToJsonElement(value) }))
    foreignMembers.forEach { (key, value) -> put(key, Json.encodeToJsonElement(value)) }
}

private fun anyToJsonElement(element: Any): JsonElement = when (element) {
    is String -> JsonPrimitive(element)
    is Boolean -> JsonPrimitive(element)
    is Double -> JsonPrimitive(element)
    is Int -> JsonPrimitive(element)
    is Float -> JsonPrimitive(element)
    is Long -> JsonPrimitive(element)
    is Enum<*> -> JsonPrimitive(element.name)
    is Map<*, *> -> {
        element as Map<String, Any?>
        val entries: Map<String, JsonElement> = element.mapValues { (_, value) ->
            value?.let { anyToJsonElement(it) } ?: JsonNull
        }
        JsonObject(entries)
    }

    is Collection<*> -> {
        JsonArray(element.map { value -> value?.let { anyToJsonElement(it) } ?: JsonNull })
    }

    is Array<*> -> anyToJsonElement(element.toList())
    is BooleanArray -> anyToJsonElement(element.toList())
    is IntArray -> anyToJsonElement(element.toList())
    is LongArray -> anyToJsonElement(element.toList())
    is FloatArray -> anyToJsonElement(element.toList())
    is DoubleArray -> anyToJsonElement(element.toList())

    else -> error("${element::class.simpleName} is not applicable to be serialized")
}
