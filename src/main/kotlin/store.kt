import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class LoginInfo(val username: String, val token: String, val hostname: String)

fun storeLoginInfo(loginInfo: LoginInfo) {
    val json = Json.encodeToString(loginInfo)
    File("loginInfo.json").writeText(json)
}

fun loadLoginInfo(): LoginInfo? {
    try {
        val json = File("loginInfo.json").readText()
        return Json.decodeFromString<LoginInfo>(json)
    } catch (e: Exception) {
        println("No login info found")
    }
    return null
}

fun logOut() {
    try {
        File("loginInfo.json").delete()
    } catch (e: Exception) {
        println("Error deleting file")
    }
}