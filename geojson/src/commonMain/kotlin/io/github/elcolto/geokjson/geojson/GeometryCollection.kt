package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.GeometrySerializer
import io.github.elcolto.geokjson.geojson.serialization.foreignMembers
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
import io.github.elcolto.geokjson.geojson.serialization.jsonProp
import io.github.elcolto.geokjson.geojson.serialization.serializeForeignMembers
import io.github.elcolto.geokjson.geojson.serialization.toBbox
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = GeometrySerializer::class)
public data class GeometryCollection @JvmOverloads constructor(
    public val geometries: List<Geometry>,
    override val bbox: BoundingBox? = null,
    override val foreignMembers: Map<String, Any>,
) : Geometry(), Collection<Geometry> by geometries {
    @JvmOverloads
    public constructor(
        vararg geometries: Geometry,
        bbox: BoundingBox? = null,
        foreignMembers: Map<String, Any> = emptyMap(),
    ) : this(geometries.toList(), bbox, foreignMembers)

    override fun json(): String = """{"type":"GeometryCollection",""" +
        bbox.jsonProp() +
        """"geometries":""" +
        geometries.jsonJoin { it.json() } +
        serializeForeignMembers() +
        """}"""

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): GeometryCollection =
            fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): GeometryCollection? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): GeometryCollection {
            require(json.getValue("type").jsonPrimitive.content == "GeometryCollection") {
                "Object \"type\" is not \"GeometryCollection\"."
            }

            val geometries = json.getValue("geometries").jsonArray.map {
                Geometry.fromJson(it.jsonObject)
            }

            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val foreignMembers = json.foreignMembers()

            return GeometryCollection(geometries, bbox, foreignMembers)
        }
    }
}
