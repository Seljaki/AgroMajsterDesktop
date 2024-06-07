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
import io.github.serpro69.kfaker.fakerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random
import org.iban4j.CountryCode
import org.iban4j.Iban

enum class FormType {
    Form2,
    Form3
}

val config = fakerConfig { locale = "de" }
val faker = Faker(config)


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
    var errorText by remember { mutableStateOf("") }
    var errorTextColor by remember { mutableStateOf(Color.Red) }
    var quantity by remember { mutableStateOf(1) }
    val (isCheckedTrueTaxpayer, setCheckedTrueTaxpayer) = remember { mutableStateOf(false) }
    val (isCheckedFalseTaxpayer, setCheckedFalseTaxpayer) = remember { mutableStateOf(false) }
    val (isCheckedTrueIssuer, setCheckedTrueIssuer) = remember { mutableStateOf(false) }
    val (isCheckedFalseIssuer, setCheckedFalseIssuer) = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = "Generiranje podjetja",
            fontSize = 20.sp
        )


        NumberInputField(
            title = "Količina vpisov",
            value = quantity,
            onValueChange = { newValue ->
                quantity = newValue
            }
        )
        Text("ali je lahko davčni zavezanec: (mora biti vsaj 1)")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly

        ) {
            CheckboxWithLabel(
                label = "ja",
                checked = isCheckedTrueTaxpayer,
                onCheckedChange = { setCheckedTrueTaxpayer(it) },
            )
            CheckboxWithLabel(
                label = "ne",
                checked = isCheckedFalseTaxpayer,
                onCheckedChange = { setCheckedFalseTaxpayer(it) },
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
        Text("ali je lahko prevzeti izdajatelj: (mora biti vsaj 1)")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly

        ) {
            CheckboxWithLabel(
                label = "ja",
                checked = isCheckedTrueIssuer,
                onCheckedChange = { setCheckedTrueIssuer(it) },
            )
            CheckboxWithLabel(
                label = "ne",
                checked = isCheckedFalseIssuer,
                onCheckedChange = { setCheckedFalseIssuer(it) },
            )
        }
        Text(
            text = errorText,
            color = errorTextColor
        )
        Button(
            onClick = {
                if (!isCheckedFalseTaxpayer && !isCheckedTrueTaxpayer) {
                    errorText = "Izberite vsaj eno možnost"
                    errorTextColor = Color.Red

                } else if (!isCheckedTrueIssuer && !isCheckedFalseIssuer) {
                    errorText = "Izberite vsaj eno možnost"
                    errorTextColor = Color.Red

                } else if (quantity < 1) {
                    errorText = "Količina vpisov mora biti vsaj 1"
                    errorTextColor = Color.Red
                } else {
                    errorText = ""
                    coroutineScope.launch {
                        fakeDataCompany(
                            quantity,
                            isCheckedTrueTaxpayer,
                            isCheckedFalseTaxpayer,
                            isCheckedTrueIssuer,
                            isCheckedFalseIssuer,
                            coroutineScope,
                            { error -> errorText = error },
                            { color -> errorTextColor = color }
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
fun Form3Content() {
    var errorText by remember { mutableStateOf("") }
    var errorTextColor by remember { mutableStateOf(Color.Red) }
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
            text = "Generiranje službe",
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
            text = errorText,
            color = errorTextColor
        )
        Button(
            onClick = {
                if (!isCheckedHour && !isCheckedPiece && !isCheckedArea) {
                    errorText = "Izberite vsaj en tip službe"
                    errorTextColor = Color.Red

                } else if (quantity < 1) {
                    errorText = "Količina vpisov mora biti vsaj 1"
                    errorTextColor = Color.Red
                } else {
                    errorText = ""
                    coroutineScope.launch {
                        var jobTypeList = mutableListOf<JobType>()

                        val invoiceList: List<Invoice> = getAllInvoices()
                        if (isCheckedHour) jobTypeList = getAllJobTypesOfQuantityType("price_per_hour")
                        if (isCheckedPiece) jobTypeList += getAllJobTypesOfQuantityType("price_per_piece")
                        if (isCheckedArea) jobTypeList += getAllJobTypesOfQuantityType("price_per_area")
                        if (jobTypeList.isEmpty()) {
                            errorText = "ta vrsta tipa službe je prazna"
                            errorTextColor = Color.Red
                        }
                        if (invoiceList.isEmpty()) {
                            errorText = "nimamo nobenih računov"
                            errorTextColor = Color.Red
                        } else
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
                                coroutineScope,
                                { error -> errorText = error },
                                { color -> errorTextColor = color }
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

fun fakeDataCompany(
    quantity: Int,
    isCheckedTrueTaxpayer: Boolean,
    isCheckedFalseTaxpayer: Boolean,
    isCheckedTrueIssuer: Boolean,
    isCheckedFalseIssuer: Boolean,
    scope: CoroutineScope,
    setErrorText: (String) -> Unit,
    setErrorTextColor: (Color) -> Unit
) {
    repeat(quantity) {

        val taxpayerStatus = generateStatus(isCheckedTrueTaxpayer, isCheckedFalseTaxpayer)
        val issuerStatus = generateStatus(isCheckedTrueIssuer, isCheckedFalseIssuer)
        val newCompany: Company
        if(taxpayerStatus){
            newCompany= Company(
                id = 0,
                name = faker.company.name(),
                address = faker.address.fullAddress(),
                phone = faker.phoneNumber.cellPhone.number(),
                taxNumber = generateTax(),
                iban = Iban.random(CountryCode.SI).toString(),
                email = faker.internet.email(),
                isTaxpayer = true,
                defaultIssuer = issuerStatus,
            )
        }
        else
        newCompany = Company(
            id = 0,
            name = faker.name.firstName() +" "+ faker.name.lastName(),
            address = faker.address.fullAddress(),
            isTaxpayer = false,
            defaultIssuer = issuerStatus,
        )
        scope.launch {
            val success = addCompany(newCompany)
            if (success) {
                setErrorText("podjetje uspešno dodano")
                setErrorTextColor(Color.Blue)
                println("Company added successfully: $newCompany")
            } else {
                setErrorText("podjetja ni bilo mogoče dodati")
                setErrorTextColor(Color.Red)
                println("Failed to add Company: $newCompany")
            }
        }
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
    scope: CoroutineScope,
    setErrorText: (String) -> Unit,
    setErrorTextColor: (Color) -> Unit
) {
    repeat(quantity) {
        val randomPrice = randomDoubleInRange(minPrice, maxPrice)
        val randomQuantity = faker.random.nextInt(minQuantity, maxQuantity)
        val randomTimeTaken = faker.random.nextInt(minTime, maxTime)
        val randomJobTypeId = jobTypeList[Random.nextInt(jobTypeList.size)].id
        val randomInvoiceId = invoiceList[Random.nextInt(invoiceList.size)].id
        var totalPrice = randomPrice * randomQuantity
        totalPrice = BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()

        val newJob = Job(
            id = 0,
            quantity = randomQuantity,
            price = randomPrice,
            totalPrice = totalPrice,
            timeTaken = randomTimeTaken,
            invoice_id = randomInvoiceId,
            jobtype_id = randomJobTypeId
        )

        scope.launch {
            val success = addJob(newJob)
            if (success) {
                setErrorText("službe uspešno dodane")
                setErrorTextColor(Color.Blue)
                println("Job added successfully: $newJob")
            } else {
                setErrorText("služb ni bilo mogoče dodati")
                setErrorTextColor(Color.Red)
                println("Failed to add job: $newJob")
            }
        }
    }
}

fun generateTax():String{
    val randomDigits = (0 until 8).map { faker.random.nextInt(0, 9) }.joinToString("")
    return "SI$randomDigits"}


fun randomDoubleInRange(min: Double, max: Double): Double {
    if (min == max)
        return BigDecimal(min).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
    val randomDouble = Random.nextDouble(min, max)
    return BigDecimal(randomDouble).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
}

fun generateStatus(trueStatus: Boolean, falseStatus: Boolean): Boolean {
    return when {
        trueStatus && falseStatus -> faker.random.nextBoolean()
        trueStatus -> true
        falseStatus -> false
        else -> false
    }
}