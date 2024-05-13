package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import http.SERVER_URL
import http.TOKEN
import http.login
import kotlinx.coroutines.launch

@Composable
fun LoginWindow(menuState: MutableState<MenuState>) {
    var url by remember { mutableStateOf(SERVER_URL) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }


    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Log in",
                fontWeight = FontWeight.Bold,
                fontSize = 3.em
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Host url") },
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = loginError,
                color = Color.Red
            )
            Button(
                onClick = {
                    if (url.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        loginError = "Fill in all fields"
                    } else {
                        scope.launch {
                            val token = login(username, password)
                            if (token != null) {
                                SERVER_URL = url
                                TOKEN = token
                                loginError=""
                                menuState.value = MenuState.MAIN
                            } else loginError = "Invalid data"
                        }
                    }
                }
            ) {
                Text("Log in")
            }
        }
    }


}