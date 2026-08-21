package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.Sede
import kotlinx.coroutines.flow.Flow

@Dao
interface SedeDao {
    @Insert
    suspend fun inserisci(sede: Sede): Long

    @Update
    suspend fun aggiorna(sede: Sede)

    @Delete
    suspend fun elimina(sede: Sede)

    @Query("SELECT * FROM sedi WHERE clienteId = :clienteId ORDER BY principale DESC, nome ASC")
    fun osservaSedi(clienteId: Long): Flow<List<Sede>>

    @Query("SELECT id FROM sedi WHERE clienteId = :clienteId AND indirizzo = :indirizzo COLLATE NOCASE LIMIT 1")
    suspend fun trovaIdPerIndirizzo(clienteId: Long, indirizzo: String): Long?
}
