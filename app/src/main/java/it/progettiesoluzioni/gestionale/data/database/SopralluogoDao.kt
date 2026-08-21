package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import kotlinx.coroutines.flow.Flow

@Dao
interface SopralluogoDao {
    @Insert
    suspend fun inserisci(sopralluogo: Sopralluogo): Long

    @Query("SELECT * FROM sopralluoghi ORDER BY dataOraEpochMillis DESC")
    fun osservaTutti(): Flow<List<Sopralluogo>>

    @Query("SELECT * FROM sopralluoghi WHERE clienteId = :clienteId ORDER BY dataOraEpochMillis DESC")
    fun osservaPerCliente(clienteId: Long): Flow<List<Sopralluogo>>
}
