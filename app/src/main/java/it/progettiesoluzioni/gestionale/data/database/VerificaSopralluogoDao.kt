package it.progettiesoluzioni.gestionale.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificaSopralluogoDao {
    @Insert
    suspend fun inserisciTutte(verifiche: List<VerificaSopralluogo>)

    @Update
    suspend fun aggiorna(verifica: VerificaSopralluogo)

    @Query("SELECT * FROM verifiche_sopralluogo WHERE sopralluogoId = :sopralluogoId ORDER BY ordine ASC")
    fun osservaPerSopralluogo(sopralluogoId: Long): Flow<List<VerificaSopralluogo>>

    @Query("SELECT COUNT(*) FROM verifiche_sopralluogo WHERE sopralluogoId = :sopralluogoId AND esito = 'DA_VERIFICARE'")
    suspend fun contaDaVerificare(sopralluogoId: Long): Int
}
