package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.GeometrySerializer
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
import io.github.elcolto.geokjson.geojson.serialization.jsonProp
import io.github.elcolto.geokjson.geojson.serialization.toBbox
import io.github.elcolto.geokjson.geojson.serialization.toPosition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = GeometrySerializer::class)
public data class MultiPoint @JvmOverloads constructor(
    public val coordinates: List<Position>,
    override val bbox: BoundingBox? = null,
) : Geometry() {
    @JvmOverloads
    public constructor(vararg coordinates: Position, bbox: BoundingBox? = null) : this(coordinates.toList(), bbox)

    @JvmOverloads
    public constructor(
        coordinates: Array<DoubleArray>,
        bbox: BoundingBox? = null,
    ) : this(coordinates.map(::Position), bbox)

    override fun json(): String =
        """{"type":"MultiPoint",${bbox.jsonProp()}"coordinates":${coordinates.jsonJoin(transform = Position::json)}}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): MultiPoint = fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): MultiPoint? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): MultiPoint {
            require(json.getValue("type").jsonPrimitive.content == "MultiPoint") {
                "Object \"type\" is not \"MultiPoint\"."
            }

            val coords = json.getValue("coordinates").jsonArray.map { it.jsonArray.toPosition() }
            val bbox = json["bbox"]?.jsonArray?.toBbox()

            return MultiPoint(coords, bbox)
        }
    }
}
