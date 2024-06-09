package ui.scraper

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.generator.*

enum class ScraperType {
  PlotScraper,
  CompanyScraper,
}


@Composable
fun scraperWindow(){
  var currentForm by remember { mutableStateOf(ScraperType.PlotScraper) }

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
        onClick = { currentForm = ScraperType.PlotScraper },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Pridobi polja")
      }
      Button(
        onClick = { currentForm = ScraperType.CompanyScraper },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Pridobi podjetja")
      }

    }
    DisplayFormScraper(currentForm)
  }

}

@Composable
fun DisplayFormScraper(scraperType: ScraperType) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {

    when (scraperType) {
      ScraperType.PlotScraper -> FormPlotScraper()
      ScraperType.CompanyScraper -> println("WIP")
    }
  }
}