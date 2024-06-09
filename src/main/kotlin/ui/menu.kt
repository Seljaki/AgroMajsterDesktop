package ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import logOut

@Composable
fun menu(onLogOutClicked: () -> Unit, onListCompaniesClicked: () -> Unit, onGeneratorClicked: () -> Unit, onScraperClicked: () -> Unit, onInvoicesClicked: () -> Unit){
    MenuButton(Icons.Default.Share,"Scraper", onClick = onScraperClicked)
    MenuButton(Icons.Default.Edit,"Generator", onClick = onGeneratorClicked)
    MenuButton(Icons.Default.Info,"Podjetja", onClick = onListCompaniesClicked)
    MenuButton(Icons.Default.Info, "Računi", onClick =  onInvoicesClicked)
    MenuButton(Icons.Default.Lock,"Odjavi se!", onClick = { logOut(); onLogOutClicked() })
}