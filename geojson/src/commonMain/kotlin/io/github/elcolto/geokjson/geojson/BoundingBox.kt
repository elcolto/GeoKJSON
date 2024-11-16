package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.BoundingBoxSerializer
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
import kotlinx.serialization.Serializable

/**
 * Represents an area bounded by a [northeast] and [southwest] [Position].
 *
 * A GeoJSON object MAY have a member named "bbox" to include information on the coordinate range for its Geometries,
 * Features, or FeatureCollections.
 *
 * When serialized, a BoundingBox is represented as an array of length 2*n where n is the number of dimensions
 * represented in the contained geometries, with all axes of the most southwesterly point followed by all axes
 * of the northeasterly point. The axes order of a BoundingBox follow the axes order of geometries.
 *
 * For the BoundingBox to be serialized in 3D form, both Positions must have a defined altitude.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7946#section-5">https://tools.ietf.org/html/rfc7946#section-5</a>
 *
 * @property northeast The northeastern corner of the BoundingBox
 * @property southwest The southwestern corner of the BoundingBox
 * @property coordinates The GeoJSON bounding box coordinate array
 */
@Serializable(with = BoundingBoxSerializer::class)
@Suppress("MagicNumber")
public class BoundingBox constructor(public val coordinates: DoubleArray) {
    init {
        require(coordinates.size == 4 || coordinates.size == 6) {
            "Bounding Box coordinates must either have 4 or 6 values"
        }
    }

    public constructor(west: Double, south: Double, east: Double, north: Double) : this(
        doubleArrayOf(west, south, east, north),
    )

    public constructor(coordinates: List<Double>) : this(coordinates.toDoubleArray())

    public constructor(
        west: Double,
        south: Double,
        minAltitude: Double,
        east: Double,
        north: Double,
        maxAltitude: Double,
    ) : this(doubleArrayOf(west, south, minAltitude, east, north, maxAltitude))

    public constructor(southwest: Position, northeast: Position) : this(
        when (southwest.hasAltitude && northeast.hasAltitude) {
            true -> southwest.coordinates + northeast.coordinates
            false -> doubleArrayOf(southwest.longitude, southwest.latitude, northeast.longitude, northeast.latitude)
        },
    )

    public val southwest: Position
        get() = when (hasAltitude) {
            true -> Position(coordinates[0], coordinates[1], coordinates[2])
            false -> Position(coordinates[0], coordinates[1])
        }

    public val northeast: Position
        get() = when (hasAltitude) {
            true -> Position(coordinates[3], coordinates[4], coordinates[5])
            false -> Position(coordinates[2], coordinates[3])
        }

    public operator fun component1(): Position = southwest
    public operator fun component2(): Position = northeast

    public operator fun get(index: Int): Double {
        require(index in coordinates.indices)
        return coordinates[index]
    }

    public operator fun contains(position: Position): Boolean =
        position.longitude >= southwest.longitude && position.longitude <= northeast.longitude &&
            position.latitude >= southwest.latitude && position.latitude <= northeast.latitude

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BoundingBox

        return coordinates.contentEquals(other.coordinates)
    }

    override fun hashCode(): Int {
        return coordinates.contentHashCode()
    }

    override fun toString(): String {
        return "BoundingBox(southwest=$southwest, northeast=$northeast)"
    }

    public fun json(): String = coordinates.jsonJoin()
}

@Suppress("MagicNumber")
public val BoundingBox.hasAltitude: Boolean
    get() = coordinates.size == 6
