@file:Suppress("MagicNumber")

package io.github.elcolto.geokjson.turf

import kotlin.math.PI

internal fun degrees(radians: Double) = radians * 180.0 / PI
internal fun radians(degrees: Double) = degrees * PI / 180.0
