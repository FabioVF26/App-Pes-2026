package it.progettiesoluzioni.gestionale.data.repository

import androidx.room.withTransaction
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo

class GestionaleRepository(private val db: AppDatabase) {
    val clienti = db.clienteDao().osservaClienti()
    val numeroClienti = db.clienteDao().contaClientiAttivi()

    fun cliente(id: Long) = db.clienteDao().osservaCliente(id)
    fun sedi(clienteId: Long) = db.sedeDao().osservaSedi(clienteId)
    fun sopralluoghi() = db.sopralluogoDao().osservaTutti()
    fun sopralluogo(id: Long) = db.sopralluogoDao().osserva(id)
    fun verifiche(sopralluogoId: Long) = db.verificaSopralluogoDao().osservaPerSopralluogo(sopralluogoId)
    fun nonConformita(sopralluogoId: Long) = db.nonConformitaDao().osservaPerSopralluogo(sopralluogoId)

    suspend fun inserisciClienteConSede(cliente: Cliente, sede: Sede) {
        db.withTransaction {
            val clienteId = db.clienteDao().inserisci(cliente)
            db.sedeDao().inserisci(sede.copy(clienteId = clienteId))
        }
    }

    suspend fun aggiornaCliente(cliente: Cliente) = db.clienteDao().aggiorna(cliente)
    suspend fun archiviaCliente(cliente: Cliente) = db.clienteDao().aggiorna(cliente.copy(attivo = false))
    suspend fun inserisciSede(sede: Sede) = db.sedeDao().inserisci(sede)
    suspend fun aggiornaSede(sede: Sede) = db.sedeDao().aggiorna(sede)
    suspend fun eliminaSede(sede: Sede) = db.sedeDao().elimina(sede)

    suspend fun creaSopralluogoHaccp(clienteId: Long, sedeId: Long, note: String): Long = db.withTransaction {
        val id = db.sopralluogoDao().inserisci(
            Sopralluogo(
                clienteId = clienteId,
                sedeId = sedeId,
                tipoServizio = "HACCP",
                dataOraEpochMillis = System.currentTimeMillis(),
                stato = "BOZZA",
                noteGenerali = note
            )
        )
        db.verificaSopralluogoDao().inserisciTutte(
            HaccpChecklist.voci.mapIndexed { index, voce ->
                VerificaSopralluogo(
                    sopralluogoId = id,
                    codice = voce.codice,
                    sezione = voce.sezione,
                    titolo = voce.titolo,
                    riferimentoNormativo = voce.riferimento,
                    ordine = index
                )
            }
        )
        id
    }

    suspend fun aggiornaVerifica(verifica: VerificaSopralluogo) = db.verificaSopralluogoDao().aggiorna(verifica)

    suspend fun salvaNonConformita(nonConformita: NonConformita) {
        val esistente = db.nonConformitaDao().perVerifica(nonConformita.verificaId)
        if (esistente == null) db.nonConformitaDao().inserisci(nonConformita)
        else db.nonConformitaDao().aggiorna(nonConformita.copy(id = esistente.id))
    }

    suspend fun eliminaNonConformitaPerVerifica(verificaId: Long) = db.nonConformitaDao().eliminaPerVerifica(verificaId)

    suspend fun chiudiSopralluogo(sopralluogo: Sopralluogo): Boolean {
        if (db.verificaSopralluogoDao().contaDaVerificare(sopralluogo.id) > 0) return false
        db.sopralluogoDao().aggiorna(sopralluogo.copy(stato = "CHIUSO"))
        return true
    }
}

data class ChecklistVoce(val codice: String, val sezione: String, val titolo: String, val riferimento: String)

