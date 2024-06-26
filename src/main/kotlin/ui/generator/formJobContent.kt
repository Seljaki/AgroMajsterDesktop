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
import faker.com.ibm.icu.math.BigDecimal
import http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun FormJobContent() {
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
      invoice_id = randomInvoiceId!!,
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