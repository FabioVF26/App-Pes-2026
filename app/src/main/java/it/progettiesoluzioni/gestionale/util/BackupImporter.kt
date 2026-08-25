package it.progettiesoluzioni.gestionale.util

import android.content.Context
import android.net.Uri
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.data.repository.BackupSnapshot
import org.json.JSONArray
import org.json.JSONObject

object BackupImporter {
    fun leggi(context: Context, uri: Uri): BackupSnapshot {
        val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            ?: error("Impossibile leggere il file selezionato")
        val root = JSONObject(text)
        require(root.optString("formato") == "P&S Gestionale backup") { "Il file non è un backup P&S Gestionale valido" }
        val versione = root.optInt("versione", 0)
        require(versione in 1..1) { "Versione backup non supportata: $versione" }

        val clienti = root.getJSONArray("clienti").mapObjects { o ->
            Cliente(
                id = o.getLong("id"), ragioneSociale = o.getString("ragioneSociale"),
                nomeCommerciale = o.optString("nomeCommerciale"), partitaIva = o.optString("partitaIva"),
                codiceFiscale = o.optString("codiceFiscale"), codiceAteco = o.optString("codiceAteco"),
                attivita = o.optString("attivita"), legaleRappresentante = o.optString("legaleRappresentante"),
                telefono = o.optString("telefono"), email = o.optString("email"), pec = o.optString("pec"),
                note = o.optString("note"), servizioHaccp = o.optBoolean("haccp"),
                servizioSicurezza = o.optBoolean("sicurezza"), servizioGdpr = o.optBoolean("gdpr"),
                attivo = o.optBoolean("attivo", true)
            )
        }
        val sedi = root.getJSONArray("sedi").mapObjects { o ->
            Sede(
                id = o.getLong("id"), clienteId = o.getLong("clienteId"), nome = o.optString("nome", "Sede principale"),
                indirizzo = o.optString("indirizzo"), civico = o.optString("civico"), cap = o.optString("cap"),
                comune = o.optString("comune"), provincia = o.optString("provincia"), note = o.optString("note"),
                principale = o.optBoolean("principale", false)
            )
        }
        val attivita = root.optJSONArray("attivitaScadenze")?.mapObjects { o ->
            AttivitaScadenza(
                id = o.getLong("id"), clienteId = o.getLong("clienteId"), servizio = o.optString("servizio", "GENERALE"),
                titolo = o.getString("titolo"), descrizione = o.optString("descrizione"), priorita = o.optString("priorita", "MEDIA"),
                stato = o.optString("stato", "DA_FARE"), scadenzaEpochMillis = o.optLongOrNull("scadenza"),
                promemoriaAttivo = o.optBoolean("promemoriaAttivo", true), creataEpochMillis = o.optLong("creata", System.currentTimeMillis()), completataEpochMillis = o.optLongOrNull("completata")
            )
        } ?: emptyList()
        val documenti = root.optJSONArray("documenti")?.mapObjects { o ->
            DocumentoCliente(
                id = o.getLong("id"), clienteId = o.getLong("clienteId"), sedeId = o.optLongOrNull("sedeId"),
                servizio = o.optString("servizio", "GENERALE"), categoria = o.optString("categoria", "Altro"), titolo = o.getString("titolo"),
                uri = o.optString("uri"), dataDocumentoEpochMillis = o.optLongOrNull("dataDocumento"),
                scadenzaEpochMillis = o.optLongOrNull("scadenza"), note = o.optString("note"),
                creatoEpochMillis = o.optLong("creato", System.currentTimeMillis())
            )
        } ?: emptyList()
        val sopralluoghi = root.optJSONArray("sopralluoghi")?.mapObjects { o ->
            Sopralluogo(
                id = o.getLong("id"), clienteId = o.getLong("clienteId"), sedeId = o.getLong("sedeId"),
                tipoServizio = o.getString("tipoServizio"), dataOraEpochMillis = o.getLong("dataOra"),
                stato = o.optString("stato", "BOZZA"), noteGenerali = o.optString("noteGenerali")
            )
        } ?: emptyList()
        val verifiche = root.optJSONArray("verificheSopralluogo")?.mapObjects { o ->
            VerificaSopralluogo(
                id = o.getLong("id"), sopralluogoId = o.getLong("sopralluogoId"), codice = o.getString("codice"),
                sezione = o.optString("sezione"), titolo = o.getString("titolo"), riferimentoNormativo = o.optString("riferimentoNormativo"),
                esito = o.optString("esito", "DA_VERIFICARE"), note = o.optString("note"), ordine = o.optInt("ordine", 0)
            )
        } ?: emptyList()
        val nonConformita = root.optJSONArray("nonConformita")?.mapObjects { o ->
            NonConformita(
                id = o.getLong("id"), sopralluogoId = o.getLong("sopralluogoId"), verificaId = o.getLong("verificaId"),
                descrizione = o.optString("descrizione"), azioneRichiesta = o.optString("azioneRichiesta"),
                sanzionePossibile = o.optString("sanzionePossibile"), priorita = o.optString("priorita", "MEDIA"),
                stato = o.optString("stato", "APERTA"), termineEpochMillis = o.optLongOrNull("termine"),
                fotoUri = o.optString("fotoUri"), fotoRisoluzioneUri = o.optString("fotoRisoluzioneUri"),
                verificaEfficacia = o.optString("verificaEfficacia", "DA_VERIFICARE"), noteVerifica = o.optString("noteVerifica"),
                dataRisoluzioneEpochMillis = o.optLongOrNull("dataRisoluzione"), dataChiusuraEpochMillis = o.optLongOrNull("dataChiusura")
            )
        } ?: emptyList()

        validateReferences(clienti, sedi, attivita, documenti, sopralluoghi, verifiche, nonConformita)
        return BackupSnapshot(clienti, sedi, attivita, documenti, sopralluoghi, verifiche, nonConformita)
    }

    private fun validateReferences(
        clienti: List<Cliente>, sedi: List<Sede>, attivita: List<AttivitaScadenza>, documenti: List<DocumentoCliente>,
        sopralluoghi: List<Sopralluogo>, verifiche: List<VerificaSopralluogo>, nonConformita: List<NonConformita>
    ) {
        val c = clienti.map { it.id }.toSet()
        val s = sedi.map { it.id }.toSet()
        val sp = sopralluoghi.map { it.id }.toSet()
        val v = verifiche.map { it.id }.toSet()
        require(sedi.all { it.clienteId in c }) { "Backup non valido: una sede fa riferimento a un cliente inesistente" }
        require(attivita.all { it.clienteId in c }) { "Backup non valido: un'attività fa riferimento a un cliente inesistente" }
        require(documenti.all { it.clienteId in c && (it.sedeId == null || it.sedeId in s) }) { "Backup non valido: un documento contiene riferimenti non validi" }
        require(sopralluoghi.all { it.clienteId in c && it.sedeId in s }) { "Backup non valido: un sopralluogo contiene riferimenti non validi" }
        require(verifiche.all { it.sopralluogoId in sp }) { "Backup non valido: una verifica fa riferimento a un sopralluogo inesistente" }
        require(nonConformita.all { it.sopralluogoId in sp && it.verificaId in v }) { "Backup non valido: una non conformità contiene riferimenti non validi" }
    }

    private inline fun <T> JSONArray.mapObjects(block: (JSONObject) -> T): List<T> =
        List(length()) { index -> block(getJSONObject(index)) }

    private fun JSONObject.optLongOrNull(name: String): Long? =
        if (!has(name) || isNull(name)) null else getLong(name)
}
