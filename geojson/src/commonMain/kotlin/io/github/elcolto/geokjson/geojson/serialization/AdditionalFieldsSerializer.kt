package io.github.elcolto.geokjson.geojson.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder

internal object AdditionalFieldsSerializer : KSerializer<Map<String, Any>> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("kotlin.Map", StructureKind.MAP)

    override fun deserialize(decoder: Decoder): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        encoder as? JsonEncoder ?: throw SerializationException("This class can only be saved as JSON")
        val json = value.entries.joinToString(separator = ",") { (key, value) ->
            val encodedValue = encodeAny(value)
            "$key:$encodedValue"
        }
        encoder.encodeString(json) // TODO better use encodeJsonElement
    }

    private fun encodeAny(obj: Any?): String {
        val encodedValue: String = when (obj) {
            is String -> Json.encodeToString(String.serializer(), obj)
            is Boolean -> Json.encodeToString(Boolean.serializer(), obj)
            is Double -> Json.encodeToString(Double.serializer(), obj)
            is Int -> Json.encodeToString(Int.serializer(), obj)
            is Float -> Json.encodeToString(Float.serializer(), obj)
            is Long -> Json.encodeToString(Long.serializer(), obj)
            is Map<*, *> -> {
                obj.checkTypeForSerialization()

                obj.mapValues { (_, value) -> encodeAny(value) }.filterKeys { it != null }.toList()
                    .joinToString(prefix = "{", separator = ",", postfix = "}") { (key, value) ->
                        ("\"" + (key as String) + "\":" + value)
                    }
            }

            else -> {
                error("serializing complex types are not supported at this time")
            }
        }
        return encodedValue
    }
}
