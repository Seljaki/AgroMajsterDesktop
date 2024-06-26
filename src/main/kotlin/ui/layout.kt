package ui

import LoginInfo
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import http.SERVER_URL
import http.TOKEN
import loadLoginInfo


enum class MenuState(val menu: String) {
    LOGIN("Log in"),
    MAIN("main window")
}

@Composable
@Preview
fun App() {
    val userInfo = remember {
        val userInfo = loadLoginInfo()
        if(userInfo != null) {
            SERVER_URL = userInfo.hostname
            TOKEN = userInfo.token
        }
        mutableStateOf<LoginInfo?>(userInfo)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        if(userInfo.value == null) {
            LoginWindow(userInfo)
        } else {
            MainWindow(userInfo)
        }
    }
}