# Ported Functions

The following functions have been ported from [turfjs](https://turfjs.org/docs/api/along).

## Measurement

- [x] [`along`](./api/turf/io.github.elcolto.geokjson.turf.measurement/along.html)
- [x] [`area`](./api/turf/io.github.elcolto.geokjson.turf.measurement/area.html)
- [x] [`bbox`](./api/turf/io.github.elcolto.geokjson.turf.measurement/bbox.html)
- [x] [`bboxPolygon`](./api/turf/io.github.elcolto.geokjson.turf.measurement/bbox-polygon.html)
- [x] [`bearing`](./api/turf/io.github.elcolto.geokjson.turf.measurement/bearing.html)
- [x] [`center`](./api/turf/io.github.elcolto.geokjson.turf.measurement/center.html)
- [x] [`centerOfMass`]((./api/turf/io.github.elcolto.geokjson.turf.measurement/center-of-mass.html))
- [x] [`centroid`](./api/turf/io.github.elcolto.geokjson.turf.measurement/centroid.html)
- [x] [`destination`](./api/turf/io.github.elcolto.geokjson.turf.measurement/destination.html)
- [x] [`distance`](./api/turf/io.github.elcolto.geokjson.turf.measurement/distance.html)
- [x] [`envelope`](./api/turf/io.github.elcolto.geokjson.turf.measurement/envelope.html)
- [x] [`greatCircle`](./api/turf/io.github.elcolto.geokjson.turf.measurement/great-circle.html)
- [x] [`length`](./api/turf/io.github.elcolto.geokjson.turf.measurement/length.html)
- [x] [`midpoint`](./api/turf/io.github.elcolto.geokjson.turf.measurement/midpoint.html)
- [x] [`pointOnFeature`](./api/turf/io.github.elcolto.geokjson.turf.measurement/point-on-feature.html)
- [x] [`polygonTangents`](./api/turf/io.github.elcolto.geokjson.turf.measurement/polygon-tangents.html)
- [x] [`pointToLineDistance`](./api/turf/io.github.elcolto.geokjson.turf.measurement/point-to-line-distance.html)
- [x] [`rhumbBearing`](./api/turf/io.github.elcolto.geokjson.turf.measurement/rhumb-bearing.html)
- [x] [`rhumbDestination`](./api/turf/io.github.elcolto.geokjson.turf.measurement/rhumb-destination.html)
- [x] [`rhumbDistance`]((./api/turf/io.github.elcolto.geokjson.turf.measurement/rhumb-distance.html))
- [x] [`square`](./api/turf/io.github.elcolto.geokjson.turf.measurement/square.html)

## Coordinate Mutation

- [x] [`cleanCoords`](./api/turf/io.github.elcolto.geokjson.turf.coordinatemutation/clean-coordinates.html)
- [x] [`flip`](./api/turf/io.github.elcolto.geokjson.turf.coordinatemutation/flip.html)
- [ ] `rewind`
- [x] `round`  
  Use `round` or `Math.round` from the standard library instead.
- [ ] `truncate`

## Transformation

- [ ] `bboxClip`
- [x] [`bezierSpline`](./api/turf/io.github.elcolto.geokjson.turf.transformation/bezier-spline.html)
- [ ] `buffer`
- [x] [`circle`](./api/turf/io.github.elcolto.geokjson.turf.transformation/circle.html)
- [ ] `clone`
- [ ] `concave`
- [x] [`convex`](./api/turf/io.github.elcolto.geokjson.turf.transformation/convex.html)
- [ ] `difference`
- [ ] `dissolve`
- [ ] `intersect`
- [ ] `lineOffset`
- [ ] `simplify`
- [ ] `tessellate`
- [ ] `transformRotate`
- [ ] `transformTranslate`
- [x] [`transformScale`](./api/turf/io.github.elcolto.geokjson.turf.transformation/scale.html)
- [ ] `union`
- [ ] `voronoi`

## Feature Conversion

- [ ] `combine`
- [ ] `explode`
- [ ] `flatten`
- [ ] `lineToPolygon`
- [ ] `polygonize`
- [ ] `polygonToLine`

## Miscellaneous

- [ ] `kinks`
- [ ] `lineArc`
- [ ] `lineChunk`
- [x] [`lineIntersect`](./api/turf/io.github.elcolto.geokjson.turf.misc/line-intersect.html)
  Partially implemented.
- [ ] `lineOverlap`
- [ ] `lineSegment`
- [x] [`lineSlice`](./api/turf/io.github.elcolto.geokjson.turf.misc/line-slice.html)
- [ ] `lineSliceAlong`
- [ ] `lineSplit`
- [ ] `mask`
- [x] [`nearestPointOnLine`](./api/turf/io.github.elcolto.geokjson.turf.misc/nearest-point-on-line.html)
- [ ] `sector`
- [ ] `shortestPath`
- [ ] `unkinkPolygon`

## Helper

Use the [GeoJson DSL](./geojson-dsl) instead.

## Random

- [ ] `randomPosition`
- [ ] `randomPoint`
- [ ] `randomLineString`
- [ ] `randomPolygon`

## Data

- [ ] `sample`

## Interpolation

- [ ] `interpolate`
- [ ] `isobands`
- [ ] `isolines`
- [ ] `planepoint`
- [ ] `tin`

## Joins

- [ ] `pointsWithinPolygon`
- [ ] `tag`

## Grids

- [ ] `hexGrid`
- [ ] `pointGrid`
- [x] [`squareGrid`](./api/turf/io.github.elcolto.geokjson.turf.grids/square-grid.html)
- [ ] `triangleGrid`

## Classification

- [x] [`nearestPoint`](./api/turf/io.github.elcolto.geokjson.turf.classification/nearest-point.html)

## Aggregation

- [ ] `collect`
- [ ] `clustersDbscan`
- [ ] `clustersKmeans`

## Meta

- [ ] `coordAll`
- [ ] `coordEach`
- [ ] `coordReduce`
- [ ] `featureEach`
- [ ] `featureReduce`
- [ ] `flattenEach`
- [ ] `flattenReduce`
- [ ] `getCoord`
- [ ] `getCoords`
- [ ] `getGeom`
- [ ] `getType`
- [ ] `geomEach`
- [ ] `geomReduce`
- [ ] `propEach`
- [ ] `segmentEach`
- [ ] `segmentReduce`
- [ ] `getCluster`
- [ ] `clusterEach`
- [ ] `clusterReduce`

## Assertations

- [ ] `collectionOf`
- [ ] `containsNumber`
- [ ] `geojsonType`
- [ ] `featureOf`

## Booleans

- [x] [`booleanClockwise`]((./api/turf/io.github.elcolto.geokjson.turf.booleans/clockwise.html))
- [ ] `booleanContains`
- [ ] `booleanCrosses`
- [ ] `booleanDisjoint`
- [ ] `booleanEqual`
- [ ] `booleanOverlap`
- [ ] `booleanParallel`
- [x] [`booleanPointInPolygon`](./api/turf/io.github.elcolto.geokjson.turf.booleans/point-in-polygon.html)
- [x] [`booleanPointOnLine`](./api/turf/io.github.elcolto.geokjson.turf.booleans/point-on-line.html)
- [x] [`booleanTouches`](./api/turf/io.github.elcolto.geokjson.turf.booleans/touches.html)
- [x] [`booleanWithin`](./api/turf/io.github.elcolto.geokjson.turf.booleans/within.html)

## Unit Conversion

- [x] [`bearingToAzimuth`](./api/turf/io.github.elcolto.geokjson.turf/bearing-to-azimuth.html)
- [x] [`convertArea`](./api/turf/io.github.elcolto.geokjson.turf/convert-area.html)
- [x] [`convertLength`](./api/turf/io.github.elcolto.geokjson.turf/convert-length.html)
- [ ] `degreesToRadians`
- [x] [`lengthToRadians`](./api/turf/io.github.elcolto.geokjson.turf/length-to-radians.html)
- [x] [`lengthToDegrees`](./api/turf/io.github.elcolto.geokjson.turf/length-to-degrees.html)
- [x] [`radiansToLength`](./api/turf/io.github.elcolto.geokjson.turf/radians-to-length.html)
- [ ] `radiansToDegrees`
- [ ] `toMercator`
- [ ] `toWgs84`
