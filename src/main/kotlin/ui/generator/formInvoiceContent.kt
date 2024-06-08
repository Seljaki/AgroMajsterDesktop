package ui.generator

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.Company
import http.Invoice
import http.getAllCompany
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

@Composable
fun FormInvoiceContent() {
  val currentYear = LocalDate.now().year
  var errorText by remember { mutableStateOf("") }
  var errorTextColor by remember { mutableStateOf(Color.Red) }
  var quantity by remember { mutableStateOf(1) }
  var minYearEnded by remember { mutableStateOf(currentYear) }
  var maxYearEnded by remember { mutableStateOf(currentYear) }
  var minYearDue by remember { mutableStateOf(currentYear) }
  var maxYearDue by remember { mutableStateOf(currentYear) }
  var selectedOptionEnded by remember { mutableStateOf("ne") }
  var selectedOptionDue by remember { mutableStateOf("ne") }
  var selectedOptionPaid by remember { mutableStateOf("ne") }


  val coroutineScope = rememberCoroutineScope()
  Column(
    modifier = Modifier
      .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
      .padding(16.dp),
    verticalArrangement = Arrangement.Center

  ) {
    Text(
      text = "Generiranje računa",
      fontSize = 20.sp
    )
    NumberInputField(
      title = "Količina vpisov",
      value = quantity,
      onValueChange = { newValue ->
        quantity = newValue
      }
    )
    Text("naj bodo generirani računi končani?")
    Row {
      RadioButtonWithLabel(
        label = "ja",
        selected = selectedOptionEnded == "ja",
        onSelected = { selectedOptionEnded = "ja" }
      )
      RadioButtonWithLabel(
        label = "ne",
        selected = selectedOptionEnded == "ne",
        onSelected = { selectedOptionEnded = "ne" }
      )
      RadioButtonWithLabel(
        label = "oboje",
        selected = selectedOptionEnded == "oboje",
        onSelected = { selectedOptionEnded = "oboje" }
      )
    }

    if (selectedOptionEnded == "ja" || selectedOptionEnded == "oboje") {
      Text("podajte območje let (minimum je trenutno leto)")
      Row {
        NumberInputField(
          title = "minimalno",
          value = minYearEnded,
          onValueChange = { newValue ->
            if (newValue >= currentYear) {
              minYearEnded = newValue
              if (minYearEnded > maxYearEnded) {
                maxYearEnded = minYearEnded
              }
            }
          }
        )
        NumberInputField(
          title = "maksimalno",
          value = maxYearEnded,
          onValueChange = { newValue ->
            if (newValue >= minYearEnded) {
              maxYearEnded = newValue
            }
          }
        )
      }
    }
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
    Text("naj bodo računi plačani?")
    Row {
      RadioButtonWithLabel(
        label = "ja",
        selected = selectedOptionPaid == "ja",
        onSelected = { selectedOptionPaid = "ja" }
      )
      RadioButtonWithLabel(
        label = "ne",
        selected = selectedOptionPaid == "ne",
        onSelected = { selectedOptionPaid = "ne" }
      )
      RadioButtonWithLabel(
        label = "oboje",
        selected = selectedOptionPaid == "oboje",
        onSelected = { selectedOptionPaid = "oboje" }
      )
    }
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
    Text("naj imajo generirani računi rok?")
    Row {
      RadioButtonWithLabel(
        label = "ja",
        selected = selectedOptionDue == "ja",
        onSelected = { selectedOptionDue = "ja" }
      )
      RadioButtonWithLabel(
        label = "ne",
        selected = selectedOptionDue == "ne",
        onSelected = { selectedOptionDue = "ne" }
      )
      RadioButtonWithLabel(
        label = "oboje",
        selected = selectedOptionDue == "oboje",
        onSelected = { selectedOptionDue = "oboje" }
      )
    }
    if (selectedOptionDue == "ja" || selectedOptionDue == "oboje") {
      Text("podajte območje let (minimum je trenutno leto)")
      Row {
        NumberInputField(
          title = "minimalno",
          value = minYearDue,
          onValueChange = { newValue ->
            if (newValue >= currentYear) {
              minYearDue = newValue
              if (minYearDue > maxYearDue) {
                maxYearDue = minYearDue
              }
            }
          }
        )
        NumberInputField(
          title = "maksimalno",
          value = maxYearDue,
          onValueChange = { newValue ->
            if (newValue >= minYearDue) {
              maxYearDue = newValue
            }
          }
        )
      }
    }

    Text(
      text = errorText,
      color = errorTextColor
    )
    Button(
      onClick = {
        if (quantity < 1) {
          errorText = "Količina vpisov mora biti vsaj 1"
          errorTextColor = Color.Red
        } else {
          errorText = ""
          coroutineScope.launch {
            val companyList: List<Company> = getAllCompany()
            if (companyList.isEmpty()) {
              errorText = "nimamo nobenih podjetij"
              errorTextColor = Color.Red
            } else
              fakeDataInvoice(
                quantity,
                selectedOptionPaid,
                selectedOptionEnded,
                selectedOptionDue,
                maxYearEnded,
                minYearEnded,
                maxYearDue,
                minYearDue,
                companyList,
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

fun fakeDataInvoice(
  quantity: Int,
  selectedOptionPaid: String,
  selectedOptionEnded: String,
  selectedOptionDue: String,
  maxYearEnded: Int,
  minYearEnded: Int,
  maxYearDue: Int,
  minYearDue: Int,
  companyList: List<Company>,
  scope: CoroutineScope,
  setErrorText: (String) -> Unit,
  setErrorTextColor: (Color) -> Unit
) {
  repeat(quantity) {
    var endedDate = ""
    var dueDate = ""
    var isPaid = false
    val customerNumber =Random.nextInt(companyList.size)
    val issuerNumber =Random.nextInt(companyList.size)
    val randomCustomerId = companyList[customerNumber].id
    val randomIssuerId = companyList[issuerNumber].id
    val customer = companyList[customerNumber]
    val issuer = companyList[issuerNumber]

    if (selectedOptionEnded == "oboje" || selectedOptionEnded == "ja") {
      if(faker.random.nextBoolean() || selectedOptionEnded == "ja")
      endedDate = generateRandomDate(minYearEnded, maxYearEnded).toString()
    }
    if (selectedOptionDue == "oboje" || selectedOptionDue == "ja") {
      if(faker.random.nextBoolean() || selectedOptionDue == "ja")
      dueDate = generateRandomDate(minYearDue, maxYearDue).toString()
    }
    if (selectedOptionPaid == "oboje" || selectedOptionPaid == "ja") {
      if(faker.random.nextBoolean() || selectedOptionPaid == "ja")
        isPaid = true
    }

    val newInvoice= Invoice(
      id = 0,
      title = faker.job.title(),
      note = faker.lorem.words(),
      started = LocalDate.now().toString(),
      ended = endedDate,
      isPaid = isPaid,
      dueDate = dueDate,
      customer_id = randomCustomerId,
      issuer_id = randomIssuerId,
      issuer = issuer,
      customer = customer
    )

    println("new invoice: $newInvoice")
  }
}

fun generateRandomDate(minYear: Int, maxYear: Int): LocalDate {
  val randomYear = if (minYear == maxYear) {
    minYear
  } else {
    Random.nextInt(minYear, maxYear)
  }
  val randomMonth = Random.nextInt(1, 12)
  val randomDay = Random.nextInt(1, LocalDate.of(randomYear, randomMonth, 1).lengthOfMonth())
  return LocalDate.of(randomYear, randomMonth, randomDay)
}