import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Dimension
import ui.App

fun main() = application {
    val icon = painterResource("logo.png")
    Window(title = "Agro Majster Manager Pro+++", onCloseRequest = ::exitApplication,
        icon = icon
        ) {
        window.minimumSize = Dimension(650, 600)
        App()
    }
}