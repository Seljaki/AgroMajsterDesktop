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
fun EditPlotForm(
    plot: Plot,
    onUpdatePlot: (Plot) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(plot.title) }
    var note by remember { mutableStateOf(plot.note) }
    var plotNumber by remember { mutableStateOf(plot.plotNumber) }
    var cadastralMunicipality by remember { mutableStateOf(plot.cadastralMunicipality) }
    var archived by remember { mutableStateOf(plot.archived) }
    var boundary by remember { mutableStateOf(plot.boundary?.coordinates.toString()) }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naslov") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Opomba") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = plotNumber,
            onValueChange = { plotNumber = it },
            label = { Text("Številka parcele") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = cadastralMunicipality.toString(),
            onValueChange = { cadastralMunicipality = it.toIntOrNull() ?: 0 },
            label = { Text("Katastrska občina") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = boundary,
            onValueChange = { boundary = it },
            label = { Text("Ozemlje (GeoJSON koordinati)") },
            modifier = Modifier.height(100.dp).fillMaxWidth().padding(bottom = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            Checkbox(
                checked = archived,
                onCheckedChange = { archived = it }
            )
            Text(text = "Arhivirano")
        }
        Row {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Prekliči")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    val plotGeoJson = if (boundary.isNotBlank()) {
                        val coordinates = parseGeoJsonCoordinates(boundary)
                        PlotGeoJson(coordinates = coordinates)
                    } else {
                        null
                    }
                    onUpdatePlot(plot.copy(
                        title = title,
                        note = note,
                        plotNumber = plotNumber,
                        cadastralMunicipality = cadastralMunicipality,
                        archived = archived,
                        boundary = plotGeoJson
                    ))
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Shrani")
            }
        }
    }
}

@Composable
fun PlotListScreen(onPlotClick: (Plot) -> Unit, onAddPlotClick: () -> Unit, plots: List<Plot>) {
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
            Icon(Icons.Filled.Add, contentDescription = "Dodaj polje")
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
                    Icon(Icons.Filled.Edit, contentDescription = "Uredi polje", tint = Color.Blue)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Izbriši polje", tint = Color.Red)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = plot.note, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Številka parcele: ${plot.plotNumber}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Katastrska občina: ${plot.cadastralMunicipality}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Arhivirano: ${plot.archived}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Nazaj")
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
        Text(text = "Dodaj novo polje", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naslov") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Opomba") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = size.toString(),
            onValueChange = { size = it.toIntOrNull() ?: 0 },
            label = { Text("Velikost") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = plotNumber,
            onValueChange = { plotNumber = it },
            label = { Text("Število parcle") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cadastralMunicipality.toString(),
            onValueChange = { cadastralMunicipality = it.toIntOrNull() ?: 0 },
            label = { Text("Katastrska občina") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = boundary,
            onValueChange = { boundary = it },
            label = { Text("Ozemlje (GeoJSON Coordinates)") },
            modifier = Modifier.height(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = archived,
                onCheckedChange = { archived = it }
            )
            Text(text = "Arhivirano")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = onBack) {
                Text("Prekliči")
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
                Text("Shrani")
            }
        }
    }
}

@Composable
fun EditPlotScreen(plot: Plot, onBack: () -> Unit, onSave: (Plot) -> Unit) {
    EditPlotForm(
        plot = plot,
        onUpdatePlot = onSave,
        onCancel = onBack
    )
}
