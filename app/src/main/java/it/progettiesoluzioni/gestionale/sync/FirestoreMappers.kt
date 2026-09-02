package it.progettiesoluzioni.gestionale.sync

import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo

/** Nomi delle collezioni Firestore, una per ciascuna tabella Room. */
object Collezioni {
    const val CLIENTI = "clienti"
    const val SEDI = "sedi"
    const val SOPRALLUOGHI = "sopralluoghi"
    const val VERIFICHE = "verifiche_sopralluogo"
    const val NON_CONFORMITA = "non_conformita"
    const val ATTIVITA_SCADENZE = "attivita_scadenze"
    const val DOCUMENTI_CLIENTE = "documenti_cliente"
}

private fun Map<String, Any?>.str(key: String, default: String = ""): String = (this[key] as? String) ?: default
private fun Map<String, Any?>.strOrNull(key: String): String? = this[key] as? String
private fun Map<String, Any?>.long(key: String, default: Long = 0L): Long = (this[key] as? Number)?.toLong() ?: default
private fun Map<String, Any?>.longOrNull(key: String): Long? = (this[key] as? Number)?.toLong()
private fun Map<String, Any?>.int(key: String, default: Int = 0): Int = (this[key] as? Number)?.toInt() ?: default
private fun Map<String, Any?>.bool(key: String, default: Boolean = false): Boolean = (this[key] as? Boolean) ?: default

fun Cliente.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "ragioneSociale" to ragioneSociale, "nomeCommerciale" to nomeCommerciale,
    "partitaIva" to partitaIva, "codiceFiscale" to codiceFiscale, "codiceAteco" to codiceAteco,
    "attivita" to attivita, "legaleRappresentante" to legaleRappresentante, "telefono" to telefono,
    "email" to email, "pec" to pec, "note" to note, "servizioHaccp" to servizioHaccp,
    "servizioSicurezza" to servizioSicurezza, "servizioGdpr" to servizioGdpr, "attivo" to attivo
)

fun Map<String, Any?>.toCliente(id: String): Cliente = Cliente(
    id = id, ragioneSociale = str("ragioneSociale"), nomeCommerciale = str("nomeCommerciale"),
    partitaIva = str("partitaIva"), codiceFiscale = str("codiceFiscale"), codiceAteco = str("codiceAteco"),
    attivita = str("attivita"), legaleRappresentante = str("legaleRappresentante"), telefono = str("telefono"),
    email = str("email"), pec = str("pec"), note = str("note"), servizioHaccp = bool("servizioHaccp"),
    servizioSicurezza = bool("servizioSicurezza"), servizioGdpr = bool("servizioGdpr"), attivo = bool("attivo", true)
)

fun Sede.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "clienteId" to clienteId, "nome" to nome, "indirizzo" to indirizzo, "civico" to civico,
    "cap" to cap, "comune" to comune, "provincia" to provincia, "note" to note, "principale" to principale
)

fun Map<String, Any?>.toSede(id: String): Sede = Sede(
    id = id, clienteId = str("clienteId"), nome = str("nome", "Sede principale"), indirizzo = str("indirizzo"),
    civico = str("civico"), cap = str("cap"), comune = str("comune"), provincia = str("provincia"),
    note = str("note"), principale = bool("principale", true)
)

fun Sopralluogo.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "clienteId" to clienteId, "sedeId" to sedeId, "tipoServizio" to tipoServizio,
    "dataOraEpochMillis" to dataOraEpochMillis, "stato" to stato, "noteGenerali" to noteGenerali
)

fun Map<String, Any?>.toSopralluogo(id: String): Sopralluogo = Sopralluogo(
    id = id, clienteId = str("clienteId"), sedeId = str("sedeId"), tipoServizio = str("tipoServizio"),
    dataOraEpochMillis = long("dataOraEpochMillis"), stato = str("stato", "BOZZA"), noteGenerali = str("noteGenerali")
)

fun VerificaSopralluogo.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "sopralluogoId" to sopralluogoId, "codice" to codice, "sezione" to sezione, "titolo" to titolo,
    "riferimentoNormativo" to riferimentoNormativo, "esito" to esito, "note" to note, "ordine" to ordine
)

fun Map<String, Any?>.toVerificaSopralluogo(id: String): VerificaSopralluogo = VerificaSopralluogo(
    id = id, sopralluogoId = str("sopralluogoId"), codice = str("codice"), sezione = str("sezione"),
    titolo = str("titolo"), riferimentoNormativo = str("riferimentoNormativo"), esito = str("esito", "DA_VERIFICARE"),
    note = str("note"), ordine = int("ordine")
)

fun NonConformita.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "sopralluogoId" to sopralluogoId, "verificaId" to verificaId, "descrizione" to descrizione,
    "azioneRichiesta" to azioneRichiesta, "sanzionePossibile" to sanzionePossibile, "priorita" to priorita,
    "stato" to stato, "termineEpochMillis" to termineEpochMillis, "fotoUri" to fotoUri,
    "fotoRisoluzioneUri" to fotoRisoluzioneUri, "verificaEfficacia" to verificaEfficacia,
    "noteVerifica" to noteVerifica, "dataRisoluzioneEpochMillis" to dataRisoluzioneEpochMillis,
    "dataChiusuraEpochMillis" to dataChiusuraEpochMillis
)

fun Map<String, Any?>.toNonConformita(id: String): NonConformita = NonConformita(
    id = id, sopralluogoId = str("sopralluogoId"), verificaId = str("verificaId"), descrizione = str("descrizione"),
    azioneRichiesta = str("azioneRichiesta"), sanzionePossibile = str("sanzionePossibile"),
    priorita = str("priorita", "MEDIA"), stato = str("stato", "APERTA"), termineEpochMillis = longOrNull("termineEpochMillis"),
    fotoUri = str("fotoUri"), fotoRisoluzioneUri = str("fotoRisoluzioneUri"),
    verificaEfficacia = str("verificaEfficacia", "DA_VERIFICARE"), noteVerifica = str("noteVerifica"),
    dataRisoluzioneEpochMillis = longOrNull("dataRisoluzioneEpochMillis"), dataChiusuraEpochMillis = longOrNull("dataChiusuraEpochMillis")
)

fun AttivitaScadenza.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "clienteId" to clienteId, "servizio" to servizio, "titolo" to titolo, "descrizione" to descrizione,
    "priorita" to priorita, "stato" to stato, "scadenzaEpochMillis" to scadenzaEpochMillis,
    "promemoriaAttivo" to promemoriaAttivo, "creataEpochMillis" to creataEpochMillis,
    "completataEpochMillis" to completataEpochMillis
)

fun Map<String, Any?>.toAttivitaScadenza(id: String): AttivitaScadenza = AttivitaScadenza(
    id = id, clienteId = str("clienteId"), servizio = str("servizio", "GENERALE"), titolo = str("titolo"),
    descrizione = str("descrizione"), priorita = str("priorita", "MEDIA"), stato = str("stato", "DA_FARE"),
    scadenzaEpochMillis = longOrNull("scadenzaEpochMillis"), promemoriaAttivo = bool("promemoriaAttivo", true),
    creataEpochMillis = long("creataEpochMillis", System.currentTimeMillis()), completataEpochMillis = longOrNull("completataEpochMillis")
)

fun DocumentoCliente.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id, "clienteId" to clienteId, "sedeId" to sedeId, "servizio" to servizio, "categoria" to categoria,
    "titolo" to titolo, "uri" to uri, "dataDocumentoEpochMillis" to dataDocumentoEpochMillis,
    "scadenzaEpochMillis" to scadenzaEpochMillis, "note" to note, "creatoEpochMillis" to creatoEpochMillis
)

fun Map<String, Any?>.toDocumentoCliente(id: String): DocumentoCliente = DocumentoCliente(
    id = id, clienteId = str("clienteId"), sedeId = strOrNull("sedeId"), servizio = str("servizio", "GENERALE"),
    categoria = str("categoria", "Altro"), titolo = str("titolo"), uri = str("uri"),
    dataDocumentoEpochMillis = longOrNull("dataDocumentoEpochMillis"), scadenzaEpochMillis = longOrNull("scadenzaEpochMillis"),
    note = str("note"), creatoEpochMillis = long("creatoEpochMillis", System.currentTimeMillis())
)
