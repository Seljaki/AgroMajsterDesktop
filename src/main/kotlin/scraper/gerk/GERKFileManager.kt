package scraper.gerk

import http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import models.Plot
import models.PlotGeoJson
import models.PlotGeoJsonMultiPolygon
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureSource
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.data.simple.SimpleFeatureIterator
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geometry.jts.JTSFactoryFinder
import org.locationtech.jts.geom.*
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun findFeatureByCoordinates(filePath: String, lng: Double, lat: Double): SimpleFeature? {
    val file = File(filePath)

    val geometryFactory: GeometryFactory = JTSFactoryFinder.getGeometryFactory()
    val point: Point = geometryFactory.createPoint(Coordinate(lng, lat))

    if (!file.exists()) {
        println("File does not exist")
        return null
    }

    try {
        // Find the data store for the shapefile
        val dataStore = FileDataStoreFinder.getDataStore(file)
        val featureSource: SimpleFeatureSource = dataStore.featureSource

        // Get the feature collection
        val featureCollection = featureSource.features

        // Iterate over the features
        val featureIterator: SimpleFeatureIterator = featureCollection.features()
        while (featureIterator.hasNext()) {
            val feature: SimpleFeature = featureIterator.next()
            val geometry: Geometry = feature.defaultGeometry as Geometry
            if (geometry.contains(point)) {
                println("FOUND")
                //featureIterator.close()
                return feature
            }
        }

        // Close the iterator
        featureIterator.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun simpleFeatureToGeoJson(feature: SimpleFeature): String {
    val featureJSON = FeatureJSON()
    val writer = StringWriter()
    featureJSON.writeFeature(feature, writer)
    val json = Json.parseToJsonElement(writer.toString())
    return json.jsonObject["geometry"].toString() ?: return ""
}

fun downloadLatestGERKFiles(url: String): File {
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 600000
            connectTimeoutMillis = 600000
            socketTimeoutMillis = 600000
        }
    }
    val file = File.createTempFile("GERKDATA", "index")

    runBlocking {
        client.prepareGet(url).execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                    println("Received ${file.length()} bytes from ${httpResponse.contentLength()}")
                }
            }
            println("A file saved to ${file.path}")
        }
    }

    return file
}

fun deleteGERKData(directoryPath: String = "downloads") {
    val dir = File(directoryPath)
    if(dir.exists()) {
        dir.deleteRecursively()
    }
}

fun doesGERKDataExist(directoryPath: String = "downloads"): Boolean {
    return File(directoryPath).exists()
}

fun unZipFile(file: File, destinationDir: String = "downloads") {
    File(destinationDir).run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipInputStream(file.inputStream()).use { zipInputStream ->
        var zipEntry: ZipEntry?
        while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
            val newFile = File(destinationDir, zipEntry!!.name)
            if (zipEntry!!.isDirectory) {
                newFile.mkdirs()
            } else {
                // Make sure directories for nested files are created
                newFile.parentFile?.mkdirs()
                FileOutputStream(newFile).use { outputStream ->
                    zipInputStream.copyTo(outputStream)
                }
            }
            zipInputStream.closeEntry()
        }
    }
}

fun getFilesWithExtension(directory: File, extension: String): File? {
    val files = directory.listFiles()
    for (file in files!!) {
        if (file.name.endsWith(".$extension"))
            return file
    }

    return null
}

fun findGERKShapefile(directoryPath: String = "downloads"): String {
    val dir = File(directoryPath)
    val file = getFilesWithExtension(dir, "shp") ?: throw Exception("No such GERK file")
    return file.path
}

fun updateGERKData() {
    deleteGERKData()
    val GERKUrl = scrapeGERForLink() ?: return
    val file = downloadLatestGERKFiles(GERKUrl)
    unZipFile(file)
    file.delete()
}

fun geoJsonMPToPolygon(geoJsonMultiPolygon: PlotGeoJsonMultiPolygon): PlotGeoJson {
    return PlotGeoJson(coordinates = geoJsonMultiPolygon.coordinates[0])
}

suspend fun main() {
    /*val shapefilePath = "downloads/GERK_20240430.shp"
    val feature = findFeatureByCoordinates(shapefilePath, 577552.2391,140326.2588)
    if(feature != null) {
        println(feature.defaultGeometry)
        println(simpleFeatureToGeoJson(feature))
    }*/

    if(!doesGERKDataExist()) { // PREVERIMO ČE OBSTAJAJO GERK PODATKI
        updateGERKData() // PRENESEMO PODATKE
    }

    val filePath = findGERKShapefile() // POIŠČEMO .shp DATOTEKO
    val feature = findFeatureByCoordinates(filePath, 581585.9012,142261.7764) // poiščemo polygon z kordinati
    if(feature != null) {
        println(feature.defaultGeometry)
        val geom = simpleFeatureToGeoJson(feature)
        val gjson = Json.decodeFromString<PlotGeoJsonMultiPolygon>(geom)
        val gjson2: PlotGeoJson = geoJsonMPToPolygon(gjson)
        println(gjson2)
        val plot = Plot("KOTLIN GERK", "TETS KOTLIN", gjson2)


        // VSTAVIMO V DB
        val token = login("admin", "admin")
        println(plot)
        println("Token: $token")
        if (token != null) {
            TOKEN = token
            postPlot(plot)
        }
    }
}