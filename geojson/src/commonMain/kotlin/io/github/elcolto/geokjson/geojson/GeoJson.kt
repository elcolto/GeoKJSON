package io.github.elcolto.geokjson.geojson

/**
 * A GeoJSON object represents a [Geometry], [Feature], or [collection of Features][FeatureCollection].
 *
 * @property bbox An optional bounding box used to represent the limits of the object's geometry.
 */
public interface GeoJson {
    public val bbox: BoundingBox?

    /**
     * Gets a JSON representation of this object.
     * @return JSON representation
     */
    public fun json(): String
}
