package io.github.elcolto.geokjson.geojson.dsl

import io.github.elcolto.geokjson.geojson.serialization.checkTypeForSerialization
import kotlin.jvm.JvmName

@GeoJsonDsl
public class AdditionalFieldsBuilder {
    private val additionalFields = mutableMapOf<String, Any>()

    public fun put(key: String, value: String) {
        additionalFields[key] = value
    }

    public fun put(key: String, value: Boolean) {
        additionalFields[key] = value
    }

    public fun put(key: String, value: Number) {
        additionalFields[key] = value
    }

    @JvmName("putStringCollection")
    public fun put(key: String, value: Collection<String>) {
        additionalFields[key] = value
    }

    @JvmName("putStringArray")
    public fun put(key: String, value: Array<String>) {
        additionalFields[key] = value
    }

    @JvmName("putBooleanCollection")
    public fun put(key: String, value: Collection<Boolean>) {
        additionalFields[key] = value
    }

    @JvmName("putBooleanArray")
    public fun put(key: String, value: BooleanArray) {
        additionalFields[key] = value
    }

    @JvmName("putIntCollection")
    public fun put(key: String, value: Collection<Int>) {
        additionalFields[key] = value
    }

    @JvmName("putIntArray")
    public fun put(key: String, value: IntArray) {
        additionalFields[key] = value
    }

    @JvmName("putLongCollection")
    public fun put(key: String, value: Collection<Long>) {
        additionalFields[key] = value
    }

    @JvmName("putLongArray")
    public fun put(key: String, value: LongArray) {
        additionalFields[key] = value
    }

    @JvmName("putFloatCollection")
    public fun put(key: String, value: Collection<Float>) {
        additionalFields[key] = value
    }

    @JvmName("putFloatArray")
    public fun put(key: String, value: FloatArray) {
        additionalFields[key] = value
    }

    @JvmName("putDoubleArray")
    public fun put(key: String, value: DoubleArray) {
        additionalFields[key] = value
    }

    @JvmName("putMapCollection")
    public fun put(key: String, value: Collection<Map<String, Any>>) {
        value.checkTypeForSerialization()
        additionalFields[key] = value
    }

    public fun put(key: String, value: Map<String, Any>) {
        value.checkTypeForSerialization()
        additionalFields[key] = value
    }

    public fun build(): Map<String, Any> = additionalFields
}
