package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sedi",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class Sede(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val clienteId: String,
    val nome: String = "Sede principale",
    val indirizzo: String = "",
    val civico: String = "",
    val cap: String = "",
    val comune: String = "",
    val provincia: String = "",
    val note: String = "",
    val principale: Boolean = true
) {
    fun indirizzoCompleto(): String = listOf(
        listOf(indirizzo, civico).filter { it.isNotBlank() }.joinToString(" "),
        listOf(cap, comune).filter { it.isNotBlank() }.joinToString(" "),
        provincia.takeIf { it.isNotBlank() }
    ).filterNotNull().filter { it.isNotBlank() }.joinToString(", ")
}
