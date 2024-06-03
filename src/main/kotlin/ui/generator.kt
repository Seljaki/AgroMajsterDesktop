package ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.serpro69.kfaker.Faker
import java.math.BigDecimal
import java.math.RoundingMode

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
    var quantity by remember { mutableStateOf(0) }
    var min by remember { mutableStateOf(0.00) }
    var max by remember { mutableStateOf(0.00) }
    val (isChecked1, setChecked1) = remember { mutableStateOf(false) }
    val (isChecked2, setChecked2) = remember { mutableStateOf(false) }
    val (isChecked3, setChecked3) = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
            .padding(16.dp)
    ) {
        NumberInputField(
            title = "Količina",
            value = quantity,
            onValueChange = { newValue ->
                quantity = newValue
            }
        )
        Row{
            CheckboxWithLabel(
                label = "Uro",
                checked = isChecked1,
                onCheckedChange = { setChecked1(it) },
            )
            CheckboxWithLabel(
                label = "m",

                checked = isChecked2,
                onCheckedChange = { setChecked2(it) },
            )
            CheckboxWithLabel(
                label = "Option 1",
                checked = isChecked3,
                onCheckedChange = { setChecked3(it) },
            )
        }
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(300.dp))
        MoneyInputField(
            title = "Minimalna cena",
            value = min,
            onValueChange = { newValue ->
                min = newValue
                if (min > max) {
                    max = min
                }
            }
        )
        MoneyInputField(
            title = "max",
            value = max,
            onValueChange = { newValue ->
                max = newValue
                if (max < min) {
                    min = max
                }
            }
        )
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