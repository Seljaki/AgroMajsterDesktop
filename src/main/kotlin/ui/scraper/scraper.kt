package ui.scraper

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import models.Company
import scraper.companyWall.scrapeCompanyWallForCompanies

enum class ScraperType {
  PlotScraper,
  CompanyScraper,
}

@Composable
fun scraperWindow() {
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
      ScraperType.CompanyScraper -> CompanyScraperUI()
    }
  }
}

@Composable
fun CompanyScraperUI() {
  var companies by remember { mutableStateOf(listOf<Company>()) }
  var loading by remember { mutableStateOf(false) }
  var job by remember { mutableStateOf<Job?>(null) }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (loading) {
      Text("Scraping v teku...")
      Button(onClick = {
        job?.cancel()
        loading = false
      }) {
        Text("Končaj")
      }
    } else {
      Button(onClick = {
        loading = true
        job = CoroutineScope(Dispatchers.IO).launch {
          companies = scrapeCompanyWallForCompanies()
          loading = false
        }
      }) {
        Text("Začni")
      }
      Spacer(modifier = Modifier.height(16.dp))
      CompanyList(companies)
    }
  }
}

@Composable
fun CompanyList(companies: List<Company>) {
  Column {
    companies.forEach { company ->
      Text("Company: ${company.name}")
      Text("Address: ${company.address}")
      Text("Tax Number: ${company.taxNumber}")
      Text("Email: ${company.email}")
      Text("IBAN: ${company.iban}")
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

