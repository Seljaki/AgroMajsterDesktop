package http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

var SERVER_URL = "http://localhost:3333"
var TOKEN = ""

fun getClient(): HttpClient {
    return HttpClient(CIO) {
        install(DefaultRequest) {
            url(SERVER_URL)
            header("x-auth-token", TOKEN)
        }
        install(ContentNegotiation) {
            json()
        }
    }
}


suspend fun main() {
    val token = login("admin", "admin")
    println("Token: $token")
    if (token != null) {
        TOKEN = token
        val users = getAllUsers()
        for(user in users) {
            println(user)
        }
    }
}