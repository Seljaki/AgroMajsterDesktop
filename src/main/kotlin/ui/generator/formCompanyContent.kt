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
import http.addCompany
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.iban4j.CountryCode
import org.iban4j.Iban

@Composable
fun FormCompanyContent() {
  var errorText by remember { mutableStateOf("") }
  var errorTextColor by remember { mutableStateOf(Color.Red) }
  var quantity by remember { mutableStateOf(1) }
  var selectedOptionTaxpayer by remember { mutableStateOf("ne") }
  var selectedOptionIssuer by remember { mutableStateOf("ne") }

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
      RadioButtonWithLabel(
        label = "ja",
        selected = selectedOptionTaxpayer == "ja",
        onSelected = { selectedOptionTaxpayer = "ja" }
      )
      RadioButtonWithLabel(
        label = "ne",
        selected = selectedOptionTaxpayer == "ne",
        onSelected = { selectedOptionTaxpayer = "ne" }
      )
      RadioButtonWithLabel(
        label = "oboje",
        selected = selectedOptionTaxpayer == "oboje",
        onSelected = { selectedOptionTaxpayer = "oboje" }
      )

    }
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
    Text("ali je lahko prevzeti izdajatelj: (mora biti vsaj 1)")
    Row(
      horizontalArrangement = Arrangement.SpaceEvenly

    ) {
      RadioButtonWithLabel(
        label = "ja",
        selected = selectedOptionIssuer == "ja",
        onSelected = { selectedOptionIssuer = "ja" }
      )
      RadioButtonWithLabel(
        label = "ne",
        selected = selectedOptionIssuer == "ne",
        onSelected = { selectedOptionIssuer = "ne" }
      )
      RadioButtonWithLabel(
        label = "oboje",
        selected = selectedOptionIssuer == "oboje",
        onSelected = { selectedOptionIssuer = "oboje" }
      )
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
            fakeDataCompany(
              quantity,
              selectedOptionTaxpayer,
              selectedOptionIssuer,
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

fun fakeDataCompany(
  quantity: Int,
  selectedOptionTaxpayer: String,
  selectedOptionIssuer: String,
  scope: CoroutineScope,
  setErrorText: (String) -> Unit,
  setErrorTextColor: (Color) -> Unit
) {
  repeat(quantity) {

    var taxpayerStatus = false
    if (selectedOptionTaxpayer == "oboje" || selectedOptionTaxpayer == "ja") {
      if(faker.random.nextBoolean() || selectedOptionTaxpayer == "ja")
        taxpayerStatus = true
    }

    var issuerStatus = false
    if (selectedOptionIssuer == "oboje" || selectedOptionIssuer == "ja") {
      if(faker.random.nextBoolean() || selectedOptionIssuer == "ja")
        issuerStatus = true
    }
    val newCompany: Company
    if (taxpayerStatus) {
      newCompany = Company(
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
    } else
      newCompany = Company(
        id = 0,
        name = faker.name.firstName() + " " + faker.name.lastName(),
        address = faker.address.fullAddress(),
        isTaxpayer = false,
        defaultIssuer = issuerStatus,
      )
    println("Company added successfully: $newCompany")

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