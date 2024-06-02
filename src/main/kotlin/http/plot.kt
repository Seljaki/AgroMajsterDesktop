package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import models.Plot

suspend fun getAllPlots(): Array<Plot> {
    val client = getClient()
    val response = client.get("/plots")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        return Json{ignoreUnknownKeys = true}.decodeFromJsonElement(json.jsonObject["plots"] ?: return emptyArray())
    }
    return emptyArray()
}

suspend fun postPlot(plot: Plot): Boolean {
    val client = getClient()
    val response = client.post("/plots") {
        contentType(ContentType.Application.Json)
        setBody(plot)
    }
    if (response.status.value in 200..299) {
        return true
    } else {
        println(Json.parseToJsonElement(response.bodyAsText()))
    }
    return false
}

suspend fun editPlot(plot: Plot, plotId: Int): Boolean {
    val client = getClient()
    val response: HttpResponse = client.put("/plots/$plotId") {
        contentType(ContentType.Application.Json)
        setBody(plot)
    }
    return response.status.value in 200..299
}

suspend fun deletePlot(plotId: Int): Boolean {
    val client = getClient()
    val response: HttpResponse = client.delete("/plots/$plotId")
    println(response)
    return response.status.value in 200..299
}

fun parseGeoJsonCoordinates(boundary: String): List<List<List<Double>>> {
    // Parse the boundary string to extract coordinates
    // This function needs to parse the input string and return the expected coordinates format
    // For simplicity, assume the input is well-formed GeoJSON
    return boundary
        .removeSurrounding("[[", "]]")
        .split("],[")
        .map {
            it.split(",")
                .map { coord -> coord.toDouble() }
        }
        .map { listOf(it) }
}
suspend fun main() {
    val token = login("admin", "admin")
    println("Token: $token")
    if (token != null) {
        TOKEN = token
        val plots = getAllPlots()
        for(plot in plots) {
            println(plot)
        }
    }
}