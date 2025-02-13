package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class JobType(val id: Int, val name: String, val quantityType: String, val price: Double)


suspend fun getAllJobTypes(): List<JobType>{
    val client = getClient()
    val response : HttpResponse = client.get("/jobTypes")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        return Json.decodeFromJsonElement(json.jsonObject["jobTypes"] ?: return emptyList())
    }
    return emptyList()
}

suspend fun getJobTypeById(id: Int): JobType?{
    val client = getClient()
    val response: HttpResponse = client.get("/jobTypes/${id}")
    println("id: " + id)
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return Json.decodeFromJsonElement(json.jsonObject["jobType"] ?: return null)
    }
    return null
}

suspend fun getAllJobTypesOfQuantityType(quantityType: String): MutableList<JobType> {
    val client = getClient()
    val response: HttpResponse = client.get("/jobTypes?quantityType=$quantityType")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        return Json.decodeFromJsonElement(json.jsonObject["jobTypes"] ?: return mutableListOf())
    }
    return mutableListOf()
}