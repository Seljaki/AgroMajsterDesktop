import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension
import ui.App
import java.awt.Toolkit

fun main() = application {
    val icon = painterResource("logo.png")
    Window(title = "Agro Majster Manager", onCloseRequest = ::exitApplication,
        icon = icon
        ) {
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val screenHeight = screenSize.height

        window.minimumSize = Dimension(650, screenHeight)
        App()
    }
}