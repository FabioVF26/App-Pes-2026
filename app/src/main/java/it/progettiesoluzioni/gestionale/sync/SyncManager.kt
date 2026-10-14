package it.progettiesoluzioni.gestionale.sync

import com.google.firebase.firestore.FirebaseFirestore
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "SyncManager"
private const val MAX_IN_ATTESA = 500
private const val MAX_TENTATIVI = 50

/**
 * Punto unico di accesso alla sincronizzazione con Firestore. Il repository
 * chiama i metodi push/delete dopo ogni scrittura locale; [avviaAscoltoRealtime]
 * va invocato una sola volta dopo il login per iniziare a ricevere le
 * modifiche fatte dagli altri dispositivi/operatori.
 */
class SyncManager(db: AppDatabase, firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val clienti = FirestoreSyncEngine<Cliente>(
        Collezioni.CLIENTI, firestore, Cliente::toFirestoreMap, { id, data -> data.toCliente(id) },
        { it.id }, db.clienteDao()::upsertDaSync, db.clienteDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val sedi = FirestoreSyncEngine<Sede>(
        Collezioni.SEDI, firestore, Sede::toFirestoreMap, { id, data -> data.toSede(id) },
        { it.id }, db.sedeDao()::upsertDaSync, db.sedeDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val sopralluoghi = FirestoreSyncEngine<Sopralluogo>(
        Collezioni.SOPRALLUOGHI, firestore, Sopralluogo::toFirestoreMap, { id, data -> data.toSopralluogo(id) },
        { it.id }, db.sopralluogoDao()::upsertDaSync, db.sopralluogoDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val verifiche = FirestoreSyncEngine<VerificaSopralluogo>(
        Collezioni.VERIFICHE, firestore, VerificaSopralluogo::toFirestoreMap, { id, data -> data.toVerificaSopralluogo(id) },
        { it.id }, db.verificaSopralluogoDao()::upsertDaSync, db.verificaSopralluogoDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val nonConformita = FirestoreSyncEngine<NonConformita>(
        Collezioni.NON_CONFORMITA, firestore, NonConformita::toFirestoreMap, { id, data -> data.toNonConformita(id) },
        { it.id }, db.nonConformitaDao()::upsertDaSync, db.nonConformitaDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val attivitaScadenze = FirestoreSyncEngine<AttivitaScadenza>(
        Collezioni.ATTIVITA_SCADENZE, firestore, AttivitaScadenza::toFirestoreMap, { id, data -> data.toAttivitaScadenza(id) },
        { it.id }, db.attivitaScadenzaDao()::upsertDaSync, db.attivitaScadenzaDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )
    private val documentiCliente = FirestoreSyncEngine<DocumentoCliente>(
        Collezioni.DOCUMENTI_CLIENTE, firestore, DocumentoCliente::toFirestoreMap, { id, data -> data.toDocumentoCliente(id) },
        { it.id }, db.documentoClienteDao()::upsertDaSync, db.documentoClienteDao()::eliminaPerId,
        this::applicaProtetto, this::riprovaInAttesa
    )

    // --- Applicazione protetta delle modifiche in arrivo -------------------------------

    private class InAttesa(val descrizione: String, val azione: suspend () -> Unit, var tentativi: Int = 0)

    private val mutex = Mutex()
    private val inAttesa = ArrayDeque<InAttesa>()

    /**
     * Applica una modifica in arrivo senza mai propagare errori. Se fallisce (tipicamente perche'
     * l'elemento padre non e' ancora arrivato) la mette in coda per un nuovo tentativo a fine lotto.
     */
    private suspend fun applicaProtetto(descrizione: String, azione: suspend () -> Unit) {
        try {
            azione()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Modifica rimandata ($descrizione): ${e.message}")
            accoda(InAttesa(descrizione, azione))
        }
    }

    private suspend fun accoda(item: InAttesa) = mutex.withLock {
        if (inAttesa.size >= MAX_IN_ATTESA) inAttesa.removeFirst()
        inAttesa.addLast(item)
    }

    /** Riprova le modifiche rimandate; quelle che falliscono troppe volte (orfane) vengono scartate. */
    private suspend fun riprovaInAttesa() {
        val daRiprovare = mutex.withLock { inAttesa.toList().also { inAttesa.clear() } }
        for (item in daRiprovare) {
            try {
                item.azione()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                item.tentativi++
                if (item.tentativi < MAX_TENTATIVI) {
                    accoda(item)
                } else {
                    Log.w(TAG, "Modifica scartata dopo ${item.tentativi} tentativi (${item.descrizione}): ${e.message}")
                }
            }
        }
    }

    fun pushCliente(item: Cliente) = clienti.push(item)
    fun deleteCliente(id: String) = clienti.delete(id)
    fun pushSede(item: Sede) = sedi.push(item)
    fun deleteSede(id: String) = sedi.delete(id)
    fun pushSopralluogo(item: Sopralluogo) = sopralluoghi.push(item)
    fun deleteSopralluogo(id: String) = sopralluoghi.delete(id)
    fun pushVerifica(item: VerificaSopralluogo) = verifiche.push(item)
    fun deleteVerifica(id: String) = verifiche.delete(id)
    fun pushNonConformita(item: NonConformita) = nonConformita.push(item)
    fun deleteNonConformitaPerId(id: String) = nonConformita.delete(id)
    fun pushAttivita(item: AttivitaScadenza) = attivitaScadenze.push(item)
    fun deleteAttivita(id: String) = attivitaScadenze.delete(id)
    fun pushDocumento(item: DocumentoCliente) = documentiCliente.push(item)
    fun deleteDocumento(id: String) = documentiCliente.delete(id)

    private var ascoltoAvviato = false

    /** Da chiamare una sola volta, dopo il login. Idempotente. */
    fun avviaAscoltoRealtime(scope: CoroutineScope) {
        if (ascoltoAvviato) return
        ascoltoAvviato = true
        clienti.avviaAscolto(scope)
        sedi.avviaAscolto(scope)
        sopralluoghi.avviaAscolto(scope)
        verifiche.avviaAscolto(scope)
        nonConformita.avviaAscolto(scope)
        attivitaScadenze.avviaAscolto(scope)
        documentiCliente.avviaAscolto(scope)
    }
}
