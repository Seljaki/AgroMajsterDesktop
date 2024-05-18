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
    var accessToke: String = UUID.randomUUID().toString(),
    var defaultIssuer: Boolean = false
)