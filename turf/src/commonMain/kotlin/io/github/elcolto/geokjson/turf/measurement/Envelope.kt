package io.github.elcolto.geokjson.turf.measurement

import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.GeoJson
import io.github.elcolto.geokjson.geojson.Geometry
import io.github.elcolto.geokjson.geojson.GeometryCollection
import io.github.elcolto.geokjson.geojson.Polygon
import io.github.elcolto.geokjson.turf.ExperimentalTurfApi
import io.github.elcolto.geokjson.turf.coordAll

/**
 * Takes any [GeoJson] and returns a  rectangular [Polygon] that encompasses all vertices.
 * @param geoJson input containing any coordinates
 * @return a rectangular [Polygon] feature that encompasses all vertices
 */
@ExperimentalTurfApi
public fun envelope(geoJson: GeoJson): Polygon {
    val coordinates = when (geoJson) {
        is Feature<*> -> geoJson.coordAll()
        is FeatureCollection -> geoJson.coordAll()
        is GeometryCollection -> geoJson.coordAll()
        is Geometry -> geoJson.coordAll()
    }.orEmpty()

    val bbox = geoJson.bbox ?: computeBbox(coordinates)

    return bboxPolygon(bbox)
}
