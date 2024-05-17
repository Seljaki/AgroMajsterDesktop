package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Company(
    val name: String,
    val address: String,
    val phone: String?,
    val taxNumber: String?,
    val iban: String?,
    val email: String?,
    val isTaxpayer: Boolean = false,
    val accessToke: String = UUID.randomUUID().toString(),
    val defaultIssuer: Boolean = false
)