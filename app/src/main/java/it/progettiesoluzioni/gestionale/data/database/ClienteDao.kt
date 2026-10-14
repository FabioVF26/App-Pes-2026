package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun inserisci(cliente: Cliente)

    @Update
    suspend fun aggiorna(cliente: Cliente)

    @Delete
    suspend fun elimina(cliente: Cliente)

    @Query("DELETE FROM clienti WHERE id = :id")
    suspend fun eliminaPerId(id: String)

    @Query("SELECT * FROM clienti WHERE attivo = 1 ORDER BY ragioneSociale ASC")
    fun osservaClienti(): Flow<List<Cliente>>

    @Query("SELECT * FROM clienti WHERE id = :id LIMIT 1")
    fun osservaCliente(id: String): Flow<Cliente?>

    @Query("SELECT COUNT(*) FROM clienti WHERE attivo = 1")
    fun contaClientiAttivi(): Flow<Int>

    @Query("SELECT id FROM clienti WHERE ragioneSociale = :ragioneSociale COLLATE NOCASE LIMIT 1")
    suspend fun trovaIdPerRagioneSociale(ragioneSociale: String): String?

    @Query("SELECT * FROM clienti WHERE id = :id LIMIT 1")
    suspend fun clientePerId(id: String): Cliente?

    @Query("SELECT * FROM clienti ORDER BY ragioneSociale ASC")
    suspend fun tuttiSnapshot(): List<Cliente>

    @Insert
    suspend fun inserisciTutti(items: List<Cliente>)

    @Query("DELETE FROM clienti")
    suspend fun eliminaTutti()

    @Upsert
    suspend fun upsertDaSync(cliente: Cliente)
}
