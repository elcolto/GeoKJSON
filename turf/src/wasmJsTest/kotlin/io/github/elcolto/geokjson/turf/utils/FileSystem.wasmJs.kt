package io.github.elcolto.geokjson.turf.utils

import okio.FileSystem
import okio.fakefilesystem.FakeFileSystem

actual val fileSystem: FileSystem = FakeFileSystem()
