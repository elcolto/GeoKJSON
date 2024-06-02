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
public class LineString @JvmOverloads constructor(
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

    init {
        require(coordinates.size >= 2) { "LineString must have at least two positions" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LineString

        if (coordinates != other.coordinates) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun json(): String =
        """{"type":"LineString",${bbox.jsonProp()}"coordinates":${coordinates.jsonJoin(transform = Position::json)}}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): LineString = fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): LineString? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): LineString {
            require(json.getValue("type").jsonPrimitive.content == "LineString") {
                "Object \"type\" is not \"LineString\"."
            }

            val coords = json.getValue("coordinates").jsonArray.map { it.jsonArray.toPosition() }
            val bbox = json["bbox"]?.jsonArray?.toBbox()

            return LineString(coords, bbox)
        }
    }
}
