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
 *   invia da solo appena torna la connessione: non serve gestirlo a mano).
 * - [delete] rimuove il documento da Firestore quando l'elemento viene
 *   eliminato localmente.
 * - [avviaAscolto] mette in piedi un listener realtime: ogni volta che un
 *   altro dispositivo aggiunge, modifica o elimina un elemento, la modifica
 *   viene applicata anche al database Room locale.
 *
 * Le scritture ancora "in sospeso" (metadata.hasPendingWrites) generate da
 * questo stesso dispositivo vengono ignorate nel listener, per evitare di
 * riscrivere su Room un dato identico a quello appena salvato localmente.
 */
class FirestoreSyncEngine<T>(
    private val collectionName: String,
    private val firestore: FirebaseFirestore,
    private val toMap: (T) -> Map<String, Any?>,
    private val fromMap: (String, Map<String, Any?>) -> T,
    private val getId: (T) -> String,
    private val upsertLocale: suspend (T) -> Unit,
    private val eliminaLocalePerId: suspend (String) -> Unit
) {
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

                for (change in snapshot.documentChanges) {
                    // Ignora l'eco della scrittura appena effettuata da questo stesso dispositivo:
                    // e' gia' presente in Room, riscriverla sarebbe ridondante.
                    if (change.document.metadata.hasPendingWrites()) continue

                    scope.launch {
                        when (change.type) {
                            DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                val data = change.document.data ?: emptyMap()
                                upsertLocale(fromMap(change.document.id, data))
                            }
                            DocumentChange.Type.REMOVED -> {
                                eliminaLocalePerId(change.document.id)
                            }
                        }
                    }
                }
            }
    }
}
