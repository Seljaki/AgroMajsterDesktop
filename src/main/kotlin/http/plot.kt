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