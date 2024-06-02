package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.getAllPlots
import http.parseGeoJsonCoordinates
import kotlinx.coroutines.launch
import models.Plot
import models.PlotGeoJson


@Composable
fun EditPlotScreen(plot: Plot, onBack: () -> Unit, onSave: (Plot) -> Unit) {
    var title by remember { mutableStateOf(plot.title) }
    var note by remember { mutableStateOf(plot.note) }
    var plotNumber by remember { mutableStateOf(plot.plotNumber) }
    var cadastralMunicipality by remember { mutableStateOf(plot.cadastralMunicipality) }
    var archived by remember { mutableStateOf(plot.archived) }
    var boundary by remember { mutableStateOf(plot.boundary?.coordinates.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Edit Plot", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = plotNumber,
            onValueChange = { plotNumber = it },
            label = { Text("Plot Number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cadastralMunicipality.toString(),
            onValueChange = { cadastralMunicipality = it.toIntOrNull() ?: 0 },
            label = { Text("Cadastral Municipality") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = boundary,
            onValueChange = { boundary = it },
            label = { Text("Boundary (GeoJSON Coordinates)") },
            modifier = Modifier.height(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = archived,
                onCheckedChange = { archived = it }
            )
            Text(text = "Archived")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onBack) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val plotGeoJson = if (boundary.isNotBlank()) {
                    val coordinates = parseGeoJsonCoordinates(boundary)
                    PlotGeoJson(coordinates = coordinates)
                } else {
                    null
                }
                onSave(Plot(title, note, plotGeoJson, plotNumber, cadastralMunicipality, archived))
            }) {
                Text("Save")
            }
        }
    }
}
@Composable
fun PlotListScreen(onPlotClick: (Plot) -> Unit, onAddPlotClick: () -> Unit) {
    var plots by remember { mutableStateOf(listOf<Plot>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            plots = getAllPlots().toList() // Fetch plots using the provided function
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(plots) { plot ->
                PlotItem(plot, onClick = { onPlotClick(plot) })
            }
        }

        FloatingActionButton(
            onClick = onAddPlotClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Plot")
        }
    }
}

@Composable
fun PlotItem(plot: Plot, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = plot.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}

@Composable
fun PlotDetailScreen(plot: Plot, onBack: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = plot.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Plot", tint = Color.Blue)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Plot", tint = Color.Red)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = plot.note, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Plot Number: ${plot.plotNumber}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Cadastral Municipality: ${plot.cadastralMunicipality}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Archived: ${plot.archived}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
@Composable
fun AddPlotScreen(onBack: () -> Unit, onSave: (Plot) -> Unit) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var size by remember { mutableStateOf(0) }
    var plotNumber by remember { mutableStateOf("") }
    var cadastralMunicipality by remember { mutableStateOf(0) }
    var archived by remember { mutableStateOf(false) }
    var boundary by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Add New Plot", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = size.toString(),
            onValueChange = { size = it.toIntOrNull() ?: 0 },
            label = { Text("Size") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = plotNumber,
            onValueChange = { plotNumber = it },
            label = { Text("Plot Number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cadastralMunicipality.toString(),
            onValueChange = { cadastralMunicipality = it.toIntOrNull() ?: 0 },
            label = { Text("Cadastral Municipality") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = boundary,
            onValueChange = { boundary = it },
            label = { Text("Boundary (GeoJSON Coordinates)") },
            modifier = Modifier.height(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = archived,
                onCheckedChange = { archived = it }
            )
            Text(text = "Archived")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onBack) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val plotGeoJson = if (boundary.isNotBlank()) {
                    val coordinates = parseGeoJsonCoordinates(boundary)
                    PlotGeoJson(coordinates = coordinates)
                } else {
                    null
                }
                onSave(Plot(title, note, plotGeoJson, plotNumber, cadastralMunicipality, archived))
            }) {
                Text("Save")
            }
        }
    }
}