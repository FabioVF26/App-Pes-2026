package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import kotlinx.coroutines.flow.Flow

@Dao
interface AttivitaScadenzaDao {
    @Insert suspend fun inserisci(item: AttivitaScadenza): Long
    @Update suspend fun aggiorna(item: AttivitaScadenza)
    @Delete suspend fun elimina(item: AttivitaScadenza)
    @Query("SELECT * FROM attivita_scadenze ORDER BY CASE WHEN scadenzaEpochMillis IS NULL THEN 1 ELSE 0 END, scadenzaEpochMillis ASC, creataEpochMillis DESC")
    fun osservaTutte(): Flow<List<AttivitaScadenza>>
    @Query("SELECT * FROM attivita_scadenze WHERE clienteId = :clienteId ORDER BY CASE WHEN scadenzaEpochMillis IS NULL THEN 1 ELSE 0 END, scadenzaEpochMillis ASC")
    fun osservaPerCliente(clienteId: Long): Flow<List<AttivitaScadenza>>
    @Query("SELECT * FROM attivita_scadenze WHERE id = :id LIMIT 1") suspend fun perId(id: Long): AttivitaScadenza?
    @Query("SELECT * FROM attivita_scadenze WHERE stato != 'COMPLETATA' AND promemoriaAttivo = 1 AND scadenzaEpochMillis IS NOT NULL AND scadenzaEpochMillis BETWEEN :da AND :a ORDER BY scadenzaEpochMillis ASC")
    suspend fun attiveConScadenzaTra(da: Long, a: Long): List<AttivitaScadenza>
    @Query("SELECT * FROM attivita_scadenze") suspend fun tutteSnapshot(): List<AttivitaScadenza>
    @Insert suspend fun inserisciTutte(items: List<AttivitaScadenza>)
    @Query("DELETE FROM attivita_scadenze") suspend fun eliminaTutte()
}
