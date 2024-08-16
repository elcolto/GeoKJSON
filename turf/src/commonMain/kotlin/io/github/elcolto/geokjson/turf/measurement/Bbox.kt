@file:Suppress("TooManyFunctions")

package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.LineString
import io.github.elcolto.geokjson.geojson.MultiLineString
import io.github.elcolto.geokjson.geojson.MultiPoint
import io.github.elcolto.geokjson.geojson.MultiPolygon
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll
import kotlin.jvm.JvmSynthetic

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: Geometry): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: Point): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: MultiPoint): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: LineString): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: MultiLineString): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: Polygon): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a geometry and calculates the bbox of all input features, and returns a bounding box.
 *
 * @param geometry The geometry to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(geometry: MultiPolygon): BoundingBox = computeBbox(geometry.coordAll())

/**
 * Takes a feature and calculates the bbox of the feature's geometry, and returns a bounding box.
 *
 * @param feature The feature to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(feature: Feature<Geometry>): BoundingBox = computeBbox(feature.coordAll() ?: emptyList())

/**
 * Takes a feature collection and calculates a bbox that covers all features in the collection.
 *
 * @param featureCollection The collection of features to compute a bounding box for.
 * @return A [BoundingBox] that covers the geometry.
 */
@ExperimentalTurfApi
public fun bbox(featureCollection: FeatureCollection): BoundingBox = computeBbox(featureCollection.coordAll())

@Suppress("MagicNumber", "DestructuringDeclarationWithTooManyEntries")
public fun computeBbox(coordinates: List<Position>): BoundingBox = coordinates.fold(
    doubleArrayOf(
        Double.POSITIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
    ),
) { acc, (longitude, latitude) ->
    acc.apply {
        set(0, minOf(get(0), longitude))
        set(1, minOf(get(1), latitude))
        set(2, maxOf(get(2), longitude))
        set(3, maxOf(get(3), latitude))
    }
}.let { (minX, minY, maxX, maxY) -> BoundingBox(minX, minY, maxX, maxY) }

/**
 * Takes a bbox and returns an equivalent [Polygon].
 *
 * @see BoundingBox.toPolygon
 *
 * @param bbox The bounding box to convert to a Polygon.
 * @return The bounding box as a polygon
 */
@ExperimentalTurfApi
public fun bboxPolygon(bbox: BoundingBox): Polygon {
    require(bbox.northeast.altitude == null && bbox.southwest.altitude == null) {
        "Bounding Box cannot have altitudes"
    }

    return Polygon(
        listOf(
            bbox.southwest,
            Position(bbox.northeast.longitude, bbox.southwest.latitude),
            bbox.northeast,
            Position(bbox.southwest.longitude, bbox.northeast.latitude),
            bbox.southwest,
        ),
    )
}

@JvmSynthetic
@ExperimentalTurfApi
public fun BoundingBox.toPolygon(): Polygon = bboxPolygon(this)
