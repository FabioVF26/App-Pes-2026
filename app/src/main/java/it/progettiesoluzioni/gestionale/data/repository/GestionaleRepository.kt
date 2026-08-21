package it.progettiesoluzioni.gestionale.data.repository

import androidx.room.withTransaction
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo

class GestionaleRepository(private val db: AppDatabase) {
    val clienti = db.clienteDao().osservaClienti()
    val numeroClienti = db.clienteDao().contaClientiAttivi()
    val attivitaScadenze = db.attivitaScadenzaDao().osservaTutte()
    val documenti = db.documentoClienteDao().osservaTutti()

    fun cliente(id: Long) = db.clienteDao().osservaCliente(id)
    fun sedi(clienteId: Long) = db.sedeDao().osservaSedi(clienteId)
    fun sopralluoghi() = db.sopralluogoDao().osservaTutti()
    fun sopralluoghiCliente(clienteId: Long) = db.sopralluogoDao().osservaPerCliente(clienteId)
    fun sopralluogo(id: Long) = db.sopralluogoDao().osserva(id)
    fun verifiche(sopralluogoId: Long) = db.verificaSopralluogoDao().osservaPerSopralluogo(sopralluogoId)
    fun nonConformita(sopralluogoId: Long) = db.nonConformitaDao().osservaPerSopralluogo(sopralluogoId)
    fun attivitaCliente(clienteId: Long) = db.attivitaScadenzaDao().osservaPerCliente(clienteId)
    fun documentiCliente(clienteId: Long) = db.documentoClienteDao().osservaPerCliente(clienteId)

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

    suspend fun importaMasterClienti(): Pair<Int, Int> = db.withTransaction {
        var clientiInseriti = 0
        var sediInserite = 0

        MasterClientiSeed.clienti.forEach { seed ->
            var clienteId = db.clienteDao().trovaIdPerRagioneSociale(seed.ragioneSociale)
            if (clienteId == null) {
                clienteId = db.clienteDao().inserisci(
                    Cliente(
                        ragioneSociale = seed.ragioneSociale,
                        attivita = seed.attivita,
                        legaleRappresentante = seed.referente,
                        telefono = seed.telefono,
                        note = seed.note,
                        servizioHaccp = seed.servizioHaccp,
                        servizioSicurezza = seed.servizioSicurezza,
                        servizioGdpr = seed.servizioGdpr
                    )
                )
                clientiInseriti++
            } else {
                val esistente = db.clienteDao().clientePerId(clienteId)
                if (esistente != null) {
                    val aggiornato = esistente.copy(
                        attivita = esistente.attivita.ifBlank { seed.attivita },
                        legaleRappresentante = esistente.legaleRappresentante.ifBlank { seed.referente },
                        telefono = esistente.telefono.ifBlank { seed.telefono },
                        note = esistente.note.ifBlank { seed.note },
                        servizioHaccp = esistente.servizioHaccp || seed.servizioHaccp,
                        servizioSicurezza = esistente.servizioSicurezza || seed.servizioSicurezza,
                        servizioGdpr = esistente.servizioGdpr || seed.servizioGdpr
                    )
                    if (aggiornato != esistente) db.clienteDao().aggiorna(aggiornato)
                }
            }

            seed.sedi.forEach { sedeSeed ->
                val sedeEsistente = db.sedeDao().trovaIdPerIndirizzo(clienteId, sedeSeed.indirizzo)
                if (sedeEsistente == null) {
                    db.sedeDao().inserisci(
                        Sede(
                            clienteId = clienteId,
                            nome = sedeSeed.nome,
                            indirizzo = sedeSeed.indirizzo,
                            note = if (sedeSeed.zona.isBlank()) "" else "Zona: ${sedeSeed.zona}",
                            principale = sedeSeed.principale
                        )
                    )
                    sediInserite++
                }
            }
        }
        clientiInseriti to sediInserite
    }


    suspend fun inserisciAttivita(item: AttivitaScadenza) = db.attivitaScadenzaDao().inserisci(item)
    suspend fun aggiornaAttivita(item: AttivitaScadenza) = db.attivitaScadenzaDao().aggiorna(item)
    suspend fun eliminaAttivita(item: AttivitaScadenza) = db.attivitaScadenzaDao().elimina(item)

    suspend fun inserisciDocumento(item: DocumentoCliente) = db.documentoClienteDao().inserisci(item)
    suspend fun aggiornaDocumento(item: DocumentoCliente) = db.documentoClienteDao().aggiorna(item)
    suspend fun eliminaDocumento(item: DocumentoCliente) = db.documentoClienteDao().elimina(item)

    suspend fun backupSnapshot(): BackupSnapshot = BackupSnapshot(
        clienti = db.clienteDao().tuttiSnapshot(),
        sedi = db.sedeDao().tutteSnapshot(),
        attivita = db.attivitaScadenzaDao().tutteSnapshot(),
        documenti = db.documentoClienteDao().tuttiSnapshot(),
        sopralluoghi = db.sopralluogoDao().tuttiSnapshot(),
        verifiche = db.verificaSopralluogoDao().tutteSnapshot(),
        nonConformita = db.nonConformitaDao().tutteSnapshot()
    )

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


    suspend fun creaSopralluogoSicurezza(clienteId: Long, sedeId: Long, note: String): Long = db.withTransaction {
        val id = db.sopralluogoDao().inserisci(
            Sopralluogo(
                clienteId = clienteId,
                sedeId = sedeId,
                tipoServizio = "SICUREZZA",
                dataOraEpochMillis = System.currentTimeMillis(),
                stato = "BOZZA",
                noteGenerali = note
            )
        )
        db.verificaSopralluogoDao().inserisciTutte(
            SicurezzaChecklist.voci.mapIndexed { index, voce ->
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

    suspend fun eliminaSopralluogo(id: Long) = db.sopralluogoDao().eliminaPerId(id)

    suspend fun chiudiSopralluogo(sopralluogo: Sopralluogo): Boolean {
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


object SicurezzaChecklist {
    val voci = listOf(
        ChecklistVoce("S01", "Documentazione e organizzazione", "Documento di valutazione dei rischi presente, coerente con attività, mansioni e rischi effettivi e aggiornato quando necessario", "D.Lgs. 81/2008, artt. 17, 28 e 29"),
        ChecklistVoce("S02", "Documentazione e organizzazione", "Organizzazione del servizio di prevenzione e protezione definita e incarichi formalizzati", "D.Lgs. 81/2008, artt. 17, 31-33"),
        ChecklistVoce("S03", "Documentazione e organizzazione", "RLS/RLST individuato e consultato nei casi previsti", "D.Lgs. 81/2008, artt. 47-50"),
        ChecklistVoce("S04", "Documentazione e organizzazione", "Gestione di appalti, cooperazione e coordinamento documentata ove applicabile", "D.Lgs. 81/2008, art. 26"),
        ChecklistVoce("S05", "Formazione e competenze", "Formazione dei lavoratori coerente con mansione e rischio e documentazione disponibile", "D.Lgs. 81/2008, art. 37; Accordo Stato-Regioni 17/04/2025"),
        ChecklistVoce("S06", "Formazione e competenze", "Formazione di dirigenti e preposti effettuata ove prevista e coerente con i ruoli effettivamente esercitati", "D.Lgs. 81/2008, artt. 18, 19 e 37; Accordo Stato-Regioni 17/04/2025"),
        ChecklistVoce("S07", "Formazione e competenze", "Addestramento specifico documentato per attrezzature, DPI e attività che lo richiedono", "D.Lgs. 81/2008, artt. 37, 71, 73 e 77"),
        ChecklistVoce("S08", "Emergenze", "Addetti alla prevenzione incendi e al primo soccorso individuati in numero adeguato e formati", "D.Lgs. 81/2008, artt. 18, 43-46"),
        ChecklistVoce("S09", "Emergenze", "Presidi di primo soccorso disponibili, accessibili e adeguatamente mantenuti", "D.Lgs. 81/2008, art. 45; D.M. 388/2003"),
        ChecklistVoce("S10", "Emergenze", "Misure antincendio, gestione dell'esodo e procedure di emergenza adeguate all'attività", "D.Lgs. 81/2008, artt. 43 e 46; decreti antincendio applicabili"),
        ChecklistVoce("S11", "Sorveglianza sanitaria", "Medico competente nominato quando la valutazione dei rischi individua obblighi di sorveglianza sanitaria", "D.Lgs. 81/2008, artt. 18, 25 e 41"),
        ChecklistVoce("S12", "Sorveglianza sanitaria", "Giudizi di idoneità e scadenze della sorveglianza sanitaria risultano gestiti nel rispetto della riservatezza", "D.Lgs. 81/2008, artt. 25 e 41"),
        ChecklistVoce("S13", "Luoghi di lavoro", "Luoghi di lavoro, vie di circolazione, pavimenti, scale, servizi e spazi risultano sicuri e mantenuti", "D.Lgs. 81/2008, artt. 63-64 e Allegato IV"),
        ChecklistVoce("S14", "Luoghi di lavoro", "Illuminazione, aerazione, microclima e condizioni dei locali risultano adeguati alle attività svolte", "D.Lgs. 81/2008, Allegato IV"),
        ChecklistVoce("S15", "Impianti e attrezzature", "Impianti elettrici e protezioni risultano mantenuti e gestiti in condizioni di sicurezza", "D.Lgs. 81/2008, artt. 80-86"),
        ChecklistVoce("S16", "Impianti e attrezzature", "Attrezzature di lavoro sono idonee, mantenute e sottoposte ai controlli previsti", "D.Lgs. 81/2008, artt. 70-71"),
        ChecklistVoce("S17", "Impianti e attrezzature", "Uso delle attrezzature riservato a personale informato, formato e, ove richiesto, abilitato", "D.Lgs. 81/2008, artt. 71 e 73; Accordo Stato-Regioni 17/04/2025 ove applicabile"),
        ChecklistVoce("S18", "DPI", "DPI individuati sulla base dei rischi, consegnati e mantenuti in efficienza", "D.Lgs. 81/2008, artt. 74-79"),
        ChecklistVoce("S19", "DPI", "Lavoratori informati, formati e addestrati all'uso dei DPI quando previsto", "D.Lgs. 81/2008, art. 77"),
        ChecklistVoce("S20", "Rischi specifici", "Rischio da movimentazione manuale dei carichi valutato e gestito ove presente", "D.Lgs. 81/2008, Titolo VI, artt. 167-171"),
        ChecklistVoce("S21", "Rischi specifici", "Rischio da videoterminali valutato e gestito ove applicabile", "D.Lgs. 81/2008, Titolo VII, artt. 172-179"),
        ChecklistVoce("S22", "Rischi specifici", "Rischio da agenti chimici valutato, con SDS, procedure, stoccaggio e misure di prevenzione coerenti", "D.Lgs. 81/2008, Titolo IX, Capo I"),
        ChecklistVoce("S23", "Rischi specifici", "Rischi fisici pertinenti all'attività (rumore, vibrazioni, CEM, ROA) valutati ove applicabili", "D.Lgs. 81/2008, Titolo VIII"),
        ChecklistVoce("S24", "Rischi specifici", "Rischio da stress lavoro-correlato incluso nella valutazione dei rischi", "D.Lgs. 81/2008, art. 28"),
        ChecklistVoce("S25", "Lavori in quota", "Lavori in quota, scale, ponteggi, PLE o sistemi su funi gestiti secondo requisiti specifici ove presenti", "D.Lgs. 81/2008, Titolo IV, artt. 111-116 e disposizioni pertinenti"),
        ChecklistVoce("S26", "Segnaletica e comportamenti", "Segnaletica di sicurezza presente e coerente con i rischi residui e le procedure", "D.Lgs. 81/2008, Titolo V"),
        ChecklistVoce("S27", "Segnaletica e comportamenti", "Istruzioni, procedure e comportamenti osservati risultano coerenti con le misure previste nel DVR", "D.Lgs. 81/2008, artt. 18, 19 e 20")
    )
}


data class BackupSnapshot(
    val clienti: List<Cliente>,
    val sedi: List<Sede>,
    val attivita: List<AttivitaScadenza>,
    val documenti: List<DocumentoCliente>,
    val sopralluoghi: List<Sopralluogo>,
    val verifiche: List<VerificaSopralluogo>,
    val nonConformita: List<NonConformita>
)
