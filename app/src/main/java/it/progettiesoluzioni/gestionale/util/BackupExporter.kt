package it.progettiesoluzioni.gestionale.util

import android.content.Context
import androidx.core.content.FileProvider
import it.progettiesoluzioni.gestionale.data.repository.BackupSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupExporter {
    fun creaBackup(context: Context, snapshot: BackupSnapshot): android.net.Uri {
        val root = JSONObject().apply {
            put("formato", "P&S Gestionale backup")
            put("versione", 1)
            put("generatoEpochMillis", System.currentTimeMillis())
            put("clienti", JSONArray().apply { snapshot.clienti.forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id); put("ragioneSociale", c.ragioneSociale); put("nomeCommerciale", c.nomeCommerciale)
                    put("partitaIva", c.partitaIva); put("codiceFiscale", c.codiceFiscale); put("codiceAteco", c.codiceAteco)
                    put("attivita", c.attivita); put("legaleRappresentante", c.legaleRappresentante); put("telefono", c.telefono)
                    put("email", c.email); put("pec", c.pec); put("note", c.note); put("haccp", c.servizioHaccp)
                    put("sicurezza", c.servizioSicurezza); put("gdpr", c.servizioGdpr); put("attivo", c.attivo)
                })
            } })
            put("sedi", JSONArray().apply { snapshot.sedi.forEach { s ->
                put(JSONObject().apply { put("id", s.id); put("clienteId", s.clienteId); put("nome", s.nome); put("indirizzo", s.indirizzo); put("civico", s.civico); put("cap", s.cap); put("comune", s.comune); put("provincia", s.provincia); put("note", s.note); put("principale", s.principale) })
            } })
            put("attivitaScadenze", JSONArray().apply { snapshot.attivita.forEach { a ->
                put(JSONObject().apply { put("id", a.id); put("clienteId", a.clienteId); put("servizio", a.servizio); put("titolo", a.titolo); put("descrizione", a.descrizione); put("priorita", a.priorita); put("stato", a.stato); put("scadenza", a.scadenzaEpochMillis ?: JSONObject.NULL); put("creata", a.creataEpochMillis); put("completata", a.completataEpochMillis ?: JSONObject.NULL) })
            } })
            put("documenti", JSONArray().apply { snapshot.documenti.forEach { d ->
                put(JSONObject().apply { put("id", d.id); put("clienteId", d.clienteId); put("sedeId", d.sedeId ?: JSONObject.NULL); put("servizio", d.servizio); put("categoria", d.categoria); put("titolo", d.titolo); put("uri", d.uri); put("dataDocumento", d.dataDocumentoEpochMillis ?: JSONObject.NULL); put("scadenza", d.scadenzaEpochMillis ?: JSONObject.NULL); put("note", d.note); put("creato", d.creatoEpochMillis) })
            } })
            put("sopralluoghi", JSONArray().apply { snapshot.sopralluoghi.forEach { s ->
                put(JSONObject().apply { put("id", s.id); put("clienteId", s.clienteId); put("sedeId", s.sedeId); put("tipoServizio", s.tipoServizio); put("dataOra", s.dataOraEpochMillis); put("stato", s.stato); put("noteGenerali", s.noteGenerali) })
            } })
            put("verificheSopralluogo", JSONArray().apply { snapshot.verifiche.forEach { v ->
                put(JSONObject().apply { put("id", v.id); put("sopralluogoId", v.sopralluogoId); put("codice", v.codice); put("sezione", v.sezione); put("titolo", v.titolo); put("riferimentoNormativo", v.riferimentoNormativo); put("esito", v.esito); put("note", v.note); put("ordine", v.ordine) })
            } })
            put("nonConformita", JSONArray().apply { snapshot.nonConformita.forEach { n ->
                put(JSONObject().apply { put("id", n.id); put("sopralluogoId", n.sopralluogoId); put("verificaId", n.verificaId); put("descrizione", n.descrizione); put("azioneRichiesta", n.azioneRichiesta); put("priorita", n.priorita); put("stato", n.stato); put("termine", n.termineEpochMillis ?: JSONObject.NULL); put("fotoUri", n.fotoUri); put("fotoRisoluzioneUri", n.fotoRisoluzioneUri); put("verificaEfficacia", n.verificaEfficacia); put("noteVerifica", n.noteVerifica); put("dataRisoluzione", n.dataRisoluzioneEpochMillis ?: JSONObject.NULL); put("dataChiusura", n.dataChiusuraEpochMillis ?: JSONObject.NULL); put("sanzionePossibile", n.sanzionePossibile) })
            } })
            put("nota", "Il backup include tutti i dati gestionali e i riferimenti URI ai file/foto. I file binari originali non sono incorporati.")
        }
        val dir = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), "Backup").apply { mkdirs() }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ITALY).format(Date())
        val file = File(dir, "PS_Gestionale_backup_$stamp.json")
        file.writeText(root.toString(2))
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
