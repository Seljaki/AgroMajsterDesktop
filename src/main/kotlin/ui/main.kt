package ui

import LoginInfo
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
fun CompanyListScreen() {
    val coroutineScope = rememberCoroutineScope()
    var companies by remember { mutableStateOf<List<Company>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddCompanyForm by remember { mutableStateOf(false) }
    var newCompanyName by remember { mutableStateOf("") }
    var newCompanyAddress by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            companies = getAllCompany()
            isLoading = false
        }
    }

    Column {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (companies.isEmpty()) {
                Text("No companies found.")
            } else {
                LazyColumn {
                    items(companies) { company ->
                        Text(text = company.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(onClick = { showAddCompanyForm = true }) {
                Text("+")
            }

            if (showAddCompanyForm) {
                Column {
                    TextField(
                        value = newCompanyName,
                        onValueChange = { newCompanyName = it },
                        label = { Text("Company Name") }
                    )
                    TextField(
                        value = newCompanyAddress,
                        onValueChange = { newCompanyAddress = it },
                        label = { Text("Company Address") }
                    )
                    Row {
                        Button(onClick = {
                            coroutineScope.launch {
                                val newCompany = Company(
                                    id = 0, // id je lahko 0 ali kakršna koli privzeta vrednost, ker ga dodeli strežnik
                                    name = newCompanyName,
                                    address = newCompanyAddress,
                                    accessToken = false,
                                    phone = null,
                                    taxNumber = null,
                                    iban = null,
                                    email = null,
                                    isTaxpayer = false,
                                    defaultIssuer = false
                                )
                                val result = addCompany(newCompany)
                                if (result) {
                                    companies = getAllCompany()
                                    showAddCompanyForm = false
                                    newCompanyName = ""
                                    newCompanyAddress = ""
                                }
                            }
                        }) {
                            Text("Add Company")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            showAddCompanyForm = false
                            newCompanyName = ""
                            newCompanyAddress = ""
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.TempContent) }
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
            menu(onLogOutClicked = { userInfo.value = null }, onListCompaniesClicked = { currentScreen = Screen.ListCompanies })
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            Modifier
                .weight(3f)
                .fillMaxHeight()
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                Screen.TempContent -> Text("TEMP CONTENT")
                Screen.ListCompanies -> CompanyListScreen()
                Screen.Scraper -> Text("Scraper Content")
                Screen.Generator -> Text("Generator Content")
            }
        }
    }
}
