package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import faker.com.ibm.icu.math.BigDecimal
import http.*
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class FormType {
    Form2,
    Form3
}

val faker = Faker()


@Composable
fun Gen() {
    var currentForm by remember { mutableStateOf(FormType.Form2) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { currentForm = FormType.Form2 },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Generiraj podjetje")
            }
            Button(
                onClick = { currentForm = FormType.Form3 },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Generiraj službo")
            }
        }
        DisplayForm(currentForm)
    }
}

@Composable
fun DisplayForm(formType: FormType) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (formType) {
            FormType.Form2 -> Form2Content()
            FormType.Form3 -> Form3Content()
        }
    }
}

@Composable
fun Form2Content() {
    Text("Form 2 content")
}

@Composable
fun Form3Content() {
    var error by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var minPrice by remember { mutableStateOf(0.00) }
    var maxPrice by remember { mutableStateOf(0.00) }
    var minQuantity by remember { mutableStateOf(0) }
    var maxQuantity by remember { mutableStateOf(0) }
    var minTime by remember { mutableStateOf(0) }
    var maxTime by remember { mutableStateOf(0) }
    val (isCheckedHour, setCheckedHour) = remember { mutableStateOf(false) }
    val (isCheckedPiece, setCheckedPiece) = remember { mutableStateOf(false) }
    val (isCheckedArea, setCheckedArea) = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = "Generiranje Službe",
            fontSize = 20.sp
        )


        NumberInputField(
            title = "Količina vpisov",
            value = quantity,
            onValueChange = { newValue ->
                quantity = newValue
            }
        )
        Text("izberi možne tipe službe: (mora biti vsaj 1)")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly

        ) {
            CheckboxWithLabel(
                label = "Uro",
                checked = isCheckedHour,
                onCheckedChange = { setCheckedHour(it) },
            )
            CheckboxWithLabel(
                label = "Kos",
                checked = isCheckedPiece,
                onCheckedChange = { setCheckedPiece(it) },
            )
            CheckboxWithLabel(
                label = "m^2",
                checked = isCheckedArea,
                onCheckedChange = { setCheckedArea(it) },
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
        Text("cena na uro / kos / m^2")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoneyInputField(
                title = "Minimalno",
                value = minPrice,
                onValueChange = { newValue ->
                    minPrice = newValue
                    if (minPrice > maxPrice) {
                        maxPrice = minPrice
                    }
                }
            )
            MoneyInputField(
                title = "Maksimalno",
                value = maxPrice,
                onValueChange = { newValue ->
                    maxPrice = newValue
                    if (maxPrice < minPrice) {
                        minPrice = maxPrice
                    }
                }
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
        Text("koliko ur / kosov / m^2")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumberInputField(
                title = "Minimalno",
                value = minQuantity,
                onValueChange = { newValue ->
                    minQuantity = newValue
                    if (minQuantity > maxQuantity) {
                        maxQuantity = minQuantity
                    }
                }
            )
            NumberInputField(
                title = "Maksimalno",
                value = maxQuantity,
                onValueChange = { newValue ->
                    maxQuantity = newValue
                    if (maxQuantity < minQuantity) {
                        minQuantity = maxQuantity
                    }
                }
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
        Text("Čas porabljen za delo v minutah")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumberInputField(
                title = "Minimalno",
                value = minTime,
                onValueChange = { newValue ->
                    minTime = newValue
                    if (minTime > maxTime) {
                        maxTime = minTime
                    }
                }
            )
            NumberInputField(
                title = "Maksimalno",
                value = maxTime,
                onValueChange = { newValue ->
                    maxTime = newValue
                    if (maxTime < minTime) {
                        minTime = maxTime
                    }
                }
            )
        }
        Text(
            text = error,
            color = Color.Red
        )
        Button(
            onClick = {
                if (!isCheckedHour && !isCheckedPiece && !isCheckedArea) {
                    error = "Izberite vsaj en tip službe"
                } else if (quantity < 1) {
                    error = "Količina vpisov mora biti vsaj 1"
                } else {
                    error = ""
                    coroutineScope.launch {
                        var jobTypeList = mutableListOf<JobType>()

                        val invoiceList: List<Invoice> = getAllInvoices()
                        if (isCheckedHour) jobTypeList = getAllJobTypesOfQuantityType("price_per_hour")
                        if (isCheckedPiece) jobTypeList += getAllJobTypesOfQuantityType("price_per_piece")
                        if (isCheckedArea) jobTypeList += getAllJobTypesOfQuantityType("price_per_area")
                        if (jobTypeList.isEmpty()) error = "ta vrsta tipa službe je prazna"
                        if (invoiceList.isEmpty()) error = "nimamo nobenih računov"
                        else
                            fakeDataJob(
                                quantity,
                                minPrice,
                                maxPrice,
                                minQuantity,
                                maxQuantity,
                                minTime,
                                maxTime,
                                jobTypeList,
                                invoiceList,
                                coroutineScope
                            )
                    }
                }
            }
        ) {
            Text("Generiraj")
        }
    }
}

@Composable
fun NumberInputField(title: String, value: Int, onValueChange: (Int) -> Unit) {
    var processedValue by remember(value) {
        mutableStateOf(value.toString())
    }

    OutlinedTextField(
        value = processedValue,
        onValueChange = {
            val newText = it.filter { char -> char.isDigit() }
            processedValue = newText
            onValueChange(newText.toIntOrNull() ?: 0)
        },
        label = { Text(title) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(16.dp)
            .width(120.dp),
    )
}

@Composable
fun MoneyInputField(title: String, value: Double, onValueChange: (Double) -> Unit) {
    var processedValue by remember(value) {
        mutableStateOf(value.toString())
    }

    OutlinedTextField(
        value = processedValue,
        onValueChange = {
            val newText = it.filter { char -> char.isDigit() || char == '.' }
            processedValue = newText

            val parts = newText.split('.')
            val integerPart = parts.getOrElse(0) { "" }
            val fractionalPart = parts.getOrElse(1) { "" }

            val limitedFractionalPart = if (fractionalPart.length > 2) fractionalPart.take(2) else fractionalPart

            val newValue = if (limitedFractionalPart.isNotEmpty()) {
                "$integerPart.$limitedFractionalPart"
            } else {
                integerPart
            }

            val doubleValue = newValue.toDoubleOrNull() ?: 0.0
            onValueChange(doubleValue)
        },
        label = { Text(title) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(16.dp)
            .width(120.dp),
    )
}

@Composable
fun CheckboxWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(text = label)
    }
}


fun fakeDataJob(
    quantity: Int,
    minPrice: Double,
    maxPrice: Double,
    minQuantity: Int,
    maxQuantity: Int,
    minTime: Int,
    maxTime: Int,
    jobTypeList: List<JobType>,
    invoiceList: List<Invoice>,
    scope: CoroutineScope
) {
    var newJob: Job
    repeat(quantity) {
        val randomPrice = randomDoubleInRange(minPrice, maxPrice)
        val randomQuantity = faker.random.nextInt(minQuantity, maxQuantity)
        val randomTimeTaken = faker.random.nextInt(minTime, maxTime)
        val randomJobTypeId = jobTypeList[Random.nextInt(jobTypeList.size)].id
        val randomInvoiceId = invoiceList[Random.nextInt(invoiceList.size)].id
        var totalPrice = randomPrice * randomQuantity
        totalPrice = BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()

        val newJob = Job(
            id=0,
            quantity = randomQuantity,
            price = randomPrice,
            totalPrice = totalPrice,
            timeTaken = randomTimeTaken,
            invoice_id = randomInvoiceId,
            jobtype_id =randomJobTypeId
        )

        scope.launch {
            val success = addJob(newJob)
            if (success) {
                println("Job added successfully: $newJob")
            } else {
                println("Failed to add job: $newJob")
            }
        }
    }

}


fun randomDoubleInRange(min: Double, max: Double): Double {
    if (min == max)
        return BigDecimal(min).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
    val randomDouble = Random.nextDouble(min, max)
    return BigDecimal(randomDouble).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
}