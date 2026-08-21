package it.progettiesoluzioni.gestionale.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verifiche_sopralluogo",
    foreignKeys = [
        ForeignKey(
            entity = Sopralluogo::class,
            parentColumns = ["id"],
            childColumns = ["sopralluogoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sopralluogoId")]
)
data class VerificaSopralluogo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sopralluogoId: Long,
    val codice: String,
    val sezione: String,
    val titolo: String,
    val riferimentoNormativo: String = "",
    val esito: String = "DA_VERIFICARE",
    val note: String = "",
    val ordine: Int = 0
)
