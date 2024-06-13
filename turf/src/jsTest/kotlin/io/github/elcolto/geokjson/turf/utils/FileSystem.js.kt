package io.github.elcolto.geokjson.turf.utils

import okio.FileSystem
import okio.NodeJsFileSystem

actual val fileSystem: FileSystem = NodeJsFileSystem
