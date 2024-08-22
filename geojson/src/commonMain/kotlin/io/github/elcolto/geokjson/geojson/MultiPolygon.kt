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
public data class MultiPolygon @JvmOverloads constructor(
    public val coordinates: List<List<List<Position>>>,
    override val bbox: BoundingBox? = null,
    override val foreignMembers: Map<String, Any> = emptyMap(),
) : Geometry() {

    public val polygons: List<Polygon> = coordinates.map { Polygon(it) }

    @JvmOverloads
    public constructor(
        vararg coordinates: List<List<Position>>,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(coordinates.toList(), bbox, foreignMembers)

    @JvmOverloads
    public constructor(
        coordinates: Array<Array<Array<DoubleArray>>>,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(coordinates.map { ring -> ring.map { it.map(::Position) } }, bbox, foreignMembers)

    override fun json(): String =
        """{"type":"MultiPolygon",${bbox.jsonProp()}"coordinates":${
            coordinates.jsonJoin { polygon ->
                polygon.jsonJoin {
                    it.jsonJoin(
                        transform = Position::json,
                    )
                }
            }
        }${serializeForeignMembers()}}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): MultiPolygon = fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): MultiPolygon? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): MultiPolygon {
            require(json.getValue("type").jsonPrimitive.content == "MultiPolygon") {
                "Object \"type\" is not \"MultiPolygon\"."
            }

            val coords =
                json.getValue("coordinates").jsonArray.map { polygon ->
                    polygon.jsonArray.map { ring -> ring.jsonArray.map { it.jsonArray.toPosition() } }
                }
            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val foreignMembers = json.foreignMembers()

            return MultiPolygon(coords, bbox, foreignMembers)
        }
    }
}
