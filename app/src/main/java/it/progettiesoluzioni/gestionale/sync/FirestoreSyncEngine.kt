package it.progettiesoluzioni.gestionale.sync

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "FirestoreSync"

/**
 * Sincronizza una singola collezione Firestore con la relativa tabella Room.
 *
 * - [push] scrive una modifica locale su Firestore (l'SDK di Firestore mette
 *   in coda automaticamente la scrittura se il dispositivo e' offline, e la
 *   invia da solo appena torna la connessione).
 * - [delete] rimuove il documento da Firestore.
 * - [avviaAscolto] mette in piedi un listener realtime: ogni modifica fatta da
 *   altri dispositivi viene applicata al database Room locale.
 *
 * Le modifiche in arrivo non vengono mai applicate direttamente: passano da
 * [applica], che le protegge da errori (un documento problematico non deve mai
 * far chiudere l'app) e, se manca l'elemento padre, le riprova piu' tardi.
 * Le scritture ancora "in sospeso" generate da questo stesso dispositivo
 * vengono ignorate, perche' sono gia' presenti in Room.
 */
class FirestoreSyncEngine<T>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val toMap: (T) -> Map<String, Any?>,
    private val fromMap: (String, Map<String, Any?>) -> T,
    private val getId: (T) -> String,
    private val upsertLocale: suspend (T) -> Unit,
    private val eliminaLocalePerId: suspend (String) -> Unit,
    private val applica: suspend (String, suspend () -> Unit) -> Unit,
    private val fineLotto: suspend () -> Unit,
    private val alPrimoSnapshotDalServer: () -> Unit = {}
) {
    private var primoSnapshotServerNotificato = false

    fun push(entity: T) {
        firestore.collection(collectionName)
            .document(getId(entity))
            .set(toMap(entity))
            .addOnFailureListener { e -> Log.w(TAG, "Push fallito su $collectionName/${getId(entity)}", e) }
    }

    fun delete(id: String) {
        firestore.collection(collectionName)
            .document(id)
            .delete()
            .addOnFailureListener { e -> Log.w(TAG, "Delete fallito su $collectionName/$id", e) }
    }

    fun avviaAscolto(scope: CoroutineScope) {
        firestore.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Ascolto fallito su $collectionName", error)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                val daServer = !snapshot.metadata.isFromCache
                val modifiche = snapshot.documentChanges.filter { !it.document.metadata.hasPendingWrites() }

                // Un'unica coroutine per lotto: le modifiche della collezione vengono applicate in ordine.
                scope.launch {
                    for (change in modifiche) {
                        val id = change.document.id
                        when (change.type) {
                            DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                val entity = try {
                                    fromMap(id, change.document.data ?: emptyMap())
                                } catch (e: Exception) {
                                    Log.w(TAG, "Documento non leggibile $collectionName/$id", e)
                                    null
                                }
                                if (entity != null) applica("$collectionName/$id") { upsertLocale(entity) }
                            }
                            DocumentChange.Type.REMOVED -> {
                                applica("$collectionName/$id (rimozione)") { eliminaLocalePerId(id) }
                            }
                        }
                    }
                    if (modifiche.isNotEmpty()) fineLotto()
                    // Primo allineamento con il server completato: i dati del cloud sono ora in Room.
                    if (daServer && !primoSnapshotServerNotificato) {
                        primoSnapshotServerNotificato = true
                        alPrimoSnapshotDalServer()
                    }
                }
            }
    }
}
