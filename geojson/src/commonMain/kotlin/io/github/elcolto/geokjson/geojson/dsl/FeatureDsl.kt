@file:JvmName("-FeatureDslKt")
@file:Suppress("MatchingDeclarationName")

package io.github.elcolto.geokjson.geojson.dsl

import io.github.elcolto.geokjson.geojson.BoundingBox
import io.github.elcolto.geokjson.geojson.Feature
import io.github.elcolto.geokjson.geojson.Geometry
import kotlin.jvm.JvmName

@GeoJsonDsl
public inline fun <reified T : Geometry> feature(
    geometry: T? = null,
    id: String? = null,
    bbox: BoundingBox? = null,
    foreignMembers: ForeignMembersBuilder.() -> Unit = {},
    properties: PropertiesBuilder.() -> Unit = {},
): Feature<T> = Feature(
    geometry = geometry,
    properties = PropertiesBuilder().apply(properties).build(),
    id = id,
    bbox = bbox,
    foreignMembers = AdditionalFieldsBuilder().apply(foreignMembers).build(),
)
