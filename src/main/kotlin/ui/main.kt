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
import androidx.compose.material.icons.filled.Edit
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
    object ListInvoices : Screen()
}

@Composable
fun AddInvoiceForm(
    companies: List<Company>,
    onAddInvoice: (Invoice) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var started by remember { mutableStateOf("") }
    var ended by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Company?>(null) }
    var selectedIssuer by remember { mutableStateOf<Company?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = started,
            onValueChange = { started = it },
            label = { Text("Started (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        CustomDropdownMenu(
            items = companies,
            selectedItem = selectedCustomer,
            onItemSelect = { selectedCustomer = it },
            label = "Select Customer",
            itemToString = { it.name }
        )
        CustomDropdownMenu(
            items = companies,
            selectedItem = selectedIssuer,
            onItemSelect = { selectedIssuer = it },
            label = "Select Issuer",
            itemToString = { it.name }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    if (selectedCustomer != null && selectedIssuer != null) {
                        val nullableNote = if (note=="") null else dueDate
                        val newInvoice = Invoice(
                            title = title,
                            note = nullableNote,
                            started = started,
                            ended = null,
                            isPaid = isPaid,
                            dueDate = null,
                            customer_id = selectedCustomer!!.id,
                            issuer_id = selectedIssuer!!.id,
                        )
                        println("Created new invoice: $newInvoice")  // Debug logging
                        onAddInvoice(newInvoice)
                    }
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Add Invoice")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = invoice.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Customer: ${invoice.customer!!.name}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Issuer: ${invoice.issuer!!.name}", fontSize = 16.sp)
        }
    }
}