object HaccpChecklist {
    val voci = listOf(
        ChecklistVoce("H01", "Documentazione e autocontrollo", "Piano di autocontrollo disponibile, pertinente all'attività e aggiornato", "Reg. (CE) 852/2004, art. 5"),
        ChecklistVoce("H02", "Documentazione e autocontrollo", "Registrazioni previste dal piano di autocontrollo compilate e conservate", "Reg. (CE) 852/2004, art. 5"),
        ChecklistVoce("H03", "Documentazione e autocontrollo", "Procedure di gestione delle non conformità e azioni correttive applicate", "Reg. (CE) 852/2004, art. 5"),
        ChecklistVoce("H04", "Locali e requisiti strutturali", "Locali mantenuti puliti, in buone condizioni e idonei alle operazioni svolte", "Reg. (CE) 852/2004, All. II, Cap. I-II"),
        ChecklistVoce("H05", "Locali e requisiti strutturali", "Pavimenti, pareti, soffitti e superfici risultano integri, lavabili e mantenuti in condizioni igieniche", "Reg. (CE) 852/2004, All. II, Cap. II"),
        ChecklistVoce("H06", "Locali e requisiti strutturali", "Lavamani disponibili, accessibili e dotati dei necessari presidi per l'igiene delle mani", "Reg. (CE) 852/2004, All. II, Cap. I"),
        ChecklistVoce("H07", "Pulizia e sanificazione", "Programma di pulizia e sanificazione adeguato e concretamente applicato", "Reg. (CE) 852/2004, All. II, Cap. V-IX"),
        ChecklistVoce("H08", "Pulizia e sanificazione", "Prodotti e attrezzature per pulizia stoccati evitando contaminazioni degli alimenti", "Reg. (CE) 852/2004, All. II, Cap. I-II"),
        ChecklistVoce("H09", "Infestanti e rifiuti", "Sono presenti misure efficaci di prevenzione e controllo degli infestanti", "Reg. (CE) 852/2004, All. II, Cap. IX"),
        ChecklistVoce("H10", "Infestanti e rifiuti", "Rifiuti rimossi e contenuti in recipienti idonei, mantenuti in condizioni igieniche", "Reg. (CE) 852/2004, All. II, Cap. VI"),
        ChecklistVoce("H11", "Conservazione e temperature", "Alimenti deperibili mantenuti a temperature adeguate e catena del freddo preservata", "Reg. (CE) 852/2004, All. II, Cap. IX"),
        ChecklistVoce("H12", "Conservazione e temperature", "Frigoriferi/congelatori sono efficienti e le temperature risultano controllate secondo il piano", "Reg. (CE) 852/2004, art. 5 e All. II, Cap. IX"),
        ChecklistVoce("H13", "Conservazione e temperature", "Separazione tra alimenti crudi, cotti, pronti al consumo e sostanze non alimentari adeguata", "Reg. (CE) 852/2004, All. II, Cap. IX"),
        ChecklistVoce("H14", "Materie prime e tracciabilità", "Materie prime e prodotti sono identificabili e corredati dalle informazioni necessarie alla rintracciabilità", "Reg. (CE) 178/2002, art. 18"),
        ChecklistVoce("H15", "Materie prime e tracciabilità", "Prodotti scaduti, alterati o non idonei sono assenti o correttamente segregati", "Reg. (CE) 178/2002, artt. 14 e 19"),
        ChecklistVoce("H16", "Allergeni e informazioni", "Informazioni sugli allergeni sono disponibili, aggiornate e coerenti con preparazioni/ingredienti", "Reg. (UE) 1169/2011, artt. 9, 21 e All. II"),
        ChecklistVoce("H17", "Allergeni e informazioni", "Sono adottate misure organizzative per limitare contaminazioni crociate da allergeni", "Reg. (CE) 852/2004, All. II, come modificato dal Reg. (UE) 2021/382"),
        ChecklistVoce("H18", "MOCA e attrezzature", "Materiali e oggetti destinati al contatto con alimenti sono idonei all'uso previsto e in buono stato", "Reg. (CE) 1935/2004; Reg. (CE) 852/2004, All. II, Cap. V"),
        ChecklistVoce("H19", "MOCA e attrezzature", "Attrezzature e superfici a contatto con alimenti sono pulite, integre e facilmente sanificabili", "Reg. (CE) 852/2004, All. II, Cap. V"),
        ChecklistVoce("H20", "Personale e igiene", "Personale mantiene adeguato livello di igiene personale e abbigliamento idoneo", "Reg. (CE) 852/2004, All. II, Cap. VIII"),
        ChecklistVoce("H21", "Personale e formazione", "Addetti sono supervisionati e/o formati in materia di igiene alimentare in relazione all'attività svolta", "Reg. (CE) 852/2004, All. II, Cap. XII"),
        ChecklistVoce("H22", "Acqua e approvvigionamento", "Acqua utilizzata nelle operazioni alimentari è adeguata e gestita secondo i requisiti applicabili", "Reg. (CE) 852/2004, All. II, Cap. VII")
    )
}
