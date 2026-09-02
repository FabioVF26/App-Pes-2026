package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoClienteDao {
    @Insert suspend fun inserisci(item: DocumentoCliente)
    @Update suspend fun aggiorna(item: DocumentoCliente)
    @Delete suspend fun elimina(item: DocumentoCliente)
    @Query("DELETE FROM documenti_cliente WHERE id = :id") suspend fun eliminaPerId(id: String)
    @Query("SELECT * FROM documenti_cliente ORDER BY creatoEpochMillis DESC") fun osservaTutti(): Flow<List<DocumentoCliente>>
    @Query("SELECT * FROM documenti_cliente WHERE clienteId = :clienteId ORDER BY creatoEpochMillis DESC") fun osservaPerCliente(clienteId: String): Flow<List<DocumentoCliente>>
    @Query("SELECT * FROM documenti_cliente") suspend fun tuttiSnapshot(): List<DocumentoCliente>
    @Insert suspend fun inserisciTutti(items: List<DocumentoCliente>)
    @Query("DELETE FROM documenti_cliente") suspend fun eliminaTutti()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaSync(documentoCliente: DocumentoCliente)
}
