package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

val json = Json {
    ignoreUnknownKeys = true
}

@Serializable
data class Company(val id : Int, val name: String, val address: String? = null, val accessToken: String? = null,
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
    return response.status.value in 200..299
}

suspend fun getCompanyById(companyId: Int): Company{
    val client = getClient()
    val response: HttpResponse = client.get("/companies/$companyId")
    val jsonElement = Json.parseToJsonElement(response.bodyAsText())
    val companyJson = jsonElement.jsonObject["company"]!!
    return json.decodeFromJsonElement(companyJson)
}

suspend fun editCompany(companyId: Int, company: Company): Boolean{
    var client = getClient()
    val response: HttpResponse = client.put("/companies/$companyId"){
        contentType(ContentType.Application.Json)
        setBody(Json.encodeToJsonElement(company))
    }
    return response.status.value in 200..299
}
suspend fun translate(company: models.Company): http.Company {
    return http.Company(
        id = 0, // Assuming the ID is generated server-side or not needed here
        name = company.name,
        address = company.address,
        accessToken = company.accessToken,
        phone = company.phone,
        taxNumber = company.taxNumber,
        iban = company.iban,
        email = company.email,
        isTaxpayer = company.isTaxpayer,
        defaultIssuer = company.defaultIssuer
    )
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