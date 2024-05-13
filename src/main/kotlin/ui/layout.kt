package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class MenuState(val menu: String) {
    LOGIN("Log in"),
}
@Composable
fun Content(menuState: MutableState<MenuState>) {
    when (menuState.value) {
        MenuState.LOGIN -> LoginWindow()
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