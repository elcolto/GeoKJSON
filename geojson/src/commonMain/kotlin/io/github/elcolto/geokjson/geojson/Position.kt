package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.serialization.PositionSerializer
import io.github.elcolto.geokjson.geojson.serialization.jsonJoin
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmSynthetic

/**
 * A position is the fundamental geometry construct. Positions are represented by [Position]s in Spatial K
 *
 * In JSON, a position is an array of numbers. There MUST be two or more elements.
 * The first two elements are [longitude] and [latitude], or easting and northing,
 * precisely in that order using decimal numbers.
 * [Altitude][altitude] or elevation MAY be included as an optional third element.
 *
 * When serialized, the [latitude], [longitude], and [altitude] (if present) will be represented as an array.
 *
 * ```kotlin
 * LngLat(longitude = -75.0, latitude = 45.0)
 * ```
 * will be serialized as
 * ```json
 * [-75.0,45.0]
 * ```
 *
 * @see <a href="https://tools.ietf.org/html/rfc7946#section-3.1.1">
 *     https://tools.ietf.org/html/rfc7946#section-3.1.1</a>
 * @see PositionSerializer
 *
 * @property latitude The latitude value of this position (or northing value for projected coordinates)
 * @property longitude The longitude value of this position (or easting value for projected coordinates)
 * @property altitude Optionally, an altitude or elevation for this position
 */
@Serializable(with = PositionSerializer::class)
public class Position(public val coordinates: DoubleArray) {
    init {
        require(coordinates.size >= 2) { "At least two coordinates must be provided" }
    }

    public constructor(longitude: Double, latitude: Double) : this(doubleArrayOf(longitude, latitude))
    public constructor(longitude: Double, latitude: Double, altitude: Double) : this(
        doubleArrayOf(
            longitude,
            latitude,
            altitude
        )
    )

    public constructor(longitude: Double, latitude: Double, altitude: Double?) : this(
        when (altitude) {
            null -> doubleArrayOf(longitude, latitude)
            else -> doubleArrayOf(longitude, latitude, altitude)
        }
    )

    public val longitude: Double
        get() = coordinates[0]
    public val latitude: Double
        get() = coordinates[1]
    public val altitude: Double?
        get() = coordinates.getOrNull(2)

    /**
     * Component function for getting the [longitude]
     * @return [longitude]
     */
    @JvmSynthetic
    public operator fun component1(): Double = longitude

    /**
     * Component function for getting the [latitude]
     * @return [latitude]
     */
    @JvmSynthetic
    public operator fun component2(): Double = latitude

    /**
     * Component function for getting the [altitude]
     * @return [altitude]
     */
    @JvmSynthetic
    public operator fun component3(): Double? = altitude

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Position

        if (!coordinates.contentEquals(other.coordinates)) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.contentHashCode()
    }

    override fun toString(): String {
        return "LngLat(longitude=$longitude, latitude=$latitude, altitude=$altitude)"
    }

    public fun json(): String = coordinates.jsonJoin()
}

@Suppress("MagicNumber")
public val Position.hasAltitude: Boolean get() = coordinates.size == 3
