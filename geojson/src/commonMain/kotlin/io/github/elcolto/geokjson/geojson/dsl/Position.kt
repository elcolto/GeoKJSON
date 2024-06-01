package io.github.elcolto.geokjson.geojson.dsl

import io.github.elcolto.geokjson.geojson.Position

private val LONGITUDE_RANGE = -180.0..180.0

private val LATITUDE_RANGE = -90.0..90.0

@GeoJsonDsl
public fun lngLat(longitude: Double, latitude: Double): Position {
    require(longitude in LONGITUDE_RANGE && latitude in LATITUDE_RANGE)
    return Position(longitude, latitude)
}

@GeoJsonDsl
public fun lngLat(longitude: Double, latitude: Double, altitude: Double): Position {
    require(longitude in LONGITUDE_RANGE && latitude in LATITUDE_RANGE)
    return Position(longitude, latitude, altitude)
}

@GeoJsonDsl
public fun lngLat(longitude: Double, latitude: Double, altitude: Double?): Position {
    require(longitude in LONGITUDE_RANGE && latitude in LATITUDE_RANGE)
    return Position(longitude, latitude, altitude)
}
