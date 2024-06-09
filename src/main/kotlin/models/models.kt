package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Company(
    var name: String = "",
    var address: String = "",
    var phone: String? = null,
    var taxNumber: String? = null,
    var iban: String? = null,
    var email: String? = null,
    var isTaxpayer: Boolean = false,
    var accessToken: String = UUID.randomUUID().toString(),
    var defaultIssuer: Boolean = false
)



@Serializable
data class crsProp(
    val name: String = "urn:ogc:def:crs:EPSG::3794"
)
@Serializable
data class crs(
    val type: String = "name",
    val properties: crsProp = crsProp()
)
@Serializable
data class PlotGeoJsonMultiPolygon (
    val type: String,
    val coordinates: List<List<List<List<Double>>>>,
    val crs: crs = crs()
)

@Serializable
data class PlotGeoJson (
    val type: String = "Polygon",
    val coordinates: List<List<List<Double>>>,
    val crs: crs = crs()
)

@Serializable
data class Plot(
    var title: String = "",
    var note: String = "",
    var boundary: PlotGeoJson? = null,
    var plotNumber: String = "GERK",
    var cadastralMunicipality: Int = -1,
    var archived: Boolean = false,
    var id: Int? = null,
)