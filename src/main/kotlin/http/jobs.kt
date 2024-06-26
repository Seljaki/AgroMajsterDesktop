package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Job(val id : Int? = null, val quantity: Int, val price: Double? = null, val totalPrice: Double? = null, val timeTaken : Int,
               var invoice_id: Int? = null, var jobtype_id: Int)

val customJson = Json { ignoreUnknownKeys = true }
suspend fun addJob(job: Job): Boolean {
    val client = getClient()
    println(job.invoice_id)
    val response: HttpResponse = client.post("/invoices/${job.invoice_id}/jobs") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(job))
        println(job.toString())
    }
    return response.status.value in 200..299
}

suspend fun getAllJobs(invoiceId: Int): List<Job> {
    val client = getClient()
    val response: HttpResponse = client.get("/invoices/${invoiceId}/jobs")
    if (response.status.value in 200..299) {
        val json = customJson.parseToJsonElement(response.bodyAsText())
        println("getAllJobs")
        println(json.toString())
        return customJson.decodeFromJsonElement(json.jsonObject["jobs"] ?: return emptyList())
    }
    return emptyList()
}

suspend fun updateJob(id: Int, job: Job): Job? {
    val client = getClient()
    val response: HttpResponse = client.put("/jobs/${id}") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(job))
    }
    println(job)
    if (response.status.value in 200..299) {
        val json = customJson.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return customJson.decodeFromJsonElement(json.jsonObject["job"] ?: return null)
    }
    return null
}


suspend fun deleteJob(id: Int): Boolean{
    val client = getClient()
    val response: HttpResponse = client.delete("/jobs/$id")
    return response.status.value in 200..299
}