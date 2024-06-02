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
data class Company(val id : Int, val name: String, val address: String? = null, val accessToken: String,
                   val phone: String? = null, val taxNumber: String? = null, val iban: String? = null, val email: String? = null,
                   val isTaxpayer: Boolean, val defaultIssuer: Boolean)

suspend fun getAllCompany(): List<Company> {
    val client = getClient()
    val response: HttpResponse = client.get("/companies")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        println(json.toString())
        return Json.decodeFromJsonElement(json.jsonObject["companies"] ?: return emptyList())
    }
    return emptyList()
}


suspend fun deleteCompany(companyId: Int): Boolean {
    val client = getClient()
    val response: HttpResponse = client.delete("/companies/$companyId")
    println(response)
    return response.status.value in 200..299
}

suspend fun addCompany(company: Company): Boolean {
    val client = getClient()
    val response: HttpResponse = client.post("/companies") {
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(company))
        println(company.toString())
    }
    return response.status.value in 200..299
}