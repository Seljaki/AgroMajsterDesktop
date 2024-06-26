package ui.generator

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig

enum class FormType {
  FormInvoice,
  FormCompany,
  FormJob
}

val config = fakerConfig { locale = "de" }
val faker = Faker(config)


@Composable
fun Gen() {
  var currentForm by remember { mutableStateOf(FormType.FormInvoice) }

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
        onClick = { currentForm = FormType.FormInvoice },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Generiraj račun")
      }
      Button(
        onClick = { currentForm = FormType.FormCompany },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Generiraj podjetje")
      }
      Button(
        onClick = { currentForm = FormType.FormJob },
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
      FormType.FormInvoice -> FormInvoiceContent()
      FormType.FormCompany -> FormCompanyContent()
      FormType.FormJob -> FormJobContent()
    }
  }
}