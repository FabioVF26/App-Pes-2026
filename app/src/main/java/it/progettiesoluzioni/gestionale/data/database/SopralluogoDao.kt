package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import kotlinx.coroutines.flow.Flow

@Dao
interface SopralluogoDao {
    @Insert
    suspend fun inserisci(sopralluogo: Sopralluogo)

    @Update
    suspend fun aggiorna(sopralluogo: Sopralluogo)

    @Query("SELECT * FROM sopralluoghi ORDER BY dataOraEpochMillis DESC")
    fun osservaTutti(): Flow<List<Sopralluogo>>

    @Query("SELECT * FROM sopralluoghi WHERE clienteId = :clienteId ORDER BY dataOraEpochMillis DESC")
    fun osservaPerCliente(clienteId: String): Flow<List<Sopralluogo>>

    @Query("SELECT * FROM sopralluoghi WHERE id = :id LIMIT 1")
    fun osserva(id: String): Flow<Sopralluogo?>

    @Query("SELECT * FROM sopralluoghi WHERE id = :id LIMIT 1")
    suspend fun perId(id: String): Sopralluogo?

    @Query("DELETE FROM sopralluoghi WHERE id = :id")
    suspend fun eliminaPerId(id: String)

    @Query("SELECT * FROM sopralluoghi ORDER BY dataOraEpochMillis DESC")
    suspend fun tuttiSnapshot(): List<Sopralluogo>

    @Insert
    suspend fun inserisciTutti(items: List<Sopralluogo>)

    @Query("DELETE FROM sopralluoghi")
    suspend fun eliminaTutti()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaSync(sopralluogo: Sopralluogo)
}
