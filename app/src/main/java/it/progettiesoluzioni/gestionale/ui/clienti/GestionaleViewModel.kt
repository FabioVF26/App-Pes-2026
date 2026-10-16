package it.progettiesoluzioni.gestionale.ui.clienti

import android.app.Application
import android.content.Context
import android.net.Uri
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
import it.progettiesoluzioni.gestionale.data.repository.RisultatoUnione
import it.progettiesoluzioni.gestionale.util.BackupImporter
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
        // Questo ViewModel viene creato solo dopo il login (vedi MainActivity), quindi
        // e' il punto giusto per avviare l'ascolto realtime dei dati condivisi su Firestore.
        repository.avviaSincronizzazione(viewModelScope)

        val prefs = application.getSharedPreferences("ps_gestionale_prefs", 0)
        val chiaveImport = "master_clienti_${MasterClientiSeed.versione}"
        if (!prefs.getBoolean(chiaveImport, false)) {
            viewModelScope.launch {
                // La lista master si importa solo dopo aver scaricato i dati gia' presenti nel cloud:
                // altrimenti un dispositivo ancora vuoto ricreerebbe aziende che esistono gia'.
                // Se non c'e' connessione l'importazione viene semplicemente rimandata al prossimo avvio.
                if (repository.attendiPrimaSincronizzazione(20_000)) {
                    repository.importaMasterClienti()
                    prefs.edit().putBoolean(chiaveImport, true).apply()
                }
            }
        }
    }

    fun cliente(id: String) = repository.cliente(id)
    fun sedi(clienteId: String) = repository.sedi(clienteId)
    fun sopralluogo(id: String) = repository.sopralluogo(id)
    fun sopralluoghiCliente(clienteId: String) = repository.sopralluoghiCliente(clienteId)
    fun verifiche(sopralluogoId: String) = repository.verifiche(sopralluogoId)
    fun nonConformita(sopralluogoId: String) = repository.nonConformita(sopralluogoId)
    fun attivitaCliente(clienteId: String) = repository.attivitaCliente(clienteId)
    fun documentiCliente(clienteId: String) = repository.documentiCliente(clienteId)

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

    fun ripristinaBackup(context: Context, uri: Uri, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = runCatching {
                val snapshot = BackupImporter.leggi(context, uri)
                repository.ripristinaBackup(snapshot)
            }
            onResult(result)
        }
    }

    fun contaDuplicati(onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repository.contaDuplicati()) }
    }

    fun unisciDuplicati(onResult: (Result<RisultatoUnione>) -> Unit) {
        viewModelScope.launch { onResult(runCatching { repository.unisciDuplicati() }) }
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

    fun creaSopralluogoHaccp(clienteId: String, sedeId: String, note: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            onCreated(repository.creaSopralluogoHaccp(clienteId, sedeId, note))
        }
    }

    fun creaSopralluogoSicurezza(clienteId: String, sedeId: String, note: String, onCreated: (String) -> Unit) {
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

    fun rimuoviNonConformita(verificaId: String) {
        viewModelScope.launch { repository.eliminaNonConformitaPerVerifica(verificaId) }
    }

    fun eliminaSopralluogo(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.eliminaSopralluogo(id)
            onDone()
        }
    }

    fun chiudiSopralluogo(sopralluogo: Sopralluogo, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.chiudiSopralluogo(sopralluogo)) }
    }
}
