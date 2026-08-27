package it.progettiesoluzioni.gestionale.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Incapsula Firebase Authentication. Espone lo stato dell'utente corrente come Flow
 * reattivo, così la UI può reagire automaticamente a login/logout senza polling.
 *
 * La creazione degli account non è esposta nell'app: gli utenti sono gestiti
 * dall'amministratore tramite Firebase Console.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val utenteCorrente: FirebaseUser?
        get() = auth.currentUser

    /** Emette l'utente corrente ogni volta che cambia lo stato di autenticazione. */
    fun osservaStatoAutenticazione(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun accedi(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.signInWithEmailAndPassword(email, password).awaitResult()
        val user = result.user
        if (user != null) Result.success(user)
        else Result.failure(IllegalStateException("Accesso non riuscito"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun inviaResetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).awaitResult()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun disconnetti() {
        auth.signOut()
    }

    /** Traduce in italiano le principali eccezioni Firebase senza dipendere dal testo dell'errore. */
    fun messaggioErrore(e: Throwable): String = when (e) {
        is FirebaseAuthInvalidCredentialsException ->
            "Email o password non corrette. Verifica i dati inseriti."
        is FirebaseAuthInvalidUserException ->
            "Account non disponibile o disabilitato. Contatta l'amministratore."
        is FirebaseNetworkException ->
            "Errore di rete: controlla la connessione e riprova."
        is FirebaseTooManyRequestsException ->
            "Troppi tentativi. Riprova più tardi."
        is IllegalArgumentException ->
            "I dati inseriti non sono validi."
        else ->
            "Operazione non riuscita. Riprova o contatta l'amministratore."
    }
}


/** Conversione locale Task -> coroutine: evita la dipendenza aggiuntiva coroutines-play-services. */
private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (!continuation.isActive) return@addOnCompleteListener
        if (task.isSuccessful) {
            continuation.resume(task.result)
        } else {
            continuation.resumeWithException(task.exception ?: IllegalStateException("Operazione Firebase non riuscita"))
        }
    }
}
