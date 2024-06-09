package ui.generator

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import faker.com.ibm.icu.math.BigDecimal
import kotlin.random.Random


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
@Composable
fun RadioButtonWithLabel(label: String, selected: Boolean, onSelected: () -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    RadioButton(
      selected = selected,
      onClick = onSelected
    )
    Text(text = label)
  }
}

fun generateTax(): String {
  val randomDigits = (0 until 8).map { faker.random.nextInt(0, 9) }.joinToString("")
  return "SI$randomDigits"
}
fun randomDoubleInRange(min: Double, max: Double): Double {
  if (min == max)
    return BigDecimal(min).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
  val randomDouble = Random.nextDouble(min, max)
  return BigDecimal(randomDouble).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
}