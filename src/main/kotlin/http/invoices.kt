package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

val customJson = Json { ignoreUnknownKeys = true }

@Serializable
data class Invoice(
    val id: Int? = null,
    val title: String,
    val note: String,
    val started: String,
    val ended: String?,
    val isPaid: Boolean,
    val dueDate: String?,
    val customer: Company,
    val issuer: Company
)
@Serializable
data class Invoice1(
    val title: String,
    val note: String,
    val started: String,
    val customer_id: Int,
    val issuer_id: Int
)

fun translate(invoice: Invoice): Invoice1 {
    return Invoice1(
        title = invoice.title,
        note = invoice.note,
        started = invoice.started,
        customer_id = invoice.customer.id,
        issuer_id = invoice.issuer.id
    )
}

suspend fun getAllInvoices(): List<Invoice> {
    val client = getClient()
    val response: HttpResponse = client.get("/invoices")
    if (response.status.value in 200..299) {
        val json = customJson.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return customJson.decodeFromJsonElement(json.jsonObject["invoices"] ?: return emptyList())
    }
    return emptyList()
}

suspend fun getInvoiceById(invoiceId: Int): Invoice? {
    val client = getClient()
    val response: HttpResponse = client.get("/invoices/$invoiceId")
    if (response.status.value in 200..299) {
        val json = customJson.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return customJson.decodeFromJsonElement(json.jsonObject["invoice"] ?: return null)
    }
    return null
}

suspend fun addInvoice(invoice: Invoice): Boolean {
    val client = getClient()
    println("Invoice: " + translate(invoice))
    val response: HttpResponse = client.post("/invoices") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(translate(invoice)))
    }
    println(response)
    return response.status.value in 200..299
}

suspend fun updateInvoice(invoiceId: Int, invoice: Invoice): Invoice? {
    val client = getClient()
    val response: HttpResponse = client.put("/invoices/$invoiceId") {
        contentType(ContentType.Application.Json)
        setBody(customJson.encodeToJsonElement(invoice))
    }
    println(invoice)
    if (response.status.value in 200..299) {
        val json = customJson.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return customJson.decodeFromJsonElement(json.jsonObject["invoice"] ?: return null)
    }
    return null
}

suspend fun deleteInvoice(invoiceId: Int): Boolean {
    val client = getClient()
    val response: HttpResponse = client.delete("/invoices/$invoiceId")
    return response.status.value in 200..299
}