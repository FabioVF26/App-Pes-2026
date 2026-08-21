package it.progettiesoluzioni.gestionale.ui.clienti

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.data.repository.GestionaleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestionaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GestionaleRepository(AppDatabase.getInstance(application))

    val clienti = repository.clienti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val numeroClienti = repository.numeroClienti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val sopralluoghi = repository.sopralluoghi().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cliente(id: Long) = repository.cliente(id)
    fun sedi(clienteId: Long) = repository.sedi(clienteId)
    fun sopralluogo(id: Long) = repository.sopralluogo(id)
    fun verifiche(sopralluogoId: Long) = repository.verifiche(sopralluogoId)
    fun nonConformita(sopralluogoId: Long) = repository.nonConformita(sopralluogoId)

    fun salvaCliente(cliente: Cliente, sede: Sede, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.inserisciClienteConSede(cliente, sede)
            onSaved()
        }
    }

    fun aggiornaCliente(cliente: Cliente, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.aggiornaCliente(cliente)
            onSaved()
        }
    }

    fun archiviaCliente(cliente: Cliente, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.archiviaCliente(cliente)
            onDone()
        }
    }

    fun aggiungiSede(sede: Sede, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.inserisciSede(sede)
            onSaved()
        }
    }

    fun creaSopralluogoHaccp(clienteId: Long, sedeId: Long, note: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            onCreated(repository.creaSopralluogoHaccp(clienteId, sedeId, note))
        }
    }

    fun aggiornaVerifica(verifica: VerificaSopralluogo) {
        viewModelScope.launch { repository.aggiornaVerifica(verifica) }
    }

    fun salvaNonConformita(nonConformita: NonConformita) {
        viewModelScope.launch { repository.salvaNonConformita(nonConformita) }
    }

    fun rimuoviNonConformita(verificaId: Long) {
        viewModelScope.launch { repository.eliminaNonConformitaPerVerifica(verificaId) }
    }

    fun chiudiSopralluogo(sopralluogo: Sopralluogo, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.chiudiSopralluogo(sopralluogo)) }
    }
}
