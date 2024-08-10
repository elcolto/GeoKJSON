package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.FeatureSerializer
import io.github.elcolto.geokjson.geojson.serialization.foreignMembers
import io.github.elcolto.geokjson.geojson.serialization.jsonProp
import io.github.elcolto.geokjson.geojson.serialization.parseJsonElement
import io.github.elcolto.geokjson.geojson.serialization.propertyMapToJson
import io.github.elcolto.geokjson.geojson.serialization.serializeForeignMembers
import io.github.elcolto.geokjson.geojson.serialization.toBbox
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * A feature object represents a spatially bounded thing.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7946#section-3.2">https://tools.ietf.org/html/rfc7946#section-3.2</a>
 *
 * @property geometry A [Geometry] object contained within the feature.
 * @property properties Additional properties about this feature.
 * When serialized, any non-simple types will be serialized into JSON objects.
 * @property id An optionally included string that commonly identifies this feature.
 */
@Suppress("TooManyFunctions")
@Serializable(with = FeatureSerializer::class)
public data class Feature<out T : Geometry>(
    public val geometry: T?,
    public val properties: Map<String, Any> = emptyMap(),
    public val id: String? = null,
    override val bbox: BoundingBox? = null,
    override val foreignMembers: Map<String, Any> = emptyMap()
) : GeoJson {

    /**
     * Gets the value of the property with the given [key].
     *
     * @param key The string key for the property
     * @return The value of the property cast to [T]?, or null if the key had no value.
     */
    @JvmName("getPropertyCast")
    public inline fun <reified T : Any?> getProperty(key: String): T? = properties[key] as T?

    override fun toString(): String = json()

    private fun idProp(): String = if (this.id == null) "" else ""","id":"${this.id}""""

    override fun json(): String =
        """{"type":"Feature",${bbox.jsonProp()}"geometry":${geometry?.json()}${idProp()}${
            ",\"properties\":${propertyMapToJson(properties, prefix = "{", postfix = "}")}"
        }${serializeForeignMembers()}}"""


    public companion object {
        @JvmStatic
        public fun <T : Geometry> fromJson(json: String): Feature<T> =
            fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun <T : Geometry> fromJsonOrNull(json: String): Feature<T>? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun <T : Geometry> fromJson(json: JsonObject): Feature<T> {
            require(json.getValue("type").jsonPrimitive.content == "Feature") {
                "Object \"type\" is not \"Feature\"."
            }

            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val id = json["id"]?.jsonPrimitive?.content

            val geom = json["geometry"]?.jsonObject
            val geometry: T? = if (geom != null) Geometry.fromJson(geom) as T else null

            val properties: Map<String, Any?> = json["properties"]?.jsonObject.orEmpty()
                .mapValues { (_, jsonElement) -> parseJsonElement(jsonElement) }
                .filterValues { it != null }

            return Feature(
                geometry,
                properties as Map<String, Any>,
                id,
                bbox,
                json.foreignMembers()
            )
        }
    }
}
