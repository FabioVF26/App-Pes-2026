package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import kotlinx.coroutines.flow.Flow

@Dao
interface NonConformitaDao {
    @Insert
    suspend fun inserisci(nonConformita: NonConformita)

    @Update
    suspend fun aggiorna(nonConformita: NonConformita)

    @Query("SELECT * FROM non_conformita WHERE sopralluogoId = :sopralluogoId ORDER BY id DESC")
    fun osservaPerSopralluogo(sopralluogoId: String): Flow<List<NonConformita>>

    @Query("SELECT * FROM non_conformita WHERE verificaId = :verificaId LIMIT 1")
    suspend fun perVerifica(verificaId: String): NonConformita?

    @Query("DELETE FROM non_conformita WHERE verificaId = :verificaId")
    suspend fun eliminaPerVerifica(verificaId: String)

    @Query("DELETE FROM non_conformita WHERE id = :id")
    suspend fun eliminaPerId(id: String)

    @Query("SELECT * FROM non_conformita ORDER BY sopralluogoId, id")
    suspend fun tutteSnapshot(): List<NonConformita>

    @Insert
    suspend fun inserisciTutte(items: List<NonConformita>)

    @Query("DELETE FROM non_conformita")
    suspend fun eliminaTutte()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaSync(nonConformita: NonConformita)
}