@Composable
fun InvoiceListScreen(onInvoiceClick: (Invoice) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var invoices by remember { mutableStateOf<List<Invoice>>(emptyList()) }
    var companies by remember { mutableStateOf<List<Company>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddInvoiceForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            invoices = getAllInvoices()
            companies = getAllCompany()
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (invoices.isEmpty()) {
                Text("No invoices found.")
            } else {
                LazyColumn {
                    items(invoices) { invoice ->
                        InvoiceItem(invoice = invoice, onClick = { onInvoiceClick(invoice) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(
                onClick = { showAddInvoiceForm = true },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Invoice")
            }

            if (showAddInvoiceForm) {
                AlertDialog(
                    onDismissRequest = { showAddInvoiceForm = false },
                    title = { Text(text = "Add New Invoice") },
                    text = {
                        AddInvoiceForm(
                            companies = companies,
                            onAddInvoice = { newInvoice ->
                                coroutineScope.launch {
                                    println("Adding new invoice: $newInvoice")  // Debug logging
                                    val result = addInvoice(newInvoice)
                                    if (result) {
                                        invoices = getAllInvoices()
                                        showAddInvoiceForm = false
                                    }
                                }
                            },
                            onCancel = { showAddInvoiceForm = false }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
}

@Composable
fun InvoiceDetailScreen(invoice: Invoice, onBack: () -> Unit, onDelete: () -> Unit, onUpdate: (Invoice) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Invoice Details", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            EditInvoiceForm(
                invoice = invoice,
                onUpdateInvoice = {
                    onUpdate(it)
                    isEditing = false
                },
                onCancel = {
                    isEditing = false
                }
            )
        } else {
            Text(text = "Invoice ID: ${invoice.id}", fontSize = 18.sp)
            Text(text = "Title: ${invoice.title}", fontSize = 18.sp)
            Text(text = "Note: ${invoice.note?: "N/A"}", fontSize = 18.sp)
            Text(text = "Started: ${invoice.started}", fontSize = 18.sp)
            Text(text = "Ended: ${invoice.ended ?: "N/A"}", fontSize = 18.sp)
            Text(text = "Due Date: ${invoice.dueDate ?: "N/A"}", fontSize = 18.sp)
            Text(text = "Is Paid: ${if (invoice.isPaid) "Yes" else "No"}", fontSize = 18.sp)
            Text(text = "Customer: ${invoice.customer!!.name}", fontSize = 18.sp)
            Text(text = "Issuer: ${invoice.issuer!!.name}", fontSize = 18.sp)
            Text(text = "Total price: ${invoice.totalPrice} €", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Invoice", tint = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { isEditing = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Invoice", tint = Color.Blue)
                }
            }
        }
    }
}

@Composable
fun EditInvoiceForm(
    invoice: Invoice,
    onUpdateInvoice: (Invoice) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(invoice.title) }
    var note by remember { mutableStateOf(invoice.note) }
    var started by remember { mutableStateOf(invoice.started) }
    var ended by remember { mutableStateOf(invoice.ended ?: "") }
    var isPaid by remember { mutableStateOf(invoice.isPaid) }
    var dueDate by remember { mutableStateOf(invoice.dueDate ?: "") }
    var selectedCustomer by remember { mutableStateOf(invoice.customer) }
    var selectedIssuer by remember { mutableStateOf(invoice.issuer) }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = ended,
            onValueChange = { ended = it },
            label = { Text("Ended (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Due Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        CustomDropdownMenu(
            items = listOf(selectedCustomer),
            selectedItem = selectedCustomer,
            onItemSelect = { selectedCustomer = it },
            label = "Select Customer",
            itemToString = { it!!.name }
        )
        CustomDropdownMenu(
            items = listOf(selectedIssuer),
            selectedItem = selectedIssuer,
            onItemSelect = { selectedIssuer = it },
            label = "Select Issuer",
            itemToString = { it!!.name }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isPaid,
                onCheckedChange = { isPaid = it },
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Is Paid")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    val nullableDueDate = if (dueDate=="") null else dueDate
                    val nullableEnded = if (ended=="") null else ended
                    val updatedInvoice = Invoice(
                        id = invoice.id,
                        title = title,
                        note = note,
                        started = started,
                        ended = nullableEnded,
                        isPaid = isPaid,
                        dueDate = nullableDueDate,
                        customer_id = selectedCustomer!!.id,
                        issuer_id = selectedIssuer!!.id,
                        customer = selectedCustomer,
                        issuer = selectedIssuer
                    )
                    onUpdateInvoice(updatedInvoice)
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Update Invoice")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun <T> CustomDropdownMenu(
    items: List<T>,
    selectedItem: T?,
    onItemSelect: (T) -> Unit,
    label: String,
    itemToString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedItem?.let { itemToString(it) } ?: "") }

    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    selectedText = itemToString(item)
                    onItemSelect(item)
                    expanded = false
                }) {
                    Text(text = itemToString(item))
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp, color = Color.Gray)
    }
}


@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ListCompanies) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }
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
            },
                onInvoicesClicked = {
                    currentScreen = Screen.ListInvoices
                    selectedInvoice = null
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
                selectedInvoice != null -> InvoiceDetailScreen(invoice = selectedInvoice!!, onBack = {
                    selectedInvoice = null
                    currentScreen = Screen.ListInvoices
                }, onDelete = {
                    coroutineScope.launch {
                        val success = selectedInvoice!!.id?.let { deleteInvoice(it) }
                        println(success)
                        if (success != null && success == true) {
                            selectedInvoice = null
                            currentScreen = Screen.ListInvoices
                        }
                    }
                }, onUpdate = {
                    coroutineScope.launch {
                        val updatedInvoice = updateInvoice(it.id!!, it)
                        if (updatedInvoice != null) {
                            selectedInvoice = updatedInvoice
                        }
                    }
                })
                currentScreen == Screen.ListCompanies -> CompanyListScreen(onCompanyClick = {
                    selectedCompany = it
                })
                currentScreen == Screen.ListInvoices -> InvoiceListScreen(onInvoiceClick = {
                    selectedInvoice = it
                })
                currentScreen == Screen.TempContent -> Text("TEMP CONTENT")
                currentScreen == Screen.Scraper -> Text("Scraper Content")
                currentScreen == Screen.Generator -> Text("Generator Content")
            }
        }
    }
}