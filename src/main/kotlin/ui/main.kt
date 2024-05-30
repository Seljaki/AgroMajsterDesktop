package ui

import LoginInfo
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
fun CompanyItem(company: Company, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick), // Handle click event
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = company.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            if (company.address != null) {
                Text(text = company.address, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CompanyDetailScreen(company: Company, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Company Details", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Name: ${company.name}", fontSize = 18.sp)
        Text(text = "Address: ${company.address ?: "N/A"}", fontSize = 18.sp)
        Text(text = "Access Token: ${company.accessToken}", fontSize = 18.sp)
        Text(text = "Phone: ${company.phone ?: "N/A"}", fontSize = 18.sp)
        Text(text = "Tax Number: ${company.taxNumber ?: "N/A"}", fontSize = 18.sp)
        Text(text = "IBAN: ${company.iban ?: "N/A"}", fontSize = 18.sp)
        Text(text = "Email: ${company.email ?: "N/A"}", fontSize = 18.sp)
        Text(text = "Is Taxpayer: ${if (company.isTaxpayer) "Yes" else "No"}", fontSize = 18.sp)
        Text(text = "Default Issuer: ${if (company.defaultIssuer) "Yes" else "No"}", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}




@Composable
fun AddCompanyForm(
    newCompanyName: String,
    onNewCompanyNameChange: (String) -> Unit,
    newCompanyAddress: String,
    onNewCompanyAddressChange: (String) -> Unit,
    onAddCompany: () -> Unit,
    onCancel: () -> Unit
) {
    Column {
        TextField(
            value = newCompanyName,
            onValueChange = onNewCompanyNameChange,
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = newCompanyAddress,
            onValueChange = onNewCompanyAddressChange,
            label = { Text("Company Address") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Row {
            Button(onClick = onAddCompany, modifier = Modifier.weight(1f)) {
                Text("Add Company")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
        }
    }
}
@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ListCompanies) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }

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
                })
                currentScreen == Screen.ListCompanies -> CompanyListScreen(onCompanyClick = {
                    selectedCompany = it
                })
                currentScreen == Screen.TempContent -> Text("TEMP CONTENT")
                currentScreen == Screen.Scraper -> Text("Scraper Content")
                currentScreen == Screen.Generator -> Text("Generator Content")
            }
        }
    }
}


@Composable
fun CompanyListScreen(onCompanyClick: (Company) -> Unit) {
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

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (companies.isEmpty()) {
                Text("No companies found.")
            } else {
                LazyColumn {
                    items(companies) { company ->
                        CompanyItem(company = company, onClick = { onCompanyClick(company) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(onClick = { showAddCompanyForm = true }) {
                Text("+")
            }

            if (showAddCompanyForm) {
                AddCompanyForm(
                    newCompanyName = newCompanyName,
                    onNewCompanyNameChange = { newCompanyName = it },
                    newCompanyAddress = newCompanyAddress,
                    onNewCompanyAddressChange = { newCompanyAddress = it },
                    onAddCompany = {
                        coroutineScope.launch {
                            val newCompany = Company(
                                id = 0, // id is set by the server
                                name = newCompanyName,
                                address = newCompanyAddress,
                                accessToken = "",
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
                    },
                    onCancel = {
                        showAddCompanyForm = false
                        newCompanyName = ""
                        newCompanyAddress = ""
                    }
                )
            }
        }
    }
}

