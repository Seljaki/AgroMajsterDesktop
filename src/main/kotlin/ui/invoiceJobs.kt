package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.*
import kotlinx.coroutines.launch

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
            label = { Text("Naslov") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Opomba") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = started,
            onValueChange = { started = it },
            label = { Text("Začetek (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        CustomDropdownMenu(
            items = companies,
            selectedItem = selectedCustomer,
            onItemSelect = { selectedCustomer = it },
            label = "Izberi stranko",
            itemToString = { it.name }
        )
        CustomDropdownMenu(
            items = companies,
            selectedItem = selectedIssuer,
            onItemSelect = { selectedIssuer = it },
            label = "Izberi delodajalca",
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
                Text("Dodaj račun")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Prekliči")
            }
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice, onClick: () -> Unit, onViewWorks: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = invoice.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Stranka: ${invoice.customer!!.name}", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Delodajalec: ${invoice.issuer!!.name}", fontSize = 16.sp)
            }
            IconButton(onClick = { onViewWorks(invoice.id!!) }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "View Jobs")
            }
        }
    }
}

@Composable
fun InvoiceListScreen(onInvoiceClick: (Invoice) -> Unit, onViewWorks: (Int) -> Unit) {
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
                Text("Ni najdenih računov.")
            } else {
                LazyColumn {
                    items(invoices) { invoice ->
                        InvoiceItem(
                            invoice = invoice,
                            onClick = { onInvoiceClick(invoice) },
                            onViewWorks = { onViewWorks(it) }
                        )
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
                    title = { Text(text = "Dodaj račun") },
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
        Text(text = "Podrobnosti", fontWeight = FontWeight.Bold, fontSize = 24.sp)
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
            Text(text = "ID: ${invoice.id}", fontSize = 18.sp)
            Text(text = "Naslov: ${invoice.title}", fontSize = 18.sp)
            Text(text = "Opomba: ${invoice.note ?: "N/A"}", fontSize = 18.sp)
            Text(text = "Začetek: ${invoice.started}", fontSize = 18.sp)
            Text(text = "Konec: ${invoice.ended ?: "N/A"}", fontSize = 18.sp)
            Text(text = "Rok: ${invoice.dueDate ?: "N/A"}", fontSize = 18.sp)
            Text(text = "Je plačano?: ${if (invoice.isPaid) "Da" else "Ne"}", fontSize = 18.sp)
            Text(text = "Stranka: ${invoice.customer!!.name}", fontSize = 18.sp)
            Text(text = "Delodajalec: ${invoice.issuer!!.name}", fontSize = 18.sp)
            Text(text = "Skupna cena: ${invoice.totalPrice} €", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Nazaj")
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
            label = { Text("Naslov") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = ended,
            onValueChange = { ended = it },
            label = { Text("Konec (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Rok (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        CustomDropdownMenu(
            items = listOf(selectedCustomer),
            selectedItem = selectedCustomer,
            onItemSelect = { selectedCustomer = it },
            label = "Izberi stranko",
            itemToString = { it!!.name }
        )
        CustomDropdownMenu(
            items = listOf(selectedIssuer),
            selectedItem = selectedIssuer,
            onItemSelect = { selectedIssuer = it },
            label = "Izberi delodajalca",
            itemToString = { it!!.name }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isPaid,
                onCheckedChange = { isPaid = it },
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Je plačan")
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
                Text("Uredi račun")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Prekliči")
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
fun AddJobForm(
    onAddJob: (Job) -> Unit,
    onCancel: () -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var timeTaken by remember { mutableStateOf("") }
    var jobTypeId by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var jobTypes by remember { mutableStateOf<List<JobType>>(emptyList()) }
    var selectedJobType by remember { mutableStateOf<JobType?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        jobTypes = getAllJobTypes()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Količina") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = timeTaken,
            onValueChange = { timeTaken = it },
            label = { Text("Porabljen čas") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedJobType?.name ?: "Izberi delo")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                jobTypes.forEach { jobType ->
                    DropdownMenuItem(onClick = {
                        selectedJobType = jobType
                        expanded = false
                    }) {
                        Text(jobType.name)
                    }
                }
            }
        }
        selectedJobType?.let {
            Text("izbrano delo: ${it.name}")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    val newJob = Job(
                        id = null, // id will be assigned by the backend
                        quantity = quantity.toInt(),
                        timeTaken = timeTaken.toInt(),
                        jobtype_id = selectedJobType?.id ?: 0,
                        invoice_id = null
                    )
                    onAddJob(newJob)
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Dodaj delo")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Prekliči")
            }
        }
    }
}


@Composable
fun JobItem(job: Job, onDelete: (Int) -> Unit, onEdit: (Job) -> Unit) {
    var name by remember { mutableStateOf("") }

    LaunchedEffect(job.jobtype_id) {
        val jobType = getJobTypeById(job.jobtype_id)
        if (jobType != null) {
            name = jobType.name
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Delo: $name", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Količina: ${job.quantity}", fontSize = 16.sp)
            }
            IconButton(onClick = { onEdit(job) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Job", tint = Color.Blue)
            }
            IconButton(onClick = { onDelete(job.id ?: return@IconButton) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Job", tint = Color.Red)
            }
        }
    }
}


@Composable
fun EditJobForm(
    job: Job,
    onUpdateJob: (Job) -> Unit,
    onCancel: () -> Unit
) {
    var quantity by remember { mutableStateOf(job.quantity.toString()) }
    var timeTaken by remember { mutableStateOf(job.timeTaken.toString()) }
    var jobTypeId by remember { mutableStateOf(job.jobtype_id.toString()) }
    val coroutineScope = rememberCoroutineScope()

    var jobTypes by remember { mutableStateOf<List<JobType>>(emptyList()) }
    var selectedJobType by remember { mutableStateOf<JobType?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        jobTypes = getAllJobTypes()
        selectedJobType = jobTypes.find { it.id == job.jobtype_id }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Količina ") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = timeTaken,
            onValueChange = { timeTaken = it },
            label = { Text("Porabljen čas") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedJobType?.name ?: "Izberi delo")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                jobTypes.forEach { jobType ->
                    DropdownMenuItem(onClick = {
                        selectedJobType = jobType
                        expanded = false
                    }) {
                        Text(jobType.name)
                    }
                }
            }
        }
        selectedJobType?.let {
            Text("Izbrano delo:: ${it.name}")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    val updatedJob = Job(
                        id = job.id,
                        quantity = quantity.toInt(),
                        timeTaken = timeTaken.toInt(),
                        jobtype_id = selectedJobType?.id ?: 0,
                        invoice_id = job.invoice_id
                    )
                    onUpdateJob(updatedJob)
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Uredi delo")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)) {
                Text("Prekliči")
            }
        }
    }
}



