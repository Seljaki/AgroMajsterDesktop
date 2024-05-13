package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
enum class MenuState(val menu: String) {
    LOGIN("Log in"),
    MAIN("main window")
}
@Composable
fun Content(menuState: MutableState<MenuState>) {
    when (menuState.value) {
        MenuState.LOGIN -> LoginWindow(menuState)
        MenuState.MAIN -> MainWindow()
    }
}

@Composable
@Preview
fun App() {
    val currentTab = remember { mutableStateOf(MenuState.LOGIN) }

    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Content(currentTab)
    }
}