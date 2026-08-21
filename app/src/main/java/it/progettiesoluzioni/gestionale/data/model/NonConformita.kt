package it.progettiesoluzioni.gestionale.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "non_conformita",
    foreignKeys = [
        ForeignKey(
            entity = Sopralluogo::class,
            parentColumns = ["id"],
            childColumns = ["sopralluogoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VerificaSopralluogo::class,
            parentColumns = ["id"],
            childColumns = ["verificaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sopralluogoId"), Index("verificaId")]
)
data class NonConformita(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sopralluogoId: Long,
    val verificaId: Long,
    val descrizione: String = "",
    val azioneRichiesta: String = "",
    val priorita: String = "MEDIA",
    val stato: String = "APERTA",
    val termineEpochMillis: Long? = null,
    val fotoUri: String = "",
    @ColumnInfo(defaultValue = "''") val fotoRisoluzioneUri: String = "",
    @ColumnInfo(defaultValue = "'DA_VERIFICARE'") val verificaEfficacia: String = "DA_VERIFICARE",
    @ColumnInfo(defaultValue = "''") val noteVerifica: String = "",
    val dataRisoluzioneEpochMillis: Long? = null,
    val dataChiusuraEpochMillis: Long? = null
)
