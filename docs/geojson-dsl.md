## GeoJson DSL

It's recommended to construct GeoJson objects in-code using the GeoJson DSL.

### Positions

Convenience functions to construct latitude/longitude Position instances is included.
These functions will check for valid latitude and longitude values and will throw an `IllegalArgumentException`
otherwise.

=== "Kotlin"

  ```kotlin
  lngLat(longitude = -75.0, latitude = 45.0)
  
  // Throws exception!!
  lngLat(longitude = -565.0, latitude = 45.0)
  ```

=== "JSON"

```json
[-75.0, 45.0]
```

### Geometry

Each geometry type has a corresponding DSL.

A GeoJson object's `bbox` value can be assigned in any of the DSLs.

#### Point

=== "Kotlin"

  ```kotlin
  point(longitude = -75.0, latitude = 45.0, altitude = 100.0)
  
  // Or...
  point(Position(12.5, 35.9))
  ```

=== "JSON"

```json
{
  "type": "Point",
  "coordinates": [-75.0, 45.0, 100.0]
}
```

#### MultiPoint

The `MultiPoint` DSL creates a `MultiPoint` from many `Point`s, or by using the unary plus operator to add `Position`
instances as positions in the geometry.
`Point` geometries can also be added to the multi point using the unary plus operator.

=== "Kotlin"

  ```kotlin
  val myPoint = Point(88.0, 34.0)
  multiPoint {
    point(-75.0, 45.0)
  
    +lngLat(-78.0, 44.0)
    +myPoint
  }
  ```

=== "JSON"

```json
{
  "type": "MultiPoint",
  "coordinates": [
    [-75.0, 45.0],
    [-78.0, 44.0],
    [88.0, 34.0]
  ]
}
```

#### LineString

A `LineString` contains main points. Like with `MultiPoint`, a `LineString` can also be built using the unary plus
operator to add positions as part of the line.
The order in which positions are added to the `LineString` is the order that the `LineString` will follow.

=== "Kotlin"

  ```kotlin
  lineString {
    point(45.0, 45.0)
    point(0.0, 0.0)
  }
  ```

=== "JSON"

```json
{
  "type": "LineString",
  "coordinates": [
    [45.0, 45.0],
    [0.0, 0.0]
  ]
}
```

#### MultiLineString

The `MultiLineString` DSL uses the unary plus operator to add multiple line strings. The `LineString` DSL can be used to
create `LineString` objects to add.

=== "Kotlin"

  ```kotlin
  val simpleLine = lineString {
    point(45.0, 45.0)
    point(0.0, 0.0)
  }
  
  multiLineString {
    +simpleLine
  
    // Inline LineString creation
    lineString {
      point(44.4, 55.5)
      point(55.5, 66.6)
    }
  }
  ```

=== "JSON"

```json
{
  "type": "MultiLineString",
  "coordinates": [
    [
      [45.0, 45.0],
      [0.0, 0.0]
    ],
    [
      [44.4, 55.5],
      [55.5, 66.6]
    ]
  ]
}
```

#### Polygon

The `Polygon` DSL is used by specifying linear rings that make up the polygon's shape and holes.
The first `ring` is the exterior ring with four or more positions. The last position must be the same as the first
position.
All `ring`s that follow will represent interior rings (i.e. holes) in the polygon.

For convenience, the `complete()` function can be used to "complete" a ring.
It adds the last position in the ring by copying the first position that was added.

=== "Kotlin"

  ```kotlin
  val simpleLine = lineString {
    point(45.0, 45.0)
    point(0.0, 0.0)
  }
  
  polygon {
    ring {
      // LineStrings can be used as part of a ring
      +simpleLine
      point(12.0, 12.0)
      complete()
    }
    ring {
      point(4.0, 4.0)
      point(2.0, 2.0)
      point(3.0, 3.0)
      complete()
    }
  }
  ```

=== "JSON"

```json
{
  "type": "Polygon",
  "coordinates": [
    [
      [45.0, 45.0],
      [0.0, 0.0],
      [12.0, 12.0],
      [45.0, 45.0]
    ],
    [
      [4.0, 4.0],
      [2.0, 2.0],
      [3.0, 3.0],
      [4.0, 4.0]
    ]
  ]
}
```

#### MultiPolygon

Like with previous "Multi" geometries, the unary plus operator is used to add multiple `Polygon` objects.
The `Polygon` DSL can also be used here.

=== "Kotlin"

  ```kotlin
  val simplePolygon = previousExample()
  
  multiPolygon {
    +simplePolygon
    polygon {
      ring {
        point(12.0, 0.0)
        point(0.0, 12.0)
        point(-12.0, 0.0)
        point(5.0, 5.0)
        complete()
      }
    }
  }
  ```

=== "JSON"

```json
{
  "type": "MultiPolygon",
  "coordinates": [
    [
      [
        [45.0, 45.0],
        [0.0, 0.0],
        [12.0, 12.0],
        [45.0, 45.0]
      ],
      [
        [4.0, 4.0],
        [2.0, 2.0],
        [3.0, 3.0],
        [4.0, 4.0]
      ]
    ],
    [
      [
        [12.0, 0.0],
        [0.0, 12.0],
        [-12.0, 0.0],
        [5.0, 5.0],
        [12.0, 0.0]
      ]
    ]
  ]
}
```

#### Geometry Collection

The unary plus operator can be used to add any geometry instance to a `GeometryCollection`.

=== "Kotlin"

  ```kotlin
  val simplePoint: Point = previousPoint()
  val simpleLine: LineString = previousLineString()
  val simplePolygon: Polygon = previousPolygon()
  
  geometryCollection {
    +simplePoint
    +simpleLine
    +simplePolygon
  }
  ```

=== "JSON"

```json
{
  "type": "GeometryCollection",
  "geometries": [
    {
      "type": "Point",
      "coordinates": [-75.0, 45.0, 100.0]
    },
    {
      "type": "LineString",
      "coordinates": [
        [45.0, 45.0],
        [0.0, 0.0]
      ]
    },
    {
      "type": "Polygon",
      "coordinates": [
        [
          [45.0, 45.0],
          [0.0, 0.0],
          [12.0, 12.0],
          [45.0, 45.0]
        ],
        [
          [4.0, 4.0],
          [2.0, 2.0],
          [3.0, 3.0],
          [4.0, 4.0]
        ]
      ]
    }
  ]
}
```

### Feature

The `Feature` DSL can construct a `Feature` object with a geometry, a bounding box, and an id. Properties can be
specified
in the `PropertiesBuilder` block by calling `put(key, value)` to add properties.

=== "Kotlin"

  ```kotlin
  feature(geometry = point(-75.0, 45.0), id = "point1", bbox = BoundingBox(-76.9, 44.1, -74.2, 45.7)) {
    put("name", "Hello World")
    put("value", 13)
    put("cool", true)
  }
  ```

=== "JSON"

```json
{
  "type": "Feature",
  "id": "point1",
  "bbox": [
    -76.9,
    44.1,
    -74.2,
    45.7
  ],
  "properties": {
    "name": "Hello World",
    "value": 13,
    "cool": true
  },
  "geometry": {
    "type": "Point",
    "coordinates": [
      -75.0,
      45.0
    ]
  }
}
```

### Feature Collection

A `FeatureCollection` is constructed by adding multiple `Feature` objects using the unary plus operator.

=== "Kotlin"

  ```kotlin
  featureCollection {
    feature(geometry = point(-75.0, 45.0))
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
          -75.0,
          45.0
        ]
      },
      "properties": {}
    }
  ]
}
  ```
