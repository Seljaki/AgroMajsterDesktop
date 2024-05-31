package scraper.gerk

import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureSource
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.data.simple.SimpleFeatureIterator
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geometry.jts.JTSFactoryFinder
import org.json.simple.JSONObject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import java.io.File
import java.io.StringWriter


fun findFeatureByCoordinates(filePath: String, lng: Double, lat: Double): SimpleFeature? {
    val file = File(filePath)

    val geometryFactory: GeometryFactory = JTSFactoryFinder.getGeometryFactory()
    val point: Point = geometryFactory.createPoint(Coordinate(lng, lat))

    if (!file.exists()) {
        println("File does not exist: $filePath")
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
                featureIterator.close()
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

/*fun readShapefile(filePath: String) {
    val file = File(filePath)
    //println(file.name)
    //println(file.forEachLine {println(it)})



    val store = FileDataStoreFinder.getDataStore(file)
    val featureSource: SimpleFeatureSource = store.featureSource
    val featureCollection = featureSource.features
    val iterator: SimpleFeatureIterator = featureCollection.features()

    try {
        while (iterator.hasNext()) {
            val feature = iterator.next()
            val geometry = feature.defaultGeometry as Geometry
            println("Feature ID: ${feature.id}, Geometry: $geometry")
        }
    } finally {
        iterator.close()
        store.dispose()
    }
}*/

@Serializable
data class GeoJsonFeature(val type: String, val geometry: String, val properties: String)

fun simpleFeatureToGeoJson(feature: SimpleFeature): String {
    val featureJSON = FeatureJSON()
    val writer = StringWriter()
    featureJSON.writeFeature(feature, writer)
    val json = Json.parseToJsonElement(writer.toString())
    return json.jsonObject["geometry"].toString() ?: return ""
}

fun main() {
    val shapefilePath = "downloads/GERK_20240430.shp"
    val feature = findFeatureByCoordinates(shapefilePath, 577552.2391,140326.2588)
    if(feature != null) {
        println(feature.defaultGeometry)
        println(simpleFeatureToGeoJson(feature))
    }
    //val geometry = readShapefile(shapefilePath)
    //println(geometry[0].toText())
}