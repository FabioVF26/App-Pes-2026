package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attivita_scadenze",
    foreignKeys = [ForeignKey(entity = Cliente::class, parentColumns = ["id"], childColumns = ["clienteId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("clienteId"), Index("scadenzaEpochMillis")]
)
data class AttivitaScadenza(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val servizio: String = "GENERALE",
    val titolo: String,
    val descrizione: String = "",
    val priorita: String = "MEDIA",
    val stato: String = "DA_FARE",
    val scadenzaEpochMillis: Long? = null,
    val creataEpochMillis: Long = System.currentTimeMillis(),
    val completataEpochMillis: Long? = null
)
