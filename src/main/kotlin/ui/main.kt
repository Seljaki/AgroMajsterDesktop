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
import models.Plot
import models.PlotGeoJson

sealed class Screen {
    object TempContent : Screen()
    object ListCompanies : Screen()
    object Scraper : Screen()
    object Generator : Screen()
    object PlotList : Screen() // New screen for displaying plots
    data class PlotDetail(val plot: Plot) : Screen()
    object AddPlot : Screen() // New screen for adding a plot
    data class EditPlot(val plot: Plot) : Screen()
    data class CompanyDetail(val company: Company) : Screen()
    data class EditCompany(val company: Company) : Screen()
}
@Composable
fun MainWindow(userInfo: MutableState<LoginInfo?>) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ListCompanies) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var selectedPlot by remember { mutableStateOf<Plot?>(null) }
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
                selectedPlot = null
            }, onPlotListClicked = {
                currentScreen = Screen.PlotList
                selectedCompany = null
                selectedPlot = null
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
            when (val screen = currentScreen) {
                is Screen.ListCompanies -> CompanyListScreen(onCompanyClick = {
                    selectedCompany = it
                    currentScreen = Screen.CompanyDetail(it)
                })
                is Screen.TempContent -> Text("TEMP CONTENT")
                is Screen.Scraper -> Text("Scraper Content")
                is Screen.Generator -> Text("Generator Content")
                is Screen.PlotList -> PlotListScreen(onPlotClick = {
                    selectedPlot = it
                    currentScreen = Screen.PlotDetail(it)
                }, onAddPlotClick = {
                    currentScreen = Screen.AddPlot
                })
                is Screen.PlotDetail -> PlotDetailScreen(
                    plot = screen.plot,
                    onBack = {
                        currentScreen = Screen.PlotList
                    },
                    onDelete = {
                        coroutineScope.launch {
                            val success = deletePlot(screen.plot.id!!)
                            if (success) {
                                currentScreen = Screen.PlotList
                            }
                        }
                    },
                    onEdit = {
                        currentScreen = Screen.EditPlot(screen.plot)
                    }
                )
                is Screen.AddPlot -> AddPlotScreen(onBack = {
                    currentScreen = Screen.PlotList
                }, onSave = { plot ->
                    coroutineScope.launch {
                        val success = postPlot(plot)
                        if (success) {
                            currentScreen = Screen.PlotList
                        }
                    }
                })
                is Screen.EditPlot -> EditPlotScreen(
                    plot = screen.plot,
                    onBack = {
                        currentScreen = Screen.PlotList
                    },
                    onSave = { updatedPlot ->
                        coroutineScope.launch {
                            val success = editPlot(updatedPlot, screen.plot.id!!)
                            if (success) {
                                currentScreen = Screen.PlotList
                            }
                        }
                    }
                )
                is Screen.CompanyDetail -> CompanyDetailScreen(
                    company = screen.company,
                    onBack = {
                        currentScreen = Screen.ListCompanies
                    },
                    onDelete = {
                        coroutineScope.launch {
                            val success = deleteCompany(screen.company.id)
                            if (success) {
                                currentScreen = Screen.ListCompanies
                                selectedCompany = null
                            }
                        }
                    },
                    onEdit = {
                        currentScreen = Screen.EditCompany(screen.company)
                    }
                )
                is Screen.EditCompany -> EditCompanyScreen(
                    company = screen.company,
                    onBack = {
                        currentScreen = Screen.CompanyDetail(screen.company)
                    },
                    onSave = { updatedCompany ->
                        coroutineScope.launch {
                            val success = editCompany(screen.company.id, updatedCompany)
                            if (success) {
                                currentScreen = Screen.CompanyDetail(updatedCompany)
                                selectedCompany = updatedCompany
                            }
                        }
                    }
                )
            }
        }
    }
}


