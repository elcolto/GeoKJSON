package io.github.elcolto.geokjson.turf.classification

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.Position
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.Units
import io.github.elcolto.geokjson.turf.measurement.distance

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param points against input point set
 * @param units the units of the numeric result, default [Units.Kilometers]
 * @returns the closest point in the set to the reference point. `distance` as [Double] value is stored in
 * [Feature.properties] with key `distanceToPoint`
 **/
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun nearestPointFeature(target: Position, points: List<Point>, units: Units = Units.Kilometers): Feature<Point> {
    require(points.isNotEmpty()) { "Parameter points must not be empty" }
    val nearestPoint = points.map { (it to distance(it.coordinates, target, units)) }
        .minBy { it.second }

    return Feature(
        nearestPoint.first,
        mapOf(NearestPoint.DISTANCE_TO_POINT to nearestPoint.second),
    )
}

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param points against input point set
 * @returns the closest point in the set to the reference point.
 **/
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun nearestPoint(target: Position, points: List<Point>): Point =
    requireNotNull(nearestPointFeature(target, points).geometry)

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param points against input point set
 * @param units the units of the numeric result, default [Units.Kilometers]
 * @returns the closest point in the set to the reference point. `distance` as [Double] value is stored in
 * [Feature.properties] with key `distanceToPoint`
 **/
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun nearestPointFeature(target: Point, points: List<Point>, units: Units = Units.Kilometers): Feature<Point> =
    nearestPointFeature(target.coordinates, points, units)

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param points against input point set
 * @returns the closest point in the set to the reference point. `distance` as [Double] value is stored in
 * [Feature.properties] with key `distanceToPoint`
 **/
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun nearestPoint(target: Point, points: List<Point>): Point = nearestPoint(target.coordinates, points)

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param featureCollection against input point set. At least one feature must be of instance [Point]
 * @param units the units of the numeric result, default [Units.Kilometers]
 * @returns the closest point in the set to the reference point. `distance` as [Double] value is stored in
 * [Feature.properties] with key [NearestPoint.DISTANCE_TO_POINT]/`distanceToPoint`
 **/
@ExperimentalTurfApi
@Throws(IllegalArgumentException::class)
public fun nearestPointFeature(
    target: Point,
    featureCollection: FeatureCollection,
    units: Units = Units.Kilometers,
): Feature<Point> {
    require(featureCollection.any { it.geometry is Point }) { "Parameter featureCollection must contain a point" }
    return nearestPointFeature(
        target.coordinates,
        featureCollection.mapNotNull { it.geometry }.filterIsInstance<Point>(),
        units,
    )
}

/**
 * Takes a target [Point] and a [List] of [Point] geometries and returns the
 * point from the list closest to the reference. This calculation
 * is geodesic.
 *
 * @name nearestPoint
 * @param target the reference coordinates
 * @param featureCollection against input point set. At least one feature must be of instance [Point]
 * @returns the closest point in the set to the reference point.
 **/
@ExperimentalTurfApi
@Throws(NoSuchElementException::class)
public fun nearestPoint(target: Point, featureCollection: FeatureCollection): Point =
    nearestPointFeature(target, featureCollection).getGeometry()

public object NearestPoint {

    /**
     * Identifier to store distance to target within [Feature.properties]
     */
    public const val DISTANCE_TO_POINT: String = "distanceToPoint"
}
