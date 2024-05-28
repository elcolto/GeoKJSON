package io.github.elcolto.geokjson.turf.utils

import com.goncalossilva.resources.Resource

const val RESOURCE_PATH = "./src/commonTest/resources"

fun readResource(name: String) = Resource("$RESOURCE_PATH/${name}").readText()
