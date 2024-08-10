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

    @JvmName("putStringArray")
    public fun put(key: String, value: Array<String>) {
        foreignMembers[key] = value
    }

    @JvmName("putBooleanCollection")
    public fun put(key: String, value: Collection<Boolean>) {
        foreignMembers[key] = value
    }

    @JvmName("putBooleanArray")
    public fun put(key: String, value: BooleanArray) {
        foreignMembers[key] = value
    }

    @JvmName("putIntCollection")
    public fun put(key: String, value: Collection<Int>) {
        foreignMembers[key] = value
    }

    @JvmName("putIntArray")
    public fun put(key: String, value: IntArray) {
        foreignMembers[key] = value
    }

    @JvmName("putLongCollection")
    public fun put(key: String, value: Collection<Long>) {
        foreignMembers[key] = value
    }

    @JvmName("putLongArray")
    public fun put(key: String, value: LongArray) {
        foreignMembers[key] = value
    }

    @JvmName("putFloatCollection")
    public fun put(key: String, value: Collection<Float>) {
        foreignMembers[key] = value
    }

    @JvmName("putFloatArray")
    public fun put(key: String, value: FloatArray) {
        foreignMembers[key] = value
    }

    @JvmName("putDoubleArray")
    public fun put(key: String, value: DoubleArray) {
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
