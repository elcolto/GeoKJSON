package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.GeometrySerializer
import io.github.elcolto.geokjson.geojson.serialization.foreignMembers
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
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
public data class MultiLineString @JvmOverloads constructor(
    public val coordinates: List<List<Position>>,
    override val bbox: BoundingBox? = null,
    override val foreignMembers: Map<String, Any> = emptyMap(),
) : Geometry() {
    @JvmOverloads
    public constructor(
        vararg coordinates: List<Position>,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(coordinates.toList(), bbox, foreignMembers)

    @JvmOverloads
    public constructor(
        coordinates: Array<Array<DoubleArray>>,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(
        coordinates.map { it.map(::Position) },
        bbox,
        foreignMembers,
    )

    init {
        coordinates.forEach { line ->
            require(line.size >= 2) { "LineString must have at least two positions" }
        }
    }

    override fun json(): String = """{"type":"MultiLineString",${bbox.jsonProp()}"coordinates":${coordinates.jsonJoin {
        it.jsonJoin(
            transform = Position::json,
        )
    }}${serializeForeignMembers()}}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): MultiLineString =
            fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): MultiLineString? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): MultiLineString {
            require(json.getValue("type").jsonPrimitive.content == "MultiLineString") {
                "Object \"type\" is not \"MultiLineString\"."
            }

            val coords =
                json.getValue("coordinates").jsonArray.map { line -> line.jsonArray.map { it.jsonArray.toPosition() } }
            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val foreignMembers = json.foreignMembers()

            return MultiLineString(coords, bbox, foreignMembers)
        }
    }
}
