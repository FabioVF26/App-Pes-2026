package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert
    suspend fun inserisci(cliente: Cliente): Long

    @Update
    suspend fun aggiorna(cliente: Cliente)

    @Delete
    suspend fun elimina(cliente: Cliente)

    @Query("SELECT * FROM clienti WHERE attivo = 1 ORDER BY ragioneSociale ASC")
    fun osservaClienti(): Flow<List<Cliente>>

    @Query("SELECT * FROM clienti WHERE id = :id LIMIT 1")
    fun osservaCliente(id: Long): Flow<Cliente?>

    @Query("SELECT COUNT(*) FROM clienti WHERE attivo = 1")
    fun contaClientiAttivi(): Flow<Int>
}
