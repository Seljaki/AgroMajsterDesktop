package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

suspend fun login(username: String, password: String): String? {
    val client = getClient()
    val response = client.post("/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(
            buildJsonObject {
                put("username", username)
                put("password", password)
            }.toString()
        )
    }
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return json["token"]?.jsonPrimitive?.content
    }
    return null
}