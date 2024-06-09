package ui

import LoginInfo
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import http.Company
import http.*
import kotlinx.coroutines.launch
import ui.generator.Gen
import ui.scraper.scraperWindow

sealed class Screen {
    object TempContent : Screen()
    object ListCompanies : Screen()
    object Scraper : Screen()
    object Generator : Screen()
    object ListInvoices : Screen()
    object TestContent : Screen()
    object Jobs : Screen()

}



@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ListCompanies) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }
    var invoiceIdForJobs by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)),
        ) {
            menu(onLogOutClicked = { userInfo.value = null }, onListCompaniesClicked = {
                currentScreen = Screen.ListCompanies
                selectedCompany = null
            },
                onInvoicesClicked = {
                    currentScreen = Screen.ListInvoices
                    selectedInvoice = null
                }, onGeneratorClicked = {
                    currentScreen = Screen.Generator
                }, onScraperClicked = {
                    currentScreen = Screen.Scraper
                })
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            Modifier
                .weight(3f)
                .fillMaxHeight()
                .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedCompany != null -> CompanyDetailScreen(company = selectedCompany!!, onBack = {
                    selectedCompany = null
                    currentScreen = Screen.ListCompanies
                }, onDelete = {
                    coroutineScope.launch {
                        val success = deleteCompany(selectedCompany!!.id)
                        println(success)
                        if (success) {
                            selectedCompany = null
                            currentScreen = Screen.ListCompanies
                        }
                    }
                })
                selectedInvoice != null -> InvoiceDetailScreen(invoice = selectedInvoice!!, onBack = {
                    selectedInvoice = null
                    currentScreen = Screen.ListInvoices
                }, onDelete = {
                    coroutineScope.launch {
                        val success = selectedInvoice!!.id?.let { deleteInvoice(it) }
                        println(success)
                        if (success != null && success == true) {
                            selectedInvoice = null
                            currentScreen = Screen.ListInvoices
                        }
                    }
                }, onUpdate = {
                    coroutineScope.launch {
                        val updatedInvoice = updateInvoice(it.id!!, it)
                        if (updatedInvoice != null) {
                            selectedInvoice = updatedInvoice
                        }
                    }
                })
                currentScreen == Screen.ListCompanies -> CompanyListScreen(onCompanyClick = {
                    selectedCompany = it
                })
                currentScreen == Screen.ListInvoices -> InvoiceListScreen(onInvoiceClick = {
                    selectedInvoice = it
                }, onViewWorks = { invoiceId ->
                    println("Navigating to Jobs screen with invoiceId: $invoiceId")
                    invoiceIdForJobs = invoiceId
                    currentScreen = Screen.Jobs
                })
                currentScreen == Screen.TempContent -> Text("TEMP CONTENT")
                currentScreen == Screen.Scraper -> scraperWindow()
                currentScreen == Screen.Generator -> Gen()
                currentScreen == Screen.TestContent -> Text("TEST CONTENT")
                currentScreen == Screen.Jobs -> invoiceIdForJobs?.let { JobsScreen(it) }
            }
        }
    }
}

