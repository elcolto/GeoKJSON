package io.github.elcolto.geokjson.turf.utils

import okio.Path.Companion.toPath

const val RESOURCE_PATH = "./src/commonTest/resources"

fun readResource(name: String) = fileSystem.read("$RESOURCE_PATH/$name".toPath()) { readUtf8() }
