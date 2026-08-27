package it.progettiesoluzioni.gestionale.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Incapsula Firebase Authentication. Espone lo stato dell'utente corrente come Flow
 * reattivo, cosi' la UI puo' reagire automaticamente a login/logout senza polling.
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
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) Result.success(user) else Result.failure(IllegalStateException("Accesso non riuscito"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun registra(email: String, password: String): Result<FirebaseUser> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) Result.success(user) else Result.failure(IllegalStateException("Registrazione non riuscita"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun inviaResetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun disconnetti() {
        auth.signOut()
    }

    /** Traduce i messaggi di errore Firebase (in inglese) in italiano per l'utente finale. */
    fun messaggioErrore(e: Throwable): String {
        val msg = e.message ?: return "Si e' verificato un errore. Riprova."
        return when {
            msg.contains("badly formatted", ignoreCase = true) -> "L'indirizzo email non e' valido."
            msg.contains("no user record", ignoreCase = true) -> "Nessun account trovato con questa email."
            msg.contains("password is invalid", ignoreCase = true) -> "Password errata."
            msg.contains("email address is already in use", ignoreCase = true) -> "Esiste gia' un account con questa email."
            msg.contains("network error", ignoreCase = true) -> "Errore di rete: controlla la connessione."
            msg.contains("weak password", ignoreCase = true) -> "La password deve avere almeno 6 caratteri."
            msg.contains("too many requests", ignoreCase = true) -> "Troppi tentativi. Riprova tra qualche minuto."
            else -> "Accesso non riuscito: $msg"
        }
    }
}
