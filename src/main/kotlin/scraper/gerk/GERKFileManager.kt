package scraper.gerk

import org.geotools.api.data.FileDataStoreFinder
import org.geotools.api.data.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureIterator
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.util.URLs
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class GeoToolsTileProvider {

    fun getTile(latitude: Double, longitude: Double, zoomLvl: Int, shapefilePath: String, tileBasePath: String): BufferedImage? {
        // Step 1: Load the shapefile
        val store = FileDataStoreFinder.getDataStore(File(shapefilePath))
        val featureSource: SimpleFeatureSource = store.featureSource

        // Step 2: Calculate the tile indices based on the coordinates and zoom level
        val tileX = long2tileX(longitude, zoomLvl)
        val tileY = lat2tileY(latitude, zoomLvl)

        // Step 3: Locate the tile image on the filesystem
        val tilePath = "$tileBasePath/$zoomLvl/$tileY/$tileX.jpg"
        val tileFile = File(tilePath)

        if (!tileFile.exists()) {
            throw IOException("Tile not found: $tilePath")
        }

        // Return the tile image
        return ImageIO.read(tileFile)
    }

    // Utility functions to convert coordinates to tile indices
    fun long2tileX(lon: Double, zoom: Int): Int {
        return Math.floor((lon + 180) / 360 * Math.pow(2.0, zoom.toDouble())).toInt()
    }

    fun lat2tileY(lat: Double, zoom: Int): Int {
        return Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * Math.pow(2.0, zoom.toDouble())).toInt()
    }
}

fun readShapefile(filePath: String): List<Geometry> {
    val shapefile = File(filePath)
    println(shapefile.exists())

    //val lon = 1.0
    //val lat = 1.0

    //val geometryFactory: GeometryFactory = JTSFactoryFinder.getGeometryFactory()
    //val point = geometryFactory.createPoint(org.locationtech.jts.geom.Coordinate(lon, lat))


    val dataStore = FileDataStoreFinder.getDataStore(shapefile)
    val featureSource: SimpleFeatureSource = dataStore.featureSource
    val featureCollection = featureSource.features


    val geometries = mutableListOf<Geometry>()
    val iterator = featureCollection.features()
    while (iterator.hasNext()) {
        val feature = iterator.next()
        println(feature.attributes)
        val geometry = feature.defaultGeometry as Geometry
        geometries.add(geometry)

        /*if (geometry is org.locationtech.jts.geom.Polygon) {
            val polygon = geometry as org.locationtech.jts.geom.Polygon

            if (polygon.contains(point)) {
                println("Point is within the polygon with attributes: ${feature.attributes}")
                break // Exit loop if the point is found in a polygon
            }
        }*/
    }
    iterator.close()
    dataStore.dispose()

    return geometries
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

fun main() {
    val shapefilePath = "./downloads/GERK_20240430.shp"
    val geometry = readShapefile(shapefilePath)
    println(geometry[0].toText())
}