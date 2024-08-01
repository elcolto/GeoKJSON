import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.elcolto.GeoJsonDslBuilder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import okio.buffer
import okio.source
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


internal suspend fun main(args: Array<String>) {

    args.forEach { println(it) }
    val buildDir = args.first()

    val httpClient = HttpClient(CIO) {
        expectSuccess = true
        followRedirects = true
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(ContentEncoding) {
            deflate()
            gzip()
        }
    }


    val httpResponse = httpClient.get("https://api.github.com/repos/Turfjs/turf/zipball")
    httpClient.close()
    val zipFileName = httpResponse.headers["content-disposition"]?.split(';')?.last()?.substringAfter('=')
    val out: ByteReadChannel = httpResponse.bodyAsChannel()
    val directoryName = downloadZipAndUnzip(buildDir, zipFileName, out)

    val turfPackages = buildDir.toPath() / directoryName / "packages"

    val testFiles = FileSystem.SYSTEM.listRecursively(turfPackages)
        .filter { path: Path ->
            path.toFile().isFile &&
                path.segments.contains("test") &&
                (path.name.endsWith(".json") || path.name.endsWith(".geojson"))
        }
        .groupBy { path ->
            "/".toPath() / path.parent?.segments?.let { it.take(it.size - 1) }?.joinToString("/").orEmpty().toPath()
        }
        .mapKeys { (key, _) ->
            val path = key.toString().removePrefix("$turfPackages/turf-").toPath()
            path.segments.camelCase { first ->
                first.split('-').camelCase()
            }.capitalize()
        }

    val skipped = mutableListOf<String>()

    val fileSpecs = testFiles.flatMap { (key, files) ->

        val segments = key.split(regex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex()).minus("Test")

        val objectName = segments.joinToString("")
        val subPackage = segments.joinToString(".") { it.lowercase() }
        val packageName = "io.github.elcolto.turf.test.$subPackage"
        val objectSpec = FileSpec.builder(
            packageName = packageName,
            fileName = objectName
        ).addType(
            TypeSpec.objectBuilder(objectName)
                .build()
        )

        val propertyFileSpecs = files.mapNotNull { path ->
            val json = buildString {
                FileSystem.SYSTEM.source(path).buffer().use { bufferedSource ->
                    while (true) {
                        val line = bufferedSource.readUtf8Line() ?: break
                        appendLine(line)
                    }
                }
            }
            runCatching { GeoJsonDslBuilder.geoJsonFromFile(json) }
                .onFailure { skipped.add(path.toString()) }
                .getOrNull()
                ?.let { geoJson ->
                    val inOrOut = path.parent?.segments?.last()?.takeIf { it == "in" || it == "out" }
                    val fileName = path.name.removeSuffix(".json").removeSuffix(".geojson")
                        .plus(inOrOut?.capitalize().orEmpty())
                        .replace(".", "")



                    FileSpec.builder(packageName, fileName)
                        .addImport(
                            "io.github.elcolto.geokjson.geojson.dsl",
                            listOf(
                                "feature",
                                "featureCollection",
                                "geometryCollection",
                                "lineString",
                                "lngLat",
                                "multiLineString",
                                "multiPoint",
                                "multiPolygon",
                                "point",
                                "polygon",
                            )
                        )
                        .addProperty(
                            PropertySpec
                                .builder(
                                    name = fileName,
                                    type = geoJson.javaClass
                                )
                                .receiver(ClassName(packageName, objectName))
                                .getter(GeoJsonDslBuilder.geoJsonToFunSpec(geoJson).build())
                                .build()
                        )
                }

        }

        listOf(objectSpec) + propertyFileSpecs
    }

    fileSpecs.forEach {
        val fileSpec = it.build()
        fileSpec.writeTo((buildDir.toPath() / "generated/classes/turf/main/kotlin").toNioPath())
    }

    println("${skipped.size} skipped elements")
    skipped.forEach { path ->
        println("\t$path")
    }

    FileSystem.SYSTEM.deleteRecursively(directoryName.toPath())
}

private fun downloadZipAndUnzip(downloadPath: String, filename: String?, out: ByteReadChannel): String {
    val zipPath = "$downloadPath/${filename ?: "turf-repo.zip"}".toPath()

    FileSystem.SYSTEM.sink(zipPath).buffer().use {
        it.writeAll(out.toInputStream().source())
    }

    val directoryName = unzipFolder(zipPath, downloadPath.toPath())

    FileSystem.SYSTEM.delete(zipPath)
    return directoryName
}


private fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


private fun List<String>.camelCase(onFirstElement: (String) -> String = { it }) = mapIndexed { i: Int, text: String ->
    if (i == 0) onFirstElement(text) else text.capitalize()
}.joinToString("")


@Throws(IOException::class)
fun unzipFolder(source: Path, target: Path?): String {
    var directoryName: String? = null
    // Put the InputStream obtained from Uri here instead of the FileInputStream perhaps?
    ZipInputStream(FileInputStream(source.toFile())).use { zis ->

        // list files in zip
        var zipEntry = zis.nextEntry

        while (zipEntry != null) {

            val newPath = zipSlipProtect(zipEntry, target!!)

            if (zipEntry.isDirectory) {
                Files.createDirectories(newPath.toNioPath())
                if (directoryName == null) {
                    directoryName = newPath.toString()
                }
            } else {

                newPath.parent?.toNioPath()?.let { path ->
                    if (Files.notExists(path)) {
                        Files.createDirectories(path)
                    }
                }

                // copy files, nio
                Files.copy(zis, newPath.toNioPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
    }
    return requireNotNull(directoryName)
}

@Throws(IOException::class)
fun zipSlipProtect(zipEntry: ZipEntry, targetDir: Path): Path {
    // test zip slip vulnerability
    // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

    val targetDirResolved = targetDir.resolve(zipEntry.name)

    // make sure normalized file still has targetDir as its prefix
    // else throws exception
    val normalizePath = targetDirResolved.toNioPath().normalize()
    if (!normalizePath.startsWith(targetDir.toNioPath())) {
        throw IOException("Bad zip entry: " + zipEntry.name)
    }

    return normalizePath.toOkioPath()
}
