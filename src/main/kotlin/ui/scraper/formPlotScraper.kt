package ui.scraper

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.postPlot
import kotlinx.serialization.json.Json
import models.Plot
import models.PlotGeoJson
import models.PlotGeoJsonMultiPolygon
import org.geotools.api.feature.simple.SimpleFeature
import scraper.gerk.*

@Composable
fun FormPlotScraper() {
  val coroutineScope = rememberCoroutineScope()
  var longitudeInput by remember { mutableStateOf("") }
  var latitudeInput by remember { mutableStateOf("") }
  var longitude by remember { mutableStateOf<Double?>(null) }
  var latitude by remember { mutableStateOf<Double?>(null) }
  var longitudeError by remember { mutableStateOf<String?>(null) }
  var latitudeError by remember { mutableStateOf<String?>(null) }
  var result: SimpleFeature? by remember { mutableStateOf(null) }
  var boundedText by remember { mutableStateOf("") }
  fun updateBoundedText(newText: String) {
    boundedText += newText + "\n"
  }

  var isLoading by remember { mutableStateOf(false) }
 var triggerCoroutinePost by remember { mutableStateOf(false) }

  LaunchedEffect(triggerCoroutinePost) {
    if (triggerCoroutinePost) {
      if (result != null) {
        updateBoundedText("Pretvarjanje v GeoJson.")
        val geom = simpleFeatureToGeoJson(result!!)
        val gjson = Json.decodeFromString<PlotGeoJsonMultiPolygon>(geom)
        val gjson2: PlotGeoJson = geoJsonMPToPolygon(gjson)
        val plot = Plot("KOTLIN GERK", "TETS KOTLIN", gjson2)
        postPlot(plot)
        updateBoundedText("Uspešno shranjeno v bazo.")
      } else {
        updateBoundedText("Prišlo je do napake pri shranjevanju polja.")
      }

      triggerCoroutinePost = false
    }
  }

  Column(
    modifier = Modifier
      .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
      .padding(16.dp),
    verticalArrangement = Arrangement.Center
  ) {
    if (isLoading) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
      Text(
        text = "Prenašanje datoteke\nZgrabite si kavico ☕",
        fontSize = 20.sp
      )
    } else {
      Text(
        text = "Pridobi polja",
        fontSize = 20.sp
      )

      Row(
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Button(
          onClick = {
            coroutineScope.launch {
              isLoading = true
              updateGERKData()
              isLoading = false
              updateBoundedText("Končano prenašanje datotek")
            }
          },
          modifier = Modifier.padding(end = 8.dp)
        ) {
          Text("Pridobi z GERK-a")
        }
        Button(onClick = {
          coroutineScope.launch {
            updateBoundedText("Brisanje GERK podatkov.")
            coroutineScope.launch {
              deleteGERKData()
              updateBoundedText("Končano.")
            }
          }
        }) {
          Text("Počisti podatke")
        }
      }

      Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.width(450.dp))

      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(top = 16.dp)
      ) {
        Column {
          TextField(
            label = { Text("Longituda") },
            value = longitudeInput,
            onValueChange = { newValue ->
              longitudeInput = newValue
              val parsedValue = newValue.toDoubleOrNull()
              if (parsedValue == null && newValue.isNotEmpty()) {
                longitudeError = "neveljavna vrednost"
              } else {
                longitudeError = null
                longitude = parsedValue
              }
            },
            isError = longitudeError != null,
            modifier = Modifier.width(150.dp)
          )
          if (longitudeError != null) {
            Text(
              text = longitudeError!!,
              color = Color.Red,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          TextField(
            label = { Text("Latituda") },
            value = latitudeInput,
            onValueChange = { newValue ->
              latitudeInput = newValue
              val parsedValue = newValue.toDoubleOrNull()
              if (parsedValue == null && newValue.isNotEmpty()) {
                latitudeError = "neveljavna vrednost"
              } else {
                latitudeError = null
                latitude = parsedValue
              }
            },
            isError = latitudeError != null,
            modifier = Modifier.width(150.dp)
          )
          if (latitudeError != null) {
            Text(
              text = latitudeError!!,
              color = Color.Red,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
        Button(
          onClick = {
            if (latitude != null && longitude != null) {
              coroutineScope.launch {
                result = findFeatureByCoordinates(findGERKShapefile(), longitude!!, latitude!!) { newBoundedText ->
                  updateBoundedText(newBoundedText)
                }
              }
            } else {
              if (longitude == null) longitudeError = "neveljavna vrednost"
              if (latitude == null) latitudeError = "neveljavna vrednost"
            }
          },
          modifier = Modifier.padding(start = 16.dp)
        ) {
          Text("Poišči polje")
        }
      }
      Spacer(modifier = Modifier.width(16.dp))
      Spacer(modifier = Modifier.height(16.dp))
      BoundedTextBox(boundedText)
      Button(onClick = {
        triggerCoroutinePost=true
      }) {
        Text("Shrani v bazo")
      }
    }
  }
}

@Composable
fun BoundedTextBox(text: String) {
  val scrollState = rememberScrollState()

  Box(
    modifier = Modifier
      .size(width = 450.dp, height = 200.dp)
      .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
      .padding(8.dp)
      .clipToBounds()
      .verticalScroll(scrollState)
  ) {
    Text(
      text = text,
      fontSize = 14.sp,
      overflow = TextOverflow.Clip,
      modifier = Modifier.fillMaxSize()
    )
  }
}