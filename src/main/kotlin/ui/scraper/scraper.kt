package ui.scraper

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import http.addCompany
import http.translate
import kotlinx.coroutines.*
import models.Company
import scraper.companyWall.scrapeCompanyWallForCompanies
import scraper.companyWall.scrapeBiziPageForCompanies
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


enum class ScraperType {
  PlotScraper,
  CompanyScraperBizi,
  CompanyScraperCompanyWall,
}


@Composable
fun scraperWindow() {
  var currentForm by remember { mutableStateOf(ScraperType.PlotScraper) }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))
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
        onClick = { currentForm = ScraperType.CompanyScraperBizi },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Pridobi podjetja Bizi")
      }
      Button(
        onClick = { currentForm = ScraperType.CompanyScraperCompanyWall },
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
      ) {
        Text("Pridobi podjetja CompanyWall")
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
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    when (scraperType) {
      ScraperType.PlotScraper -> FormPlotScraper()
      ScraperType.CompanyScraperBizi -> CompanyScraperUI(scraperType)
      ScraperType.CompanyScraperCompanyWall -> CompanyScraperUI(scraperType)
    }
  }
}


@Composable
fun CompanyScraperUI(scraperType: ScraperType) {
  var companies by remember { mutableStateOf(listOf<Company>()) }
  var loading by remember { mutableStateOf(false) }
  var job by remember { mutableStateOf<Job?>(null) }
  var maxCompanies by remember { mutableStateOf("3") }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text("Število podjetij:")
      Spacer(modifier = Modifier.width(8.dp))
      TextField(
        value = maxCompanies,
        onValueChange = { maxCompanies = it },
        modifier = Modifier.width(60.dp)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
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
          companies = when(scraperType) {
            ScraperType.CompanyScraperBizi -> scrapeBiziPageForCompanies(maxCompaniesToScrape = maxCompanies.toInt())
            ScraperType.CompanyScraperCompanyWall -> scrapeCompanyWallForCompanies(maxCompaniesToScrape = maxCompanies.toInt())
            else -> listOf()
          }
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
  val coroutineScope = rememberCoroutineScope()
  var statusMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp)
  ) {
    statusMessage?.let {
      Text(
        text = it,
        color = if (it.startsWith("Success")) MaterialTheme.colors.primary else MaterialTheme.colors.error,
        modifier = Modifier.padding(8.dp)
      )
    }

    companies.forEach { company ->
      Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = 4.dp
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Company: ${company.name}", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Address: ${company.address}", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tax Number: ${company.taxNumber}", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Email: ${company.email}", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(4.dp))
            Text("IBAN: ${company.iban}", style = MaterialTheme.typography.body2)
          }
          IconButton(onClick = {
            coroutineScope.launch {
              val result = addCompany(translate(company))
              statusMessage = if (result) {
                "Podjetje shranjeno v bazo: ${company.name}"
              } else {
                "Podjetja ni mogoče shraniti v bazo: ${company.name}"
              }
            }
          }) {
            Icon(Icons.Default.Add, contentDescription = "Save Company")
          }
        }
      }
    }
  }
}




