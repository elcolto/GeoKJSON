@file:JvmName("-FeatureCollectionDslKt")

package io.github.elcolto.geokjson.geojson.dsl

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.FeatureCollection
import io.github.elcolto.geokjson.geojson.Geometry
import kotlin.jvm.JvmName

@GeoJsonDsl
public class FeatureCollectionDsl(
    private val features: MutableList<Feature<Geometry>> = mutableListOf(),
    public var bbox: BoundingBox? = null,
) {
    public operator fun Feature<Geometry>.unaryPlus() {
        features.add(this)
    }

    public fun create(): FeatureCollection = FeatureCollection(features, bbox)

    public fun feature(
        geometry: Geometry? = null,
        id: String? = null,
        bbox: BoundingBox? = null,
        properties: PropertiesBuilder.() -> Unit = {},
    ) {
        +Feature(geometry, PropertiesBuilder().apply(properties).build(), id, bbox)
    }
}

@GeoJsonDsl
public inline fun featureCollection(block: FeatureCollectionDsl.() -> Unit): FeatureCollection = FeatureCollectionDsl()
    .apply(block).create()
