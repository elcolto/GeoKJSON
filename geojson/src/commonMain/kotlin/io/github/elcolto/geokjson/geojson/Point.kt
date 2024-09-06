package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.GeometrySerializer
import io.github.elcolto.geokjson.geojson.serialization.foreignMembers
import io.github.elcolto.geokjson.geojson.serialization.jsonProp
import io.github.elcolto.geokjson.geojson.serialization.serializeForeignMembers
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
public data class Point @JvmOverloads constructor(
    public val coordinates: Position,
    override val bbox: BoundingBox? = null,
    override val foreignMembers: Map<String, Any> = emptyMap(),
) : Geometry() {
    @JvmOverloads
    public constructor(
        coordinates: DoubleArray,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(Position(coordinates), bbox, foreignMembers)

    override fun json(): String =
        """{"type":"Point",${bbox.jsonProp()}"coordinates":${coordinates.json()}${serializeForeignMembers()}}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): Point = fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): Point? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        internal fun fromJson(json: JsonObject): Point {
            require(json.getValue("type").jsonPrimitive.content == "Point") {
                "Object \"type\" is not \"Point\"."
            }

            val coords = json.getValue("coordinates").jsonArray.toPosition()
            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val foreignMembers = json.foreignMembers()

            return Point(coords, bbox, foreignMembers)
        }
    }
}
