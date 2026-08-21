package it.progettiesoluzioni.gestionale.ui.clienti

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.data.repository.GestionaleRepository
import it.progettiesoluzioni.gestionale.data.repository.MasterClientiSeed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestionaleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GestionaleRepository(AppDatabase.getInstance(application))

    val clienti = repository.clienti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val numeroClienti = repository.numeroClienti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val sopralluoghi = repository.sopralluoghi().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val attivitaScadenze = repository.attivitaScadenze.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val documenti = repository.documenti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        val prefs = application.getSharedPreferences("ps_gestionale_prefs", 0)
        val chiaveImport = "master_clienti_${MasterClientiSeed.versione}"
        if (!prefs.getBoolean(chiaveImport, false)) {
            viewModelScope.launch {
                repository.importaMasterClienti()
                prefs.edit().putBoolean(chiaveImport, true).apply()
            }
        }
    }

    fun cliente(id: Long) = repository.cliente(id)
    fun sedi(clienteId: Long) = repository.sedi(clienteId)
    fun sopralluogo(id: Long) = repository.sopralluogo(id)
    fun sopralluoghiCliente(clienteId: Long) = repository.sopralluoghiCliente(clienteId)
    fun verifiche(sopralluogoId: Long) = repository.verifiche(sopralluogoId)
    fun nonConformita(sopralluogoId: Long) = repository.nonConformita(sopralluogoId)
    fun attivitaCliente(clienteId: Long) = repository.attivitaCliente(clienteId)
    fun documentiCliente(clienteId: Long) = repository.documentiCliente(clienteId)


    fun salvaAttivita(item: AttivitaScadenza, onSaved: () -> Unit = {}) {
        viewModelScope.launch { repository.inserisciAttivita(item); onSaved() }
    }

    fun aggiornaAttivita(item: AttivitaScadenza) {
        viewModelScope.launch { repository.aggiornaAttivita(item) }
    }

    fun eliminaAttivita(item: AttivitaScadenza) {
        viewModelScope.launch { repository.eliminaAttivita(item) }
    }

    fun salvaDocumento(item: DocumentoCliente, onSaved: () -> Unit = {}) {
        viewModelScope.launch { repository.inserisciDocumento(item); onSaved() }
    }

    fun eliminaDocumento(item: DocumentoCliente) {
        viewModelScope.launch { repository.eliminaDocumento(item) }
    }

    fun creaBackup(onReady: (it.progettiesoluzioni.gestionale.data.repository.BackupSnapshot) -> Unit) {
        viewModelScope.launch { onReady(repository.backupSnapshot()) }
    }

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

    fun creaSopralluogoSicurezza(clienteId: Long, sedeId: Long, note: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            onCreated(repository.creaSopralluogoSicurezza(clienteId, sedeId, note))
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

    fun eliminaSopralluogo(id: Long, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.eliminaSopralluogo(id)
            onDone()
        }
    }

    fun chiudiSopralluogo(sopralluogo: Sopralluogo, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.chiudiSopralluogo(sopralluogo)) }
    }
}
