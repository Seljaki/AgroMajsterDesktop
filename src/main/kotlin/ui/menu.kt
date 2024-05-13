package ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*

@Composable
fun menu(){
    MenuButton(Icons.Default.Share,"Scraper", onClick = { println("Scraper was clicked")})
    MenuButton(Icons.Default.Edit,"Generater", onClick = { println("Generater was clicked")})
}