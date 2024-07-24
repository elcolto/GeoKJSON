# GeoJson

The `geojson` module contains an implementation of the [GeoJson standard](https://tools.ietf.org/html/rfc7946).

See below for constructing GeoJson objects using the DSL.

## Installation

=== "Kotlin"

  ```kotlin
  dependencies {
    implementation("io.github.elcolto.geokjson:geojson:<version>")
  }
  ```

=== "Groovy"

  ```groovy
  dependencies {
    implementation "io.github.elcolto.geokjson:geojson:<version>"
  }
  ```

## GeoJson Objects

The `GeoJson` interface represents all GeoJson objects. All GeoJson objects can have a `bbox` property specified on them
which is a `BoundingBox` that represents the bounds of that object's geometry.

### Geometry

Geometry objects are a sealed hierarchy of classes that inherit from the `Geometry` class. This allows for exhaustive
type checks in Kotlin using a `when` block.

=== "Kotlin"

  ```kotlin
  val geometry: Geometry = getSomeGeometry()
  
      val type = when (geometry) {
          is Point -> "Point"
          is MultiPoint -> "MultiPoint"
          is LineString -> "LineString"
          is MultiLineString -> "MultiLineString"
          is Polygon -> "Polygon"
          is MultiPolygon -> "MultiPolygon"
          is GeometryCollection -> "GeometryCollection"
      }
  ```

All seven types of GeoJSON geometries are implemented and summarized below. Full documentation can be found in
the [API pages](../api/geojson/).

#### Position

Positions are implemented as a `DoubleArray`-backed class. Each component (`longitude`, `latitude`, `altitude`) can be
accessed by its propery.
The class also supports destructuring.

Positions are implemented as an interface where the longitude, latitude, and optionally an altitude are accessible as
properties. The basic implementation of the `Position` interface is the `LngLat` class.

=== "Kotlin"

  ```kotlin
  val position: Position = Position(-75.0, 45.0)
  val (longitude, latitude, altitude) = position
  
      // Access values
      position.longitude
      position.latitude
      position.altitude // null if unspecified
  ```

=== "JSON"

  ```json
  [-75, 45]
  ```

#### Point

A Point is a single Position.

=== "Kotlin"

  ```kotlin
  val point = Point(Position(-75.0, 45.0))
  
  println(point.longitude)
  // Prints: -75.0
  ```

=== "JSON"

```json
{
  "type": "Point",
  "coordinates": [-75, 45]
}
```

#### MultiPoint

A `MultiPoint` is an array of Positions.

=== "Kotlin"

  ```kotlin
  val multiPoint = MultiPoint(Position(-75.0, 45.0), Position(-79.0, 44.0))
  ```

=== "JSON"

```json
{
  "type": "MultiPoint",
  "coordinates": [
    [-75, 45],
    [-79, 44]
  ]
}
```

#### LineString

A `LineString` is a sequence of two or more Positions.

=== "Kotlin"

  ```kotlin
  val lineString = LineString(Position(-75.0, 45.0), Position(-79.0, 44.0))
  ```

=== "JSON"

```json
{
  "type": "LineString",
  "coordinates": [
    [
      -75,
      45
    ],
    [
      -79,
      44
    ]
  ]
}
```

#### MultiLineString

A `MultiLineString` is an array of LineStrings.

=== "Kotlin"

  ```kotlin
  val multiLineString = MultiLineString(
    listOf(Position(12.3, 45.6), Position(78.9, 12.3)),
    listOf(Position(87.6, 54.3), Position(21.9, 56.4))
  )
  ```

=== "JSON"

```json
{
  "type": "MultiLineString",
  "coordinates": [
    [
      [12.3, 45.6],
      [78.9, 12.3]
    ],
    [
      [87.6, 54.3],
      [21.9, 56.4]
    ]
  ]
}
```

#### Polygon

A `Polygon` is an array of rings. Each ring is a sequence of points with the last point matching the first point to
indicate a closed area.
The first ring defines the outer shape of the polygon, while all the following rings define "holes" inside the polygon.

=== "Kotlin"

  ```kotlin
  val polygon = Polygon(
    listOf(
      Position(-79.87, 43.42),
      Position(-78.89, 43.49),
      Position(-79.07, 44.02),
      Position(-79.95, 43.87),
      Position(-79.87, 43.42)
    ),
    listOf(
      Position(-79.75, 43.81),
      Position(-79.56, 43.85),
      Position(-79.7, 43.88),
      Position(-79.75, 43.81)
    )
  )
  ```

=== "JSON"

```json
{
  "type": "Polygon",
  "coordinates": [
    [
      [
        -79.87,
        43.42
      ],
      [-78.89, 43.49],
      [-79.07, 44.02],
      [-79.95, 43.87],
      [-79.87, 43.42]
    ],
    [
      [-79.75, 43.81],
      [-79.56, 43.85],
      [-79.7, 43.88],
      [-79.75, 43.81]
    ]
  ]
}
```

#### MultiPolygon

A `MultiPolygon` is an array of Polygons.

=== "Kotlin"

  ```kotlin
  val polygon = listOf(
    Position(-79.87, 43.42),
    Position(-78.89, 43.49),
    Position(-79.07, 44.02),
    Position(-79.95, 43.87),
    Position(-79.87, 43.42)
  ),
  listOf(
    Position(-79.75, 43.81),
    Position(-79.56, 43.85),
    Position(-79.7, 43.88),
    Position(-79.75, 43.81)
  )
  val multiPolygon = MultiPolygon(polygon, polygon)
  ```

=== "JSON"

```json
{
  "type": "MultiPolygon",
  "coordinates": [
    [
      [
        [-79.87, 43.42],
        [-78.89, 43.49],
        [-79.07, 44.02],
        [-79.95, 43.87],
        [-79.87, 43.42]
      ],
      [
        [-79.75, 43.81],
        [-79.56, 43.85],
        [-79.7, 43.88],
        [-79.75, 43.81]
      ]
    ],
    [
      [
        [-79.87, 43.42],
        [-78.89, 43.49],
        [-79.07, 44.02],
        [-79.95, 43.87],
        [-79.87, 43.42]
      ],
      [
        [-79.75, 43.81],
        [-79.56, 43.85],
        [-79.7, 43.88],
        [-79.75, 43.81]
      ]
    ]
  ]
}
```

#### GeometryCollection

A `GeometryCollection` is a collection of different types of Geometry. It implements the `Collection` interface and can
be used in any place that a collection can be used.

=== "Kotlin"

  ```kotlin
  val geometryCollection = GeometryCollection(point, lineString)
  
  // Can be iterated over, and used in any way a Collection<T> can be
  geometryCollection.forEach { geometry ->
    // ...
  }
  ```

=== "JSON"

```json
{
  "type": "GeometryCollection",
  "coordinates": [
    {
      "type": "Point",
      "coordinates": [-75, 45]
    },
    {
      "type": "LineString",
      "coordinates": [
        [-75, 45],
        [-79, 44]
      ]
    }
  ]
}
```

### Feature

A `Feature` can contain a `Geometry` object, as well as a set of data properties, and optionally a commonly used
identifier (`id`).

A feature's properties are stored as a map of `JsonElement` objects from `kotlinx.serialization`.
A set of helper methods to get and set properties with the appropriate types directly.

=== "Kotlin"

  ```kotlin
  val feature = Feature(point)
  feature.setNumberProperty("size", 9999)
  
  val size: Number? = feature.getNumberProperty("size") // 9999
  val geometry: Geometry? = feature.geometry // point
  ```

=== "JSON"

```json
{
  "type": "Feature",
  "geometry": {
    "type": "Point",
    "coordinates": [
      -75,
      45
    ]
  },
  "properties": {
    "size": 9999
  }
}
```

### FeatureCollection

A `FeatureCollection` is a collection of multiple features. `FeatureCollection` implements the `Collection` interface
and can be used in any place that a collection can be used.

=== "Kotlin"

  ```kotlin
  val featureCollection = FeatureCollection(pointFeature)
  
  featureCollection.forEach { feature ->
    // ...
  }
  ```

=== "JSON"

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "Point",
        "coordinates": [
          -75,
          45
        ]
      },
      "properties": {
        "size": 9999
      }
    }
  ]
}
```

### BoundingBox

The `BoundingBox` class is used to represent the bounding boxes that can be set for any `GeoJson` object.
Like the `Position` class, bounding boxes are backed by a `DoubleArray` with each component accessible by its
propery (`southwest` and `northeast`).
Bounding boxes also support destructuring.

=== "Kotlin"

  ```kotlin
  val bbox = BoundingBox(west = 11.6, south = 45.1, east = 12.7, north = 45.7)
  val (southwest, northeast) = bbox // Two Positions
  ```

=== "JSON"

```json
[
  11.6,
  45.1,
  12.7,
  45.7
]
```

## Serialization

### To Json

Any `GeoJson` object can be serialized to a JSON string using the `json()` function.
This function converts the object to JSON using string concatenation and is therefore very fast.

=== "Kotlin"

``` kotlin
val featureCollection: FeatureCollection = getFeatureCollection()

    val json = featureCollection.json()
    println(json)
```

Spatial-K is also fully compatible with `kotlinx.serialization` to allow for integration into more complex models,
however
this is much slower. For encoding directly to JSON strings, prefer to use the `json()` function.

### From Json

The `fromJson` and `fromJsonOrNull` companion (or static) functions are available on each `GeoJson` class to decode each
type of object from a JSON string.

=== "Kotlin"

  ```kotlin
  // Throws exception if the JSON cannot be deserialized to a Point
  val myPoint = Point.fromJson("{...geojson...}")
  
  // Returns null if an error occurs
  val nullable = Point.fromJsonOrNull("{...not a point...}")
  ```

=== "Java"

  ```java
  // Throws exception if the JSON cannot be deserialized to a Point
  var myPoint = Point.fromJson("{...geojson...}");
  
  // Returns null if an error occurs
  var nullable = Point.fromJsonOrNull("{...not a point...}");
  ```

Like with encoding, Spatial-K objects can also be decoded using `kotlinx.serialization`.

=== "Kotlin"

  ```kotlin
  val feature: Feature = Json.decodeFromString(Feature.serializer(), "{...feature...}")
  ```
