package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.GeoJson
import io.github.elcolto.geokjson.geojson.Position
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

internal fun DoubleArray.jsonJoin(transform: ((Double) -> CharSequence)? = null) =
    joinToString(separator = ",", prefix = "[", postfix = "]", transform = transform)

internal fun <T> Iterable<T>.jsonJoin(transform: ((T) -> CharSequence)? = null) =
    joinToString(separator = ",", prefix = "[", postfix = "]", transform = transform)

internal fun BoundingBox?.jsonProp(): String = if (this == null) "" else """"bbox":${this.json()},"""

internal fun Feature<*>.idProp(): String = if (this.id == null) "" else """"id":"${this.id}","""

internal fun JsonArray.toPosition(): Position =
    Position(this[0].jsonPrimitive.double, this[1].jsonPrimitive.double, this.getOrNull(2)?.jsonPrimitive?.double)

internal fun JsonArray.toBbox(): BoundingBox = BoundingBox(this.map { it.jsonPrimitive.double }.toDoubleArray())

internal fun GeoJson.serializeForeignMembers(): String {
    if (foreignMembers.isEmpty()) return ""
    return mapToJsonRepresentation(this.foreignMembers)
}

private fun mapToJsonRepresentation(
    map: Map<String, Any>,
    prefix: String = ",",
    postfix: String = ""
) = map.entries.joinToString(prefix = prefix, separator = ",", postfix = postfix) { (key, value) ->
    val encodedValue = toJsonRepresentation(value)
    """"$key":$encodedValue"""
}

private fun toJsonRepresentation(value: Any): String {
    value.checkTypeForSerialization()
    val encodedValue = when (value) {
        is String -> Json.encodeToString(String.serializer(), value)
        is Boolean -> Json.encodeToString(Boolean.serializer(), value)
        is Double -> Json.encodeToString(Double.serializer(), value)
        is Int -> Json.encodeToString(Int.serializer(), value)
        is Float -> Json.encodeToString(Float.serializer(), value)
        is Long -> Json.encodeToString(Long.serializer(), value)
        is Enum<*> -> Json.encodeToString(String.serializer(), value.name)
        is Map<*, *> -> {
            value as Map<String, Any>
            mapToJsonRepresentation(value, prefix = "{", postfix = "}")
        }

        is Collection<*> -> value.filterNotNull().joinToString(
            prefix = "[",
            separator = ",",
            postfix = "]"
        ) { element -> toJsonRepresentation(element) }

        else -> error("serializing complex types are not supported at this time")
    }
    return encodedValue
}

private val featureMembers = listOf("type", "geometry", "properties", "id", "bbox")
private val featureCollectionMembers = listOf("type", "features", "bbox")
private val geometryCollectionMembers = listOf("type", "geometries", "bbox")
private val geometryElementMembers = listOf("type", "coordinates", "bbox")

internal fun JsonObject.foreignMembers(): Map<String, Any> {
    val knownMembers = when (this.getValue("type").jsonPrimitive.content) {
        "Feature" -> featureMembers
        "FeatureCollection" -> featureCollectionMembers
        "GeometryCollection" -> geometryCollectionMembers
        else -> geometryElementMembers
    }
    return this.filterKeys { !knownMembers.contains(it) }
        .mapValues { (_, jsonElement) ->
            parseJsonElement(jsonElement)
        }
        .filterValues { it != null }
        .mapValues { (key, value) -> requireNotNull(value) }
}

/**
 * Recursively parse [jsonElement]. Result will be a primitive, a (recursive) [Map] of [String] to [Any], complex types
 * or a [List] of primitives or [Map]s
 */
private fun parseJsonElement(jsonElement: JsonElement): Any? = when (jsonElement) {
    is JsonArray -> jsonElement.jsonArray.map { element -> parseJsonElement(element) }
    is JsonObject -> jsonElement.jsonObject.entries.associate { (key, value) -> key to parseJsonElement(value) } //convert entry to Map<String, Any>
    is JsonPrimitive -> {
        val primitive = jsonElement.jsonPrimitive
        when {
            primitive.isString -> primitive.content
            primitive.booleanOrNull != null -> primitive.boolean
            primitive.intOrNull != null -> primitive.int
            primitive.longOrNull != null -> primitive.long
            primitive.floatOrNull != null -> primitive.float
            primitive.doubleOrNull != null -> primitive.double
            else -> null
        }
    }

    JsonNull -> null
}

internal fun Any.isPrimitive(): Boolean = when (this) {
    is String,
    is Boolean,
    is Double,
    is Int,
    is Float,
    is Long -> true

    else -> false
}

internal fun Any.checkTypeForSerialization() {
    if (isPrimitive()) return

    when (this) {
        is Array<*> -> filterNotNull().forEach {
            it.checkTypeForSerialization()
        }

        is Collection<*> -> filterNotNull().forEach {
            it.checkTypeForSerialization()
        }

        is Map<*, *> -> {
            if (keys.any { it !is String })
                error("Only type String as key in Map is supported")

            values.filterNotNull().forEach { it.checkTypeForSerialization() }
        }

        else -> error("Type ${this::class.simpleName} is not applicable for serialization")
    }
}

