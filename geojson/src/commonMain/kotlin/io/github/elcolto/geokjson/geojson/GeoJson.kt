package io.github.elcolto.geokjson.geojson

/**
 * A GeoJSON object represents a [Geometry], [Feature], or [collection of Features][FeatureCollection].
 *
 * @property bbox An optional bounding box used to represent the limits of the object's geometry.
 * @property foreignMembers Members not described by specification ("foreign members") MAY be used in a GeoJSON
 * document. **NOTE:** Every custom type, which is added as a value will be represented also as a `Map<String, Any>`.
 * If deserialization of custom types are desired, this hast to be done by consumer.
 */
public sealed interface GeoJson {
    public val bbox: BoundingBox?
    public val foreignMembers: Map<String, Any>

    /**
     * Gets a JSON representation of this object.
     * @return JSON representation
     */
    public fun json(): String
}
