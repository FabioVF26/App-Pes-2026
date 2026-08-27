package it.progettiesoluzioni.gestionale.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val caricamento: Boolean = false,
    val errore: String? = null,
    val emailResetInviata: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _utenteCorrente = MutableStateFlow(repository.utenteCorrente)
    val utenteCorrente: StateFlow<FirebaseUser?> = _utenteCorrente.asStateFlow()

    init {
        viewModelScope.launch {
            repository.osservaStatoAutenticazione().collect { user ->
                _utenteCorrente.value = user
            }
        }
    }

    fun accedi(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errore = "Inserisci email e password.")
            return
        }
        _uiState.value = _uiState.value.copy(caricamento = true, errore = null)
        viewModelScope.launch {
            val result = repository.accedi(email.trim(), password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState() },
                onFailure = { AuthUiState(errore = repository.messaggioErrore(it)) }
            )
        }
    }

    fun registra(email: String, password: String, confermaPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errore = "Inserisci email e password.")
            return
        }
        if (password != confermaPassword) {
            _uiState.value = _uiState.value.copy(errore = "Le password non coincidono.")
            return
        }
        _uiState.value = _uiState.value.copy(caricamento = true, errore = null)
        viewModelScope.launch {
            val result = repository.registra(email.trim(), password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState() },
                onFailure = { AuthUiState(errore = repository.messaggioErrore(it)) }
            )
        }
    }

    fun inviaResetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errore = "Inserisci l'email per ricevere il link di reset.")
            return
        }
        _uiState.value = _uiState.value.copy(caricamento = true, errore = null)
        viewModelScope.launch {
            val result = repository.inviaResetPassword(email.trim())
            _uiState.value = result.fold(
                onSuccess = { AuthUiState(emailResetInviata = true) },
                onFailure = { AuthUiState(errore = repository.messaggioErrore(it)) }
            )
        }
    }

    fun disconnetti() {
        repository.disconnetti()
    }

    fun consumaErrore() {
        _uiState.value = _uiState.value.copy(errore = null)
    }
}
