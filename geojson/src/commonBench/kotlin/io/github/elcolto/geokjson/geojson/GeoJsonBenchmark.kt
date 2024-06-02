@file:Suppress("MagicNumber")

package io.github.elcolto.geokjson.geojson

import io.github.elcolto.geokjson.geojson.dsl.featureCollection
import io.github.elcolto.geokjson.geojson.dsl.lineString
import io.github.elcolto.geokjson.geojson.dsl.point
import io.github.elcolto.geokjson.geojson.dsl.polygon
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class GeoJsonBenchmark {
    private lateinit var dataset: FeatureCollection
    private lateinit var geojson: String
    private lateinit var jsonObject: JsonObject

    private fun generateDataset(): FeatureCollection {
        val random = Random(0)
        return featureCollection {
            repeat(5000) {
                feature(geometry = point(random.nextDouble(360.0) - 180, random.nextDouble(360.0) - 180))
            }

            repeat(5000) {
                feature(
                    geometry = lineString {
                        repeat(10) {
                            +Position(random.nextDouble(360.0) - 180, random.nextDouble(360.0) - 180)
                        }
                    },
                )
            }

            repeat(5000) {
                feature(
                    geometry = polygon {
                        ring {
                            repeat(10) {
                                +Position(random.nextDouble(360.0) - 180, random.nextDouble(360.0) - 180)
                            }
                        }
                    },
                )
            }
        }
    }

    @Setup
    fun setup() {
        dataset = generateDataset()
        geojson = dataset.json()
        jsonObject = Json.decodeFromString(geojson)
    }

    /**
     * Benchmark serialization using the string concat implementation
     */
    @Benchmark
    fun fastSerialization() {
        dataset.json()
    }

    /**
     * Benchmark serialization using plain kotlinx.serialization
     */
    @Benchmark
    fun kotlinxSerialization() {
        Json.encodeToString(dataset)
    }

    /**
     * Benchmark how fast kotlinx.serialization can encode a GeoJSON structure directly
     */
    @Benchmark
    fun baselineSerialization() {
        Json.encodeToString(jsonObject)
    }

    @Benchmark
    fun deserialization() {
        FeatureCollection.fromJson(geojson)
    }
}
