package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "clienti")
data class Cliente(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ragioneSociale: String,
    val nomeCommerciale: String = "",
    val partitaIva: String = "",
    val codiceFiscale: String = "",
    val codiceAteco: String = "",
    val attivita: String = "",
    val legaleRappresentante: String = "",
    val telefono: String = "",
    val email: String = "",
    val pec: String = "",
    val note: String = "",
    val servizioHaccp: Boolean = false,
    val servizioSicurezza: Boolean = false,
    val servizioGdpr: Boolean = false,
    val attivo: Boolean = true
)
