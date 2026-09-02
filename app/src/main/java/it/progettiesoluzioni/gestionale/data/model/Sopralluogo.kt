package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "sopralluoghi",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sede::class,
            parentColumns = ["id"],
            childColumns = ["sedeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId"), Index("sedeId")]
)
data class Sopralluogo(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val clienteId: String,
    val sedeId: String,
    val tipoServizio: String,
    val dataOraEpochMillis: Long,
    val stato: String = "BOZZA",
    val noteGenerali: String = ""
)
