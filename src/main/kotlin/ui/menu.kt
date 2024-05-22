package ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import http.TOKEN
import logOut

@Composable
fun menu(onLogOutClicked: () -> Unit, onListCompaniesClicked: () -> Unit){
    MenuButton(Icons.Default.Share,"Scraper", onClick = { println("Scraper was clicked")})
    MenuButton(Icons.Default.Edit,"Generater", onClick = { println("Generater was clicked $TOKEN")})
    MenuButton(Icons.Default.Info,"List Companies", onClick = onListCompaniesClicked)
    MenuButton(Icons.Default.Lock,"Log out", onClick = { logOut(); onLogOutClicked() })
}