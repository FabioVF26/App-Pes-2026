package it.progettiesoluzioni.gestionale.ui.sopralluoghi

/**
 * Indicazioni di supporto al tecnico. Non sostituiscono la verifica della specifica
 * fattispecie, del soggetto obbligato e dell'edizione vigente della norma.
 */
object SanzioniSicurezza {
    fun proposta(codice: String): String = when (codice) {
        "S01" -> "Se la non conformità integra la violazione dell'art. 29, c. 1: arresto da 3 a 6 mesi o ammenda da € 3.559,60 a € 9.112,57 (art. 55, c. 1, D.Lgs. 81/2008). Verificare la fattispecie concreta."
        "S02" -> "In caso di omessa nomina del RSPP ex art. 17, c. 1, lett. b): arresto da 3 a 6 mesi o ammenda da € 3.559,60 a € 9.112,57 (art. 55, c. 1, D.Lgs. 81/2008). Verificare la fattispecie concreta."
        "S04" -> "Le sanzioni dipendono dal precetto dell'art. 26 concretamente violato. A titolo indicativo, per alcune violazioni sono previsti arresto da 2 a 4 mesi e/o ammende ai sensi dell'art. 55, c. 5. Verificare comma e soggetto obbligato."
        "S05", "S06" -> "Se la NC integra omessa/inadeguata formazione prevista dall'art. 37: arresto da 2 a 4 mesi o ammenda prevista dall'art. 55, c. 5, lett. c). Verificare il percorso formativo e il soggetto obbligato nell'edizione vigente."
        "S07" -> "La sanzione varia in funzione dell'obbligo di addestramento concretamente violato (artt. 37, 71, 73 o 77). Verificare il precetto specifico e il relativo articolo sanzionatorio."
        "S08" -> "Per omessa formazione degli addetti nei casi previsti dall'art. 37, c. 9, si applica il regime sanzionatorio dell'art. 55, c. 5, lett. c). Verificare anche gli obblighi di designazione e gestione emergenze."
        "S09" -> "Se la NC integra la violazione dell'art. 45, c. 1: arresto da 2 a 4 mesi o ammenda da € 1.067,88 a € 5.695,36 (art. 55, c. 5, lett. a), D.Lgs. 81/2008)."
        "S10" -> "La sanzione dipende dallo specifico obbligo antincendio/emergenza violato (artt. 43 e 46 e decreti attuativi). Verificare la fattispecie concreta e il relativo regime sanzionatorio."
        "S11", "S12" -> "La sanzione dipende dallo specifico obbligo di nomina/sorveglianza sanitaria violato (artt. 18, 25 e 41). Verificare precetto, soggetto obbligato e articolo sanzionatorio applicabile."
        "S13", "S14" -> "Per i requisiti dei luoghi di lavoro il regime sanzionatorio è previsto dal Titolo II, Capo II (art. 68). Individuare il punto dell'Allegato IV o il precetto concretamente violato."
        "S15" -> "Per le violazioni in materia di impianti elettrici il regime sanzionatorio dipende dal precetto degli artt. 80-86 concretamente violato. Verificare l'art. 87 e le disposizioni speciali applicabili."
        "S16", "S17" -> "Per attrezzature e abilitazioni la sanzione dipende dal comma degli artt. 70, 71 o 73 violato; il relativo regime è disciplinato dall'art. 87. Verificare la fattispecie concreta."
        "S18", "S19" -> "Per i DPI il regime sanzionatorio dipende dall'obbligo dell'art. 77 concretamente violato. Verificare soggetto obbligato e articolo sanzionatorio applicabile."
        "S20" -> "Per la movimentazione manuale dei carichi verificare il precetto degli artt. 168-169 concretamente violato e le sanzioni del Titolo VI."
        "S21" -> "Per i videoterminali verificare il precetto degli artt. 174-176 concretamente violato e le sanzioni del Titolo VII."
        "S22" -> "Per agenti chimici la sanzione dipende dallo specifico precetto del Titolo IX, Capo I concretamente violato. Verificare la disposizione sanzionatoria pertinente."
        "S23" -> "Per i rischi fisici (rumore, vibrazioni, CEM, ROA) individuare il capo del Titolo VIII e il precetto concretamente violato prima di determinare la sanzione."
        "S24" -> "La mancata valutazione dello stress lavoro-correlato può incidere sulla completezza della valutazione dei rischi ex art. 28. Verificare se ricorre la fattispecie sanzionata dall'art. 55."
        "S25" -> "Per lavori in quota/scale/PLE/funi la sanzione dipende dalla specifica disposizione del Titolo IV violata. Individuare il precetto prima di determinare la sanzione."
        "S26" -> "Per la segnaletica di sicurezza verificare lo specifico obbligo del Titolo V e il relativo articolo sanzionatorio applicabile."
        "S27" -> "La possibile sanzione dipende dall'obbligo sostanziale disatteso e dal soggetto responsabile (datore di lavoro, dirigente, preposto o lavoratore). Individuare il precetto concreto."
        else -> ""
    }
}
