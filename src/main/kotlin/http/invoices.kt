package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class Invoice(
    val id: Int? = null, val title: String, val note: String? = null, val started: String,
    val ended: String? = null, val isPaid: Boolean = false, val dueDate: String? = null,
    val customer_id: Int, val issuer_id: Int, val issuer: Company? = null , val customer: Company? = null, val totalPrice: String? = null
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

suspend fun getInvoiceById(invoiceId: Int): Invoice? {
    val client = getClient()
    val response: HttpResponse = client.get("/invoices/$invoiceId")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return Json.decodeFromJsonElement(json.jsonObject["invoice"] ?: return null)
    }
    return null
}

suspend fun addInvoice(invoice: Invoice): Boolean {
    val client = getClient()
    println("Invoice: " + invoice)
    val response: HttpResponse = client.post("/invoices") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(invoice))
    }
    println(response)
    return response.status.value in 200..299
}

suspend fun updateInvoice(invoiceId: Int, invoice: Invoice): Invoice? {
    val client = getClient()
    val response: HttpResponse = client.put("/invoices/$invoiceId") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(invoice))
    }
    println(invoice)
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return Json.decodeFromJsonElement(json.jsonObject["invoice"] ?: return null)
    }
    return null
}

suspend fun deleteInvoice(invoiceId: Int): Boolean {
    val client = getClient()
    val response: HttpResponse = client.delete("/invoices/$invoiceId")
    return response.status.value in 200..299
}