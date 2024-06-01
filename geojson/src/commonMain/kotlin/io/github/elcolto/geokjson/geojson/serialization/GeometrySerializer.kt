package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.geojson.serialization.BoundingBoxSerializer.toJsonArray
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

@Suppress("LongMethod")
public object GeometrySerializer : KSerializer<Geometry> {
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("Geometry", PolymorphicKind.SEALED)

    override fun deserialize(decoder: Decoder): Geometry {
        decoder as? JsonDecoder ?: throw SerializationException("This class can only be loaded from JSON")

        return Geometry.fromJson(decoder.decodeJsonElement().jsonObject)
    }

    override fun serialize(encoder: Encoder, value: Geometry) {
        encoder as? JsonEncoder ?: throw SerializationException("This class can only be saved as JSON")

        encoder.encodeJsonElement(value.toJsonObject())
    }

    @Suppress("ComplexMethod")
    internal fun Geometry.toJsonObject(): JsonObject = buildJsonObject {
        when (this@toJsonObject) {
            is Point -> {
                put("type", JsonPrimitive("Point"))
                put("coordinates", coordinates.toJsonArray())
            }
            is MultiPoint -> {
                put("type", JsonPrimitive("MultiPoint"))
                put(
                    "coordinates",
                    buildJsonArray {
                        coordinates.forEach { position -> add(position.toJsonArray()) }
                    },
                )
            }
            is LineString -> {
                put("type", JsonPrimitive("LineString"))
                put(
                    "coordinates",
                    buildJsonArray {
                        coordinates.forEach { position -> add(position.toJsonArray()) }
                    },
                )
            }
            is MultiLineString -> {
                put("type", JsonPrimitive("MultiLineString"))
                put(
                    "coordinates",
                    buildJsonArray {
                        coordinates.forEach { line ->
                            add(
                                buildJsonArray {
                                    line.forEach { position -> add(position.toJsonArray()) }
                                },
                            )
                        }
                    },
                )
            }
            is Polygon -> {
                put("type", JsonPrimitive("Polygon"))
                put(
                    "coordinates",
                    buildJsonArray {
                        coordinates.forEach { ring ->
                            add(
                                buildJsonArray {
                                    ring.forEach { position -> add(position.toJsonArray()) }
                                },
                            )
                        }
                    },
                )
            }
            is MultiPolygon -> {
                put("type", JsonPrimitive("MultiPolygon"))
                put(
                    "coordinates",
                    buildJsonArray {
                        coordinates.forEach { polygon ->
                            add(
                                buildJsonArray {
                                    polygon.forEach { ring ->
                                        add(
                                            buildJsonArray {
                                                ring.forEach { position -> add(position.toJsonArray()) }
                                            },
                                        )
                                    }
                                },
                            )
                        }
                    },
                )
            }
            is GeometryCollection -> {
                put("type", JsonPrimitive("GeometryCollection"))
                put(
                    "geometries",
                    buildJsonArray {
                        geometries.forEach {
                            add(it.toJsonObject())
                        }
                    },
                )
            }
        }

        bbox?.let { put("bbox", it.toJsonArray()) }
    }

    private fun Position.toJsonArray(): JsonArray = buildJsonArray {
        add(JsonPrimitive(longitude))
        add(JsonPrimitive(latitude))
        altitude?.let { add(JsonPrimitive(it)) }
    }
}
