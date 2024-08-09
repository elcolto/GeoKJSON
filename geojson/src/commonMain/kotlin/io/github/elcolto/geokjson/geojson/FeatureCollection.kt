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
public data class FeatureCollection(
    public val features: List<Feature<Geometry>> = emptyList(),
    override val bbox: BoundingBox? = null,
) : Collection<Feature<Geometry>> by features, GeoJson {

    public constructor(vararg features: Feature<Geometry>, bbox: BoundingBox? = null) : this(
        features.toMutableList(),
        bbox,
    )

    override fun toString(): String = json()

    override fun json(): String =
        """{"type":"FeatureCollection",${bbox.jsonProp()}"features":${features.jsonJoin { it.json() }}}"""

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
            val features = json.getValue("features").jsonArray
                .map { Feature.fromJson<Geometry>(it.jsonObject) }

            return FeatureCollection(features, bbox)
        }
    }
}
