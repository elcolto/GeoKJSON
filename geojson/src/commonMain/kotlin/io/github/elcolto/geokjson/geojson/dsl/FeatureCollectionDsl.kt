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
    public val foreignMembers: MutableMap<String, Any> = mutableMapOf(),
) {
    public operator fun Feature<Geometry>.unaryPlus() {
        features.add(this)
    }

    public fun create(): FeatureCollection = FeatureCollection(features, bbox, foreignMembers.toMap())

    public fun feature(
        geometry: Geometry? = null,
        id: String? = null,
        bbox: BoundingBox? = null,
        foreignMembers: AdditionalFieldsBuilder.() -> Unit = {},
        properties: AdditionalFieldsBuilder.() -> Unit = {},
    ) {
        +Feature(
            geometry = geometry,
            properties = AdditionalFieldsBuilder().apply(properties).build(),
            id = id,
            bbox = bbox,
            foreignMembers = AdditionalFieldsBuilder().apply(foreignMembers).build(),
        )
    }

    public fun foreignMembers(foreignMembers: AdditionalFieldsBuilder.() -> Unit = {}) {
        this.foreignMembers.putAll(AdditionalFieldsBuilder().apply(foreignMembers).build())
    }
}

@GeoJsonDsl
public inline fun featureCollection(block: FeatureCollectionDsl.() -> Unit): FeatureCollection = FeatureCollectionDsl()
    .apply(block).create()
