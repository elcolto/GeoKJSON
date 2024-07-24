# GeoKJSON

GeoKJSON (spelled: "Ge - OK - Json", aligned to Squares Kotlin OK libraries
like [Okio](https://square.github.io/okio/)) is a Kotlin Multiplatform library. Based
on [Spatial K](https://github.com/dellisd/spatial-k) by [Derek Ellis](https://github.com/dellisd) this library is
designed to work with geospatial data in Kotlin including an implementation of GeoJSON and a port of Turfjs written in
pure Kotlin. It supports plain Kotlin and Multiplatform (KMP) projects.

This project divided into two modules which are even released into separate artifacts.

- [geojson](geojson.md) - Containing a collection of [GeoJSON](https://geojson.org/) structured geographic data. These are
  designed to apply to [WGS 84](https://en.wikipedia.org/wiki/World_Geodetic_System#WGS_84)
  and [RFC 7946](https://datatracker.ietf.org/doc/html/rfc7946)
- [turf](turf.md) - A port of [turfjs](https://turfjs.org/) functions for spatial analysis and operations

The goal of these libraries is to provide GeoJSON functionality for Kotlin Multiplatform projects. So encapsulation and
logical operations for geographic needs can be achieved on a shared code base.

## Installation

```kotlin
commonMain {
    dependencies {
        implementation("io.github.elcolto.geokjson:geojson:$geokVersion")
        implementation("io.github.elcolto.geokjson:turf:$geokVersion")
    }
}
```