@Composable
fun JobsScreen(invoiceId: Int) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddJobForm by remember { mutableStateOf(false) }
    var showEditJobForm by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(invoiceId) {
        coroutineScope.launch {
            jobs = getAllJobs(invoiceId)
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (jobs.isEmpty()) {
                Text("V tem račuinu ni služb.")
            } else {
                LazyColumn {
                    items(jobs) { job ->
                        JobItem(job = job, onDelete = { jobId ->
                            coroutineScope.launch {
                                if (deleteJob(jobId)) {
                                    jobs = jobs.filterNot { it.id == jobId }
                                }
                            }
                        }, onEdit = { jobToEdit ->
                            showEditJobForm = jobToEdit
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(
                onClick = { showAddJobForm = true },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Job")
            }

            if (showAddJobForm) {
                AlertDialog(
                    onDismissRequest = { showAddJobForm = false },
                    title = { Text(text = "Dodaj novo službo") },
                    text = {
                        AddJobForm(
                            onAddJob = { newJob ->
                                coroutineScope.launch {
                                    newJob.invoice_id = invoiceId
                                    val result = addJob(newJob)
                                    if (result) {
                                        jobs = getAllJobs(invoiceId)
                                        showAddJobForm = false
                                    }
                                }
                            },
                            onCancel = { showAddJobForm = false }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }

            if (showEditJobForm != null) {
                AlertDialog(
                    onDismissRequest = { showEditJobForm = null },
                    title = { Text(text = "Uredi službo") },
                    text = {
                        EditJobForm(
                            job = showEditJobForm!!,
                            onUpdateJob = { updatedJob ->
                                coroutineScope.launch {
                                    val result = updateJob(updatedJob.id!!, updatedJob)
                                    if (result != null) {
                                        jobs = getAllJobs(invoiceId)
                                        showEditJobForm = null
                                    }
                                }
                            },
                            onCancel = { showEditJobForm = null }
                        )
                    },
                    confirmButton = {},
                    dismissButton = {}
                )
            }
        }
    }
}