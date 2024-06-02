package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.FeatureCollectionSerializer
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
import io.github.elcolto.geokjson.geojson.serialization.jsonProp
import io.github.elcolto.geokjson.geojson.serialization.toBbox
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.JvmStatic

/**
 * A FeatureCollection object is a collection of [Feature] objects.
 * This class implements the [Collection] interface and can be used as a Collection directly.
 * The list of features contained in this collection are also accessible through the [features] property.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7946#section-3.3">https://tools.ietf.org/html/rfc7946#section-3.3</a>
 *
 * @property features The collection of [Feature] objects stored in this collection
 */
@Serializable(with = FeatureCollectionSerializer::class)
public class FeatureCollection(
    public val features: List<Feature> = emptyList(),
    override val bbox: BoundingBox? = null,
) : Collection<Feature> by features, GeoJson {

    public constructor(vararg features: Feature, bbox: BoundingBox? = null) : this(features.toMutableList(), bbox)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FeatureCollection

        if (features != other.features) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = features.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = json()

    override fun json(): String =
        """{"type":"FeatureCollection",${bbox.jsonProp()}"features":${features.jsonJoin { it.json() }}}"""

    public operator fun component1(): List<Feature> = features
    public operator fun component2(): BoundingBox? = bbox

    public companion object {
        @JvmStatic
        public fun fromJson(json: String): FeatureCollection =
            fromJson(Json.decodeFromString(JsonObject.serializer(), json))

        @JvmStatic
        public fun fromJsonOrNull(json: String): FeatureCollection? = try {
            fromJson(json)
        } catch (_: Exception) {
            null
        }

        @JvmStatic
        public fun fromJson(json: JsonObject): FeatureCollection {
            require(json.getValue("type").jsonPrimitive.content == "FeatureCollection") {
                "Object \"type\" is not \"FeatureCollection\"."
            }

            val bbox = json["bbox"]?.jsonArray?.toBbox()
            val features = json.getValue("features").jsonArray.map { Feature.fromJson(it.jsonObject) }

            return FeatureCollection(features, bbox)
        }
    }
}
