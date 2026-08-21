package it.progettiesoluzioni.gestionale.data.repository

import androidx.room.withTransaction
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.Sede

class GestionaleRepository(private val db: AppDatabase) {
    val clienti = db.clienteDao().osservaClienti()
    val numeroClienti = db.clienteDao().contaClientiAttivi()

    fun cliente(id: Long) = db.clienteDao().osservaCliente(id)
    fun sedi(clienteId: Long) = db.sedeDao().osservaSedi(clienteId)
    fun sopralluoghi() = db.sopralluogoDao().osservaTutti()

    suspend fun inserisciClienteConSede(cliente: Cliente, sede: Sede) {
        db.withTransaction {
            val clienteId = db.clienteDao().inserisci(cliente)
            db.sedeDao().inserisci(sede.copy(clienteId = clienteId))
        }
    }
}
