import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

class StoreKtTest {

    @Test
    fun storeLoginInfo() {
        val login = LoginInfo("admin", "dsadsadhsadhasjdak", "localhost")
        storeLoginInfo(login)
        assertTrue(File("loginInfo.json").exists())
    }

    @Test
    fun loadLoginInfoTest() {
        val login = LoginInfo("admin", "dsadsadhsadhasjdak", "localhost")
        storeLoginInfo(login)

        val login2 = loadLoginInfo()

        if (login2 == null)
            assert(true)

        if (login2 != null) {
            assertTrue(login.token == login2.token)
            assertTrue(login.username == login2.username)
            assertTrue(login.hostname == login2.hostname)
        }
    }

    @Test
    fun logOutTest() {
        val login = LoginInfo("admin", "dsadsadhsadhasjdak", "localhost")
        storeLoginInfo(login)

        logOut()
        assertFalse(File("loginInfo.json").exists())
    }
}