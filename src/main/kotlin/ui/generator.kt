package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.serpro69.kfaker.Faker

val faker = Faker()

enum class FormType {
    Form1,
    Form2,
    Form3
}

@Composable
fun Gen() {
    var currentForm by remember { mutableStateOf(FormType.Form1) }

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
                onClick = { currentForm = FormType.Form1 },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Generiraj zemljišče")
            }
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
            FormType.Form1 -> Form1Content()
            FormType.Form2 -> Form2Content()
            FormType.Form3 -> Form3Content()
        }
    }
}

@Composable
fun Form1Content() {
    Text("Form 1 content")
}

@Composable
fun Form2Content() {
    Text("Form 2 content")
}

@Composable
fun Form3Content() {
    Text("Form 3 content")
}