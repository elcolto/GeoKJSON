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
        foreignMembers: ForeignMembersBuilder.() -> Unit = {},
        properties: PropertiesBuilder.() -> Unit = {},
    ) {
        +Feature(
            geometry,
            PropertiesBuilder().apply(properties).build(),
            id,
            bbox,
            ForeignMembersBuilder().apply(foreignMembers).build(),
        )
    }

    public fun foreignMembers(foreignMembers: ForeignMembersBuilder.() -> Unit = {}) {
        this.foreignMembers.putAll(ForeignMembersBuilder().apply(foreignMembers).build())
    }
}

@GeoJsonDsl
public inline fun featureCollection(block: FeatureCollectionDsl.() -> Unit): FeatureCollection = FeatureCollectionDsl()
    .apply(block).create()
