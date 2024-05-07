package http

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class User(val id : Int, val username: String, val password: String? = null, val email: String?)

suspend fun getAllUsers(): Array<User> {
    val client = getClient()
    val response = client.get("/users")
    if (response.status.value in 200..299) {
        val json = Json.parseToJsonElement(response.bodyAsText())
        return Json.decodeFromJsonElement(json.jsonObject["users"] ?: return emptyArray())
    }
    return emptyArray()
}