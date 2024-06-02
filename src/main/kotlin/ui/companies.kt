package ui

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.Company
import http.addCompany
import http.getAllCompany
import kotlinx.coroutines.launch

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
fun EditCompanyScreen(company: Company, onBack: () -> Unit, onSave: (Company) -> Unit) {
    var name by remember { mutableStateOf(company.name) }
    var address by remember { mutableStateOf(company.address ?: "") }
    var accessToken by remember { mutableStateOf(company.accessToken) }
    var phone by remember { mutableStateOf(company.phone ?: "") }
    var taxNumber by remember { mutableStateOf(company.taxNumber ?: "") }
    var iban by remember { mutableStateOf(company.iban ?: "") }
    var email by remember { mutableStateOf(company.email ?: "") }
    var isTaxpayer by remember { mutableStateOf(company.isTaxpayer) }
    var defaultIssuer by remember { mutableStateOf(company.defaultIssuer) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Edit Company", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = accessToken,
            onValueChange = { accessToken = it },
            label = { Text("Access Token") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = taxNumber,
            onValueChange = { taxNumber = it },
            label = { Text("Tax Number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = iban,
            onValueChange = { iban = it },
            label = { Text("IBAN") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isTaxpayer,
                onCheckedChange = { isTaxpayer = it }
            )
            Text(text = "Is Taxpayer")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = defaultIssuer,
                onCheckedChange = { defaultIssuer = it }
            )
            Text(text = "Default Issuer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onBack) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val updatedCompany = company.copy(
                    name = name,
                    address = address,
                    accessToken = accessToken,
                    phone = phone,
                    taxNumber = taxNumber,
                    iban = iban,
                    email = email,
                    isTaxpayer = isTaxpayer,
                    defaultIssuer = defaultIssuer
                )
                onSave(updatedCompany)
            }) {
                Text("Save")
            }
        }
    }
}



@Composable
fun CompanyDetailScreen(company: Company, onBack: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Company", tint = Color.Red)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Company", tint = Color.Blue)
            }
        }
    }
}

@Composable
fun AddCompanyForm(
    newCompanyName: String,
    onNewCompanyNameChange: (String) -> Unit,
    newCompanyAddress: String,
    onNewCompanyAddressChange: (String) -> Unit,
    newCompanyAccessToken: String,
    onNewCompanyAccessTokenChange: (String) -> Unit,
    newCompanyPhone: String,
    onNewCompanyPhoneChange: (String) -> Unit,
    newCompanyTaxNumber: String,
    onNewCompanyTaxNumberChange: (String) -> Unit,
    newCompanyIban: String,
    onNewCompanyIbanChange: (String) -> Unit,
    newCompanyEmail: String,
    onNewCompanyEmailChange: (String) -> Unit,
    newCompanyIsTaxpayer: Boolean,
    onNewCompanyIsTaxpayerChange: (Boolean) -> Unit,
    newCompanyDefaultIssuer: Boolean,
    onNewCompanyDefaultIssuerChange: (Boolean) -> Unit,
    onAddCompany: () -> Unit,
    onCancel: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
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
        TextField(
            value = newCompanyAccessToken,
            onValueChange = onNewCompanyAccessTokenChange,
            label = { Text("Access Token") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = newCompanyPhone,
            onValueChange = onNewCompanyPhoneChange,
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = newCompanyTaxNumber,
            onValueChange = onNewCompanyTaxNumberChange,
            label = { Text("Tax Number") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = newCompanyIban,
            onValueChange = onNewCompanyIbanChange,
            label = { Text("IBAN") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = newCompanyEmail,
            onValueChange = onNewCompanyEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = newCompanyIsTaxpayer,
                onCheckedChange = onNewCompanyIsTaxpayerChange,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Is Taxpayer")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = newCompanyDefaultIssuer,
                onCheckedChange = onNewCompanyDefaultIssuerChange,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Default Issuer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onAddCompany, modifier = Modifier.weight(1f)) {
                Text("Add Company")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Cancel")
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
    var newCompanyAccessToken by remember { mutableStateOf("") }
    var newCompanyPhone by remember { mutableStateOf("") }
    var newCompanyTaxNumber by remember { mutableStateOf("") }
    var newCompanyIban by remember { mutableStateOf("") }
    var newCompanyEmail by remember { mutableStateOf("") }
    var newCompanyIsTaxpayer by remember { mutableStateOf(false) }
    var newCompanyDefaultIssuer by remember { mutableStateOf(false) }

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

            FloatingActionButton(
                onClick = { showAddCompanyForm = true },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Company")
            }

            if (showAddCompanyForm) {
                AlertDialog(
                    onDismissRequest = { showAddCompanyForm = false },
                    title = { Text(text = "Add New Company") },
                    text = {
                        AddCompanyForm(
                            newCompanyName = newCompanyName,
                            onNewCompanyNameChange = { newCompanyName = it },
                            newCompanyAddress = newCompanyAddress,
                            onNewCompanyAddressChange = { newCompanyAddress = it },
                            newCompanyAccessToken = newCompanyAccessToken,
                            onNewCompanyAccessTokenChange = { newCompanyAccessToken = it },
                            newCompanyPhone = newCompanyPhone,
                            onNewCompanyPhoneChange = { newCompanyPhone = it },
                            newCompanyTaxNumber = newCompanyTaxNumber,
                            onNewCompanyTaxNumberChange = { newCompanyTaxNumber = it },
                            newCompanyIban = newCompanyIban,
                            onNewCompanyIbanChange = { newCompanyIban = it },
                            newCompanyEmail = newCompanyEmail,
                            onNewCompanyEmailChange = { newCompanyEmail = it },
                            newCompanyIsTaxpayer = newCompanyIsTaxpayer,
                            onNewCompanyIsTaxpayerChange = { newCompanyIsTaxpayer = it },
                            newCompanyDefaultIssuer = newCompanyDefaultIssuer,
                            onNewCompanyDefaultIssuerChange = { newCompanyDefaultIssuer = it },
                            onAddCompany = {
                                coroutineScope.launch {
                                    val newCompany = Company(
                                        id = 0, // id is set by the server
                                        name = newCompanyName,
                                        address = newCompanyAddress,
                                        accessToken = newCompanyAccessToken,
                                        phone = newCompanyPhone,
                                        taxNumber = newCompanyTaxNumber,
                                        iban = newCompanyIban,
                                        email = newCompanyEmail,
                                        isTaxpayer = newCompanyIsTaxpayer,
                                        defaultIssuer = newCompanyDefaultIssuer
                                    )
                                    val result = addCompany(newCompany)
                                    if (result) {
                                        companies = getAllCompany()
                                        showAddCompanyForm = false
                                        newCompanyName = ""
                                        newCompanyAddress = ""
                                        newCompanyAccessToken = ""
                                        newCompanyPhone = ""
                                        newCompanyTaxNumber = ""
                                        newCompanyIban = ""
                                        newCompanyEmail = ""
                                        newCompanyIsTaxpayer = false
                                        newCompanyDefaultIssuer = false
                                    }
                                }
                            },
                            onCancel = {
                                showAddCompanyForm = false
                                newCompanyName = ""
                                newCompanyAddress = ""
                                newCompanyAccessToken = ""
                                newCompanyPhone = ""
                                newCompanyTaxNumber = ""
                                newCompanyIban = ""
                                newCompanyEmail = ""
                                newCompanyIsTaxpayer = false
                                newCompanyDefaultIssuer = false
                            }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
}
