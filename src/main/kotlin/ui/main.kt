package ui

import LoginInfo
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.Company
import http.*
import kotlinx.coroutines.launch

sealed class Screen {
    object TempContent : Screen()
    object ListCompanies : Screen()
    object Scraper : Screen()
    object Generator : Screen()
}
@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ListCompanies) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)),
        ) {
            menu(onLogOutClicked = { userInfo.value = null }, onListCompaniesClicked = {
                currentScreen = Screen.ListCompanies
                selectedCompany = null
            }, onGeneratorClicked = {
                currentScreen = Screen.Generator
            })
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            Modifier
                .weight(3f)
                .fillMaxHeight()
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedCompany != null -> CompanyDetailScreen(company = selectedCompany!!, onBack = {
                    selectedCompany = null
                    currentScreen = Screen.ListCompanies
                }, onDelete = {
                    coroutineScope.launch {
                        val success = deleteCompany(selectedCompany!!.id)
                        println(success)
                        if (success) {
                            selectedCompany = null
                            currentScreen = Screen.ListCompanies
                        }
                    }
                })
                currentScreen == Screen.ListCompanies -> CompanyListScreen(onCompanyClick = {
                    selectedCompany = it
                })
                currentScreen == Screen.TempContent -> Text("TEMP CONTENT")
                currentScreen == Screen.Scraper -> Text("Scraper Content")
                currentScreen == Screen.Generator -> Gen()
            }
        }
    }
}
