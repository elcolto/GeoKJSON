package io.github.elcolto.geokjson.geojson.serialization

import io.github.elcolto.geokjson.geojson.Point
import io.github.elcolto.geokjson.geojson.dsl.AdditionalFieldsBuilder
import io.github.elcolto.geokjson.geojson.dsl.point
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ForeignMembersSerializationTest {

    @Test
    fun serializePrimitiveForeignMember() {
        val point = testPoint {
            put("key0", "value0")
            put("key1", true)
            put("key2", 3.2)
        }

        val foreignMembers = point.foreignMembers
        assertTrue(foreignMembers.isNotEmpty())
        assertEquals("value0", foreignMembers["key0"])
        assertEquals(true, foreignMembers["key1"])
        assertEquals(3.2, foreignMembers["key2"])

        val serializedForeignMembers = point.serializeForeignMembers()
        assertContains(serializedForeignMembers, """"key0":"value0"""")
        assertContains(serializedForeignMembers, """"key1":true""")
        assertContains(serializedForeignMembers, """"key2":3.2""")
    }

    @Test
    fun deserializePrimitiveForeignMember() {
        val json = testPoint {
            put("key0", "value0")
            put("key1", true)
            put("key2", 3.2)
        }.json()

        val point = Point.fromJson(json)

        val foreignMembers = point.foreignMembers
        assertTrue(foreignMembers.isNotEmpty())
        assertEquals("value0", foreignMembers["key0"])
        assertEquals(true, foreignMembers["key1"])
        assertEquals(3.2, foreignMembers["key2"])

        val foreignMembersJson = point.serializeForeignMembers()
        assertContains(foreignMembersJson, foreignMembers["key0"].toString())
        assertContains(foreignMembersJson, foreignMembers["key1"].toString())
        assertContains(foreignMembersJson, foreignMembers["key2"].toString())
        assertContains(foreignMembersJson, "key0")
        assertContains(foreignMembersJson, "key1")
        assertContains(foreignMembersJson, "key2")
    }

    @Test
    fun serializeMapsForeignMember() {
        val value = mapOf(
            "key1" to 1,
            "key2" to true,
            "key3" to "value0",
        )
        val point = testPoint {
            put("customType", value)
        }

        val foreignMembers = point.foreignMembers
        assertTrue(foreignMembers.isNotEmpty())
        assertEquals(value, foreignMembers["customType"])

        val json = point.serializeForeignMembers(standaloneParsing = true)
        assertTrue(json.isNotEmpty())

        val plainJson = """{"customType":{"key1":1,"key2":true,"key3":"value0"}}"""

        val jsonObject = Json.parseToJsonElement(json)
        assertIs<JsonObject>(jsonObject)
        assertEquals(Json.parseToJsonElement(plainJson), jsonObject)
    }

    @Test
    fun deserializeMapForeignMember() {
        val value: Map<String, Any> = mapOf(
            "key1" to 1,
            "key2" to true,
            "key3" to "value0",
        )
        val json = testPoint {
            put("customType", value)
        }.json()

        val point = Point.fromJson(json)

        val foreignMembers = point.foreignMembers
        assertTrue(foreignMembers.isNotEmpty())
        val map = foreignMembers["customType"]
        assertIs<Map<String, Any>>(map)
        assertEquals(value.entries, map.entries)
    }

    @Test
    fun serializeArrayForeignMember() {
        val stringArray = arrayOf("a", "b", "c")
        val intArray = intArrayOf(1, 2, 3)
        val booleanSet = setOf(true, false)
        val point = testPoint {
            put("stringArray", stringArray)
            put("intArray", intArray)
            put("booleanSet", booleanSet)
        }

        val foreignMembers = point.foreignMembers
        assertTrue(foreignMembers.isNotEmpty())
        assertContentEquals(stringArray, foreignMembers["stringArray"] as Array<String>)
        assertContentEquals(intArray, foreignMembers["intArray"] as IntArray)
        assertEquals(booleanSet, foreignMembers["booleanSet"])

        val json = point.serializeForeignMembers(standaloneParsing = true)
        assertTrue(json.isNotEmpty())

        val plainJson = """{"stringArray":["a","b","c"],"intArray":[1,2,3],"booleanSet":[true,false]}"""

        val jsonObject = Json.parseToJsonElement(json)
        assertIs<JsonObject>(jsonObject)
        assertEquals(Json.parseToJsonElement(plainJson), jsonObject)
    }

    private companion object {
        fun testPoint(foreignMembers: AdditionalFieldsBuilder.() -> Unit) =
            point(longitude = 54.3, latitude = 10.0, altitude = null) {
                foreignMembers(foreignMembers)
            }
    }
}
