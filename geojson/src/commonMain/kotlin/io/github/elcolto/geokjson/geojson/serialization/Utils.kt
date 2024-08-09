package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Position
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive

internal fun DoubleArray.jsonJoin(transform: ((Double) -> CharSequence)? = null) =
    joinToString(separator = ",", prefix = "[", postfix = "]", transform = transform)

internal fun <T> Iterable<T>.jsonJoin(transform: ((T) -> CharSequence)? = null) =
    joinToString(separator = ",", prefix = "[", postfix = "]", transform = transform)

internal fun BoundingBox?.jsonProp(): String = if (this == null) "" else """"bbox":${this.json()},"""

internal fun Feature<*>.idProp(): String = if (this.id == null) "" else """"id":"${this.id}","""

internal fun JsonArray.toPosition(): Position =
    Position(this[0].jsonPrimitive.double, this[1].jsonPrimitive.double, this.getOrNull(2)?.jsonPrimitive?.double)

internal fun JsonArray.toBbox(): BoundingBox = BoundingBox(this.map { it.jsonPrimitive.double }.toDoubleArray())
