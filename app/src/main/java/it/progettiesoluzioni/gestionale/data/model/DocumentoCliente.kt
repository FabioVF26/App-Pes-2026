package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documenti_cliente",
    foreignKeys = [
        ForeignKey(entity = Cliente::class, parentColumns = ["id"], childColumns = ["clienteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Sede::class, parentColumns = ["id"], childColumns = ["sedeId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("clienteId"), Index("sedeId"), Index("scadenzaEpochMillis")]
)
data class DocumentoCliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val sedeId: Long? = null,
    val servizio: String = "GENERALE",
    val categoria: String = "Altro",
    val titolo: String,
    val uri: String = "",
    val dataDocumentoEpochMillis: Long? = null,
    val scadenzaEpochMillis: Long? = null,
    val note: String = "",
    val creatoEpochMillis: Long = System.currentTimeMillis()
)
