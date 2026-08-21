package it.progettiesoluzioni.gestionale.ui.clienti

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.repository.GestionaleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestionaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GestionaleRepository(AppDatabase.getInstance(application))

    val clienti = repository.clienti.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val numeroClienti = repository.numeroClienti.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        0
    )

    fun cliente(id: Long) = repository.cliente(id)
    fun sedi(clienteId: Long) = repository.sedi(clienteId)

    fun salvaCliente(cliente: Cliente, sede: Sede, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.inserisciClienteConSede(cliente, sede)
            onSaved()
        }
    }
}
