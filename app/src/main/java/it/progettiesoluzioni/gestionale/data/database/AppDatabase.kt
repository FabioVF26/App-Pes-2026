package it.progettiesoluzioni.gestionale.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo

@Database(
    entities = [Cliente::class, Sede::class, Sopralluogo::class, VerificaSopralluogo::class, NonConformita::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun sedeDao(): SedeDao
    abstract fun sopralluogoDao(): SopralluogoDao
    abstract fun verificaSopralluogoDao(): VerificaSopralluogoDao
    abstract fun nonConformitaDao(): NonConformitaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS verifiche_sopralluogo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sopralluogoId INTEGER NOT NULL,
                        codice TEXT NOT NULL,
                        sezione TEXT NOT NULL,
                        titolo TEXT NOT NULL,
                        riferimentoNormativo TEXT NOT NULL,
                        esito TEXT NOT NULL,
                        note TEXT NOT NULL,
                        ordine INTEGER NOT NULL,
                        FOREIGN KEY(sopralluogoId) REFERENCES sopralluoghi(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_verifiche_sopralluogo_sopralluogoId ON verifiche_sopralluogo(sopralluogoId)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS non_conformita (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sopralluogoId INTEGER NOT NULL,
                        verificaId INTEGER NOT NULL,
                        descrizione TEXT NOT NULL,
                        azioneRichiesta TEXT NOT NULL,
                        priorita TEXT NOT NULL,
                        stato TEXT NOT NULL,
                        termineEpochMillis INTEGER,
                        fotoUri TEXT NOT NULL,
                        FOREIGN KEY(sopralluogoId) REFERENCES sopralluoghi(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(verificaId) REFERENCES verifiche_sopralluogo(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_non_conformita_sopralluogoId ON non_conformita(sopralluogoId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_non_conformita_verificaId ON non_conformita(verificaId)")
            }
        }


        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE non_conformita ADD COLUMN fotoRisoluzioneUri TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE non_conformita ADD COLUMN verificaEfficacia TEXT NOT NULL DEFAULT 'DA_VERIFICARE'")
                db.execSQL("ALTER TABLE non_conformita ADD COLUMN noteVerifica TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE non_conformita ADD COLUMN dataRisoluzioneEpochMillis INTEGER")
                db.execSQL("ALTER TABLE non_conformita ADD COLUMN dataChiusuraEpochMillis INTEGER")
            }
        }

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ps_gestionale.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                .also { INSTANCE = it }
        }
    }
}
