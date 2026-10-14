package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.Sede
import kotlinx.coroutines.flow.Flow

@Dao
interface SedeDao {
    @Insert
    suspend fun inserisci(sede: Sede)

    @Update
    suspend fun aggiorna(sede: Sede)

    @Delete
    suspend fun elimina(sede: Sede)

    @Query("DELETE FROM sedi WHERE id = :id")
    suspend fun eliminaPerId(id: String)

    @Query("SELECT * FROM sedi WHERE clienteId = :clienteId ORDER BY principale DESC, nome ASC")
    fun osservaSedi(clienteId: String): Flow<List<Sede>>

    @Query("SELECT id FROM sedi WHERE clienteId = :clienteId AND indirizzo = :indirizzo COLLATE NOCASE LIMIT 1")
    suspend fun trovaIdPerIndirizzo(clienteId: String, indirizzo: String): String?

    @Query("SELECT * FROM sedi ORDER BY clienteId, principale DESC, nome")
    suspend fun tutteSnapshot(): List<Sede>

    @Insert
    suspend fun inserisciTutte(items: List<Sede>)

    @Query("DELETE FROM sedi")
    suspend fun eliminaTutte()

    @Upsert
    suspend fun upsertDaSync(sede: Sede)
}
