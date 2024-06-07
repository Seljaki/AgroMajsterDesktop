package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Invoice(
    val id: Int, val title: String, val note: String? = "", val started: String,
    val ended: String? = null, val isPaid: Boolean = false, val dueDate: String? = null,
    val customer_id: Int, val issuer_id: Int, val issuer: Company, val customer: Company, val totalPrice: String
)

suspend fun getAllInvoices(): List<Invoice> {
    val client = getClient()
    val response: HttpResponse = client.get("/invoices")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return Json.decodeFromJsonElement(json.jsonObject["invoices"] ?: return emptyList())

    }
    return emptyList()
}