package io.github.elcolto.geokjson.geojson.dsl

import io.github.elcolto.geokjson.geojson.serialization.checkTypeForSerialization
import kotlin.jvm.JvmName

@GeoJsonDsl
public class ForeignMembersBuilder {
    private val foreignMembers = mutableMapOf<String, Any>()

    public fun put(key: String, value: String) {
        foreignMembers[key] = value
    }

    public fun put(key: String, value: Boolean) {
        foreignMembers[key] = value
    }

    public fun put(key: String, value: Number) {
        foreignMembers[key] = value
    }

    @JvmName("putStringCollection")
    public fun put(key: String, value: Collection<String>) {
        foreignMembers[key] = value
    }

    @JvmName("putBooleanCollection")
    public fun put(key: String, value: Collection<Boolean>) {
        foreignMembers[key] = value
    }

    @JvmName("putNumberCollection")
    public fun put(key: String, value: Collection<Number>) {
        foreignMembers[key] = value
    }

    @JvmName("putMapCollection")
    public fun put(key: String, value: Collection<Map<String, Any>>) {
        value.checkTypeForSerialization()
        foreignMembers[key] = value
    }


    public fun put(key: String, value: Map<String, Any>) {
        value.checkTypeForSerialization()
        foreignMembers[key] = value
    }


    public fun build(): Map<String, Any> = foreignMembers
}
