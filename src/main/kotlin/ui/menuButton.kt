package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val buttonModifier = Modifier
    .fillMaxWidth()
@Composable
fun MenuButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    TextButton(
        modifier = buttonModifier,
        onClick = onClick,
    ) {
        Row{
            Icon(icon, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = Color.Black)
        }
    }
}