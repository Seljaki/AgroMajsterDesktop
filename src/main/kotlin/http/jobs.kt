package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class Job(val id : Int? = null, val quantity: Int, val price: Double, val totalPrice: Double, val timeTaken : Int,
               val invoice_id: Int,val jobtype_id: Int)


suspend fun addJob(job: Job): Boolean {
    val client = getClient()
    val response: HttpResponse = client.post("/invoices/${job.invoice_id}/jobs") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(job))
        println(job.toString())
    }
    return response.status.value in 200..299
}