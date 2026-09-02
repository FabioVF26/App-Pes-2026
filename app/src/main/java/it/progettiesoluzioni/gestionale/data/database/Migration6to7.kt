package it.progettiesoluzioni.gestionale.data.database

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

/**
 * Migrazione 6 -> 7.
 *
 * Fino alla v0.11 tutte le tabelle usavano ID numerici autoincrementali (Long),
 * locali al singolo dispositivo. Con l'introduzione della sincronizzazione su
 * Firestore (dati condivisi tra piu' dispositivi/operatori), servono ID
 * globalmente univoci: due dispositivi diversi genererebbero altrimenti lo
 * stesso ID numerico (es. entrambi partono da 1, 2, 3...) causando collisioni
 * quando i dati confluiscono nello stesso database condiviso.
 *
 * Questa migrazione:
 *  1) genera un nuovo ID testuale (UUID) per ogni riga esistente in ciascuna
 *     delle 7 tabelle, mantenendo una mappa vecchioId(Long) -> nuovoId(String);
 *  2) riscrive tutte le colonne di chiave esterna usando le mappe delle
 *     tabelle "genitore" gia' processate, cosi' nessuna relazione viene persa;
 *  3) ricrea ciascuna tabella con lo schema testuale definitivo e i relativi
 *     indici, esattamente come richiesto dalle nuove entita' Room.
 *
 * L'ordine di elaborazione segue le dipendenze (genitore prima del figlio):
 * clienti -> sedi -> sopralluoghi -> verifiche_sopralluogo -> non_conformita
 *         -> attivita_scadenze -> documenti_cliente.
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {

        fun Cursor.stringOrNull(col: String): String? =
            getColumnIndex(col).let { if (it < 0) null else if (isNull(it)) null else getString(it) }

        fun Cursor.longOrNull(col: String): Long? =
            getColumnIndex(col).let { if (it < 0) null else if (isNull(it)) null else getLong(it) }

        fun Cursor.long(col: String): Long = getLong(getColumnIndexOrThrow(col))
        fun Cursor.string(col: String): String = getString(getColumnIndexOrThrow(col)) ?: ""
        fun Cursor.int(col: String): Int = getInt(getColumnIndexOrThrow(col))

        // ---------------------------------------------------------------
        // 1) CLIENTI (nessuna dipendenza)
        // ---------------------------------------------------------------
        val clientiMap = HashMap<Long, String>()
        db.execSQL(
            """
            CREATE TABLE clienti_v7 (
                id TEXT NOT NULL, ragioneSociale TEXT NOT NULL, nomeCommerciale TEXT NOT NULL,
                partitaIva TEXT NOT NULL, codiceFiscale TEXT NOT NULL, codiceAteco TEXT NOT NULL,
                attivita TEXT NOT NULL, legaleRappresentante TEXT NOT NULL, telefono TEXT NOT NULL,
                email TEXT NOT NULL, pec TEXT NOT NULL, note TEXT NOT NULL,
                servizioHaccp INTEGER NOT NULL, servizioSicurezza INTEGER NOT NULL,
                servizioGdpr INTEGER NOT NULL, attivo INTEGER NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM clienti").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                clientiMap[c.long("id")] = newId
                db.execSQL(
                    """INSERT INTO clienti_v7 (id, ragioneSociale, nomeCommerciale, partitaIva, codiceFiscale,
                        codiceAteco, attivita, legaleRappresentante, telefono, email, pec, note,
                        servizioHaccp, servizioSicurezza, servizioGdpr, attivo)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(
                        newId, c.string("ragioneSociale"), c.string("nomeCommerciale"), c.string("partitaIva"),
                        c.string("codiceFiscale"), c.string("codiceAteco"), c.string("attivita"),
                        c.string("legaleRappresentante"), c.string("telefono"), c.string("email"),
                        c.string("pec"), c.string("note"), c.int("servizioHaccp"), c.int("servizioSicurezza"),
                        c.int("servizioGdpr"), c.int("attivo")
                    )
                )
            }
        }
        db.execSQL("DROP TABLE clienti")
        db.execSQL("ALTER TABLE clienti_v7 RENAME TO clienti")

        // ---------------------------------------------------------------
        // 2) SEDI (dipende da clienti)
        // ---------------------------------------------------------------
        val sediMap = HashMap<Long, String>()
        db.execSQL(
            """
            CREATE TABLE sedi_v7 (
                id TEXT NOT NULL, clienteId TEXT NOT NULL, nome TEXT NOT NULL, indirizzo TEXT NOT NULL,
                civico TEXT NOT NULL, cap TEXT NOT NULL, comune TEXT NOT NULL, provincia TEXT NOT NULL,
                note TEXT NOT NULL, principale INTEGER NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(clienteId) REFERENCES clienti(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM sedi").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val oldClienteId = c.long("clienteId")
                sediMap[c.long("id")] = newId
                val nuovoClienteId = clientiMap[oldClienteId] ?: continue
                db.execSQL(
                    """INSERT INTO sedi_v7 (id, clienteId, nome, indirizzo, civico, cap, comune, provincia, note, principale)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(
                        newId, nuovoClienteId, c.string("nome"), c.string("indirizzo"), c.string("civico"),
                        c.string("cap"), c.string("comune"), c.string("provincia"), c.string("note"), c.int("principale")
                    )
                )
            }
        }
        db.execSQL("DROP TABLE sedi")
        db.execSQL("ALTER TABLE sedi_v7 RENAME TO sedi")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sedi_clienteId ON sedi(clienteId)")

        // ---------------------------------------------------------------
        // 3) SOPRALLUOGHI (dipende da clienti e sedi)
        // ---------------------------------------------------------------
        val sopralluoghiMap = HashMap<Long, String>()
        db.execSQL(
            """
            CREATE TABLE sopralluoghi_v7 (
                id TEXT NOT NULL, clienteId TEXT NOT NULL, sedeId TEXT NOT NULL, tipoServizio TEXT NOT NULL,
                dataOraEpochMillis INTEGER NOT NULL, stato TEXT NOT NULL, noteGenerali TEXT NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(clienteId) REFERENCES clienti(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(sedeId) REFERENCES sedi(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM sopralluoghi").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val nuovoClienteId = clientiMap[c.long("clienteId")] ?: continue
                val nuovaSedeId = sediMap[c.long("sedeId")] ?: continue
                sopralluoghiMap[c.long("id")] = newId
                db.execSQL(
                    """INSERT INTO sopralluoghi_v7 (id, clienteId, sedeId, tipoServizio, dataOraEpochMillis, stato, noteGenerali)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(newId, nuovoClienteId, nuovaSedeId, c.string("tipoServizio"), c.long("dataOraEpochMillis"), c.string("stato"), c.string("noteGenerali"))
                )
            }
        }
        db.execSQL("DROP TABLE sopralluoghi")
        db.execSQL("ALTER TABLE sopralluoghi_v7 RENAME TO sopralluoghi")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sopralluoghi_clienteId ON sopralluoghi(clienteId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_sopralluoghi_sedeId ON sopralluoghi(sedeId)")

        // ---------------------------------------------------------------
        // 4) VERIFICHE_SOPRALLUOGO (dipende da sopralluoghi)
        // ---------------------------------------------------------------
        val verificheMap = HashMap<Long, String>()
        db.execSQL(
            """
            CREATE TABLE verifiche_sopralluogo_v7 (
                id TEXT NOT NULL, sopralluogoId TEXT NOT NULL, codice TEXT NOT NULL, sezione TEXT NOT NULL,
                titolo TEXT NOT NULL, riferimentoNormativo TEXT NOT NULL, esito TEXT NOT NULL,
                note TEXT NOT NULL, ordine INTEGER NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(sopralluogoId) REFERENCES sopralluoghi(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM verifiche_sopralluogo").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val nuovoSopralluogoId = sopralluoghiMap[c.long("sopralluogoId")] ?: continue
                verificheMap[c.long("id")] = newId
                db.execSQL(
                    """INSERT INTO verifiche_sopralluogo_v7 (id, sopralluogoId, codice, sezione, titolo, riferimentoNormativo, esito, note, ordine)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(newId, nuovoSopralluogoId, c.string("codice"), c.string("sezione"), c.string("titolo"), c.string("riferimentoNormativo"), c.string("esito"), c.string("note"), c.int("ordine"))
                )
            }
        }
        db.execSQL("DROP TABLE verifiche_sopralluogo")
        db.execSQL("ALTER TABLE verifiche_sopralluogo_v7 RENAME TO verifiche_sopralluogo")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_verifiche_sopralluogo_sopralluogoId ON verifiche_sopralluogo(sopralluogoId)")

        // ---------------------------------------------------------------
        // 5) NON_CONFORMITA (dipende da sopralluoghi e verifiche)
        // ---------------------------------------------------------------
        db.execSQL(
            """
            CREATE TABLE non_conformita_v7 (
                id TEXT NOT NULL, sopralluogoId TEXT NOT NULL, verificaId TEXT NOT NULL,
                descrizione TEXT NOT NULL, azioneRichiesta TEXT NOT NULL,
                sanzionePossibile TEXT NOT NULL DEFAULT '', priorita TEXT NOT NULL, stato TEXT NOT NULL,
                termineEpochMillis INTEGER, fotoUri TEXT NOT NULL,
                fotoRisoluzioneUri TEXT NOT NULL DEFAULT '',
                verificaEfficacia TEXT NOT NULL DEFAULT 'DA_VERIFICARE',
                noteVerifica TEXT NOT NULL DEFAULT '',
                dataRisoluzioneEpochMillis INTEGER, dataChiusuraEpochMillis INTEGER,
                PRIMARY KEY(id),
                FOREIGN KEY(sopralluogoId) REFERENCES sopralluoghi(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(verificaId) REFERENCES verifiche_sopralluogo(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM non_conformita").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val nuovoSopralluogoId = sopralluoghiMap[c.long("sopralluogoId")] ?: continue
                val nuovaVerificaId = verificheMap[c.long("verificaId")] ?: continue
                db.execSQL(
                    """INSERT INTO non_conformita_v7 (id, sopralluogoId, verificaId, descrizione, azioneRichiesta,
                        sanzionePossibile, priorita, stato, termineEpochMillis, fotoUri, fotoRisoluzioneUri,
                        verificaEfficacia, noteVerifica, dataRisoluzioneEpochMillis, dataChiusuraEpochMillis)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(
                        newId, nuovoSopralluogoId, nuovaVerificaId, c.string("descrizione"), c.string("azioneRichiesta"),
                        c.stringOrNull("sanzionePossibile") ?: "", c.string("priorita"), c.string("stato"),
                        c.longOrNull("termineEpochMillis"), c.string("fotoUri"), c.stringOrNull("fotoRisoluzioneUri") ?: "",
                        c.stringOrNull("verificaEfficacia") ?: "DA_VERIFICARE", c.stringOrNull("noteVerifica") ?: "",
                        c.longOrNull("dataRisoluzioneEpochMillis"), c.longOrNull("dataChiusuraEpochMillis")
                    )
                )
            }
        }
        db.execSQL("DROP TABLE non_conformita")
        db.execSQL("ALTER TABLE non_conformita_v7 RENAME TO non_conformita")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_non_conformita_sopralluogoId ON non_conformita(sopralluogoId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_non_conformita_verificaId ON non_conformita(verificaId)")

        // ---------------------------------------------------------------
        // 6) ATTIVITA_SCADENZE (dipende da clienti)
        // ---------------------------------------------------------------
        db.execSQL(
            """
            CREATE TABLE attivita_scadenze_v7 (
                id TEXT NOT NULL, clienteId TEXT NOT NULL, servizio TEXT NOT NULL, titolo TEXT NOT NULL,
                descrizione TEXT NOT NULL, priorita TEXT NOT NULL, stato TEXT NOT NULL,
                scadenzaEpochMillis INTEGER, promemoriaAttivo INTEGER NOT NULL,
                creataEpochMillis INTEGER NOT NULL, completataEpochMillis INTEGER,
                PRIMARY KEY(id),
                FOREIGN KEY(clienteId) REFERENCES clienti(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM attivita_scadenze").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val nuovoClienteId = clientiMap[c.long("clienteId")] ?: continue
                db.execSQL(
                    """INSERT INTO attivita_scadenze_v7 (id, clienteId, servizio, titolo, descrizione, priorita, stato,
                        scadenzaEpochMillis, promemoriaAttivo, creataEpochMillis, completataEpochMillis)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(
                        newId, nuovoClienteId, c.string("servizio"), c.string("titolo"), c.string("descrizione"),
                        c.string("priorita"), c.string("stato"), c.longOrNull("scadenzaEpochMillis"),
                        c.int("promemoriaAttivo"), c.long("creataEpochMillis"), c.longOrNull("completataEpochMillis")
                    )
                )
            }
        }
        db.execSQL("DROP TABLE attivita_scadenze")
        db.execSQL("ALTER TABLE attivita_scadenze_v7 RENAME TO attivita_scadenze")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_attivita_scadenze_clienteId ON attivita_scadenze(clienteId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_attivita_scadenze_scadenzaEpochMillis ON attivita_scadenze(scadenzaEpochMillis)")

        // ---------------------------------------------------------------
        // 7) DOCUMENTI_CLIENTE (dipende da clienti e, opzionalmente, sedi)
        // ---------------------------------------------------------------
        db.execSQL(
            """
            CREATE TABLE documenti_cliente_v7 (
                id TEXT NOT NULL, clienteId TEXT NOT NULL, sedeId TEXT, servizio TEXT NOT NULL,
                categoria TEXT NOT NULL, titolo TEXT NOT NULL, uri TEXT NOT NULL,
                dataDocumentoEpochMillis INTEGER, scadenzaEpochMillis INTEGER, note TEXT NOT NULL,
                creatoEpochMillis INTEGER NOT NULL,
                PRIMARY KEY(id),
                FOREIGN KEY(clienteId) REFERENCES clienti(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(sedeId) REFERENCES sedi(id) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.query("SELECT * FROM documenti_cliente").use { c ->
            while (c.moveToNext()) {
                val newId = UUID.randomUUID().toString()
                val nuovoClienteId = clientiMap[c.long("clienteId")] ?: continue
                val vecchiaSedeId = c.longOrNull("sedeId")
                val nuovaSedeId = vecchiaSedeId?.let { sediMap[it] }
                db.execSQL(
                    """INSERT INTO documenti_cliente_v7 (id, clienteId, sedeId, servizio, categoria, titolo, uri,
                        dataDocumentoEpochMillis, scadenzaEpochMillis, note, creatoEpochMillis)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(
                        newId, nuovoClienteId, nuovaSedeId, c.string("servizio"), c.string("categoria"),
                        c.string("titolo"), c.string("uri"), c.longOrNull("dataDocumentoEpochMillis"),
                        c.longOrNull("scadenzaEpochMillis"), c.string("note"), c.long("creatoEpochMillis")
                    )
                )
            }
        }
        db.execSQL("DROP TABLE documenti_cliente")
        db.execSQL("ALTER TABLE documenti_cliente_v7 RENAME TO documenti_cliente")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_documenti_cliente_clienteId ON documenti_cliente(clienteId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_documenti_cliente_sedeId ON documenti_cliente(sedeId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_documenti_cliente_scadenzaEpochMillis ON documenti_cliente(scadenzaEpochMillis)")
    }
}
