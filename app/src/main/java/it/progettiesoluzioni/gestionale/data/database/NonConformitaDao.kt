package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import kotlinx.coroutines.flow.Flow

@Dao
interface NonConformitaDao {
    @Insert
    suspend fun inserisci(nonConformita: NonConformita): Long

    @Update
    suspend fun aggiorna(nonConformita: NonConformita)

    @Query("SELECT * FROM non_conformita WHERE sopralluogoId = :sopralluogoId ORDER BY id DESC")
    fun osservaPerSopralluogo(sopralluogoId: Long): Flow<List<NonConformita>>

    @Query("SELECT * FROM non_conformita WHERE verificaId = :verificaId LIMIT 1")
    suspend fun perVerifica(verificaId: Long): NonConformita?

    @Query("DELETE FROM non_conformita WHERE verificaId = :verificaId")
    suspend fun eliminaPerVerifica(verificaId: Long)

    @Query("SELECT * FROM non_conformita ORDER BY sopralluogoId, id")
    suspend fun tutteSnapshot(): List<NonConformita>
}
