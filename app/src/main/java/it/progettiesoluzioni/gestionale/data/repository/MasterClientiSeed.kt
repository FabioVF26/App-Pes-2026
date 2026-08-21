package it.progettiesoluzioni.gestionale.data.repository

data class MasterSedeSeed(
    val nome: String,
    val indirizzo: String,
    val zona: String,
    val principale: Boolean
)

data class MasterClienteSeed(
    val ragioneSociale: String,
    val attivita: String,
    val referente: String,
    val telefono: String,
    val note: String,
    val servizioHaccp: Boolean,
    val servizioSicurezza: Boolean,
    val servizioGdpr: Boolean,
    val sedi: List<MasterSedeSeed>
)

object MasterClientiSeed {
    const val versione = "17-08-2026"
    val clienti = listOf(

        MasterClienteSeed(
            ragioneSociale = "Foodie Srl",
            attivita = "Laboratorio Art.",
            referente = "Forte Fernando",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Giovanni Battista Bodoni 24", zona = "Testaccio", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Gambero Rosso",
            attivita = "Scuola Cucina",
            referente = "Luigi Salerno",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Ottavio Gasparri 13", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Gambaru Sushi",
            attivita = "Ristorante",
            referente = "Luca Carbonari",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via G. Chiabrera 71", zona = "Testaccio", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ma.Ma. Fish Srl",
            attivita = "Ristorante Pesch",
            referente = "Agrifoglio Maria Rita",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via dei Castani 27", zona = "Centocelle", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Villa Zaccardi Srl",
            attivita = "Albergo",
            referente = "Zaccardi Fabio",
            telefono = "",
            note = "Fulvio",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Circonvallazione Giannicolense 226", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Umeya",
            attivita = "",
            referente = "",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Il Pozzo ai Massimi",
            attivita = "Ristorante",
            referente = "Peroni Danilo",
            telefono = "",
            note = "Fulvio",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Portuense 962", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Il Pozzetto - Colli Srl",
            attivita = "Bar",
            referente = "Peroni Stefano",
            telefono = "",
            note = "Fulvio",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "V.le dei Colli Portuensi, 454", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "THAI MALAY",
            attivita = "Ristorante",
            referente = "Loo Chek Ang",
            telefono = "3495699474",
            note = "Fulvio",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Britannia 5", zona = "San Giovanni", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "4 Venti",
            attivita = "",
            referente = "HAWALADER SUJON",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "M&R Food Hall Srl",
            attivita = "Bar",
            referente = "Luca Ruggeri",
            telefono = "3496841457",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Ovidio 12", zona = "San Pietro/Prati", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "La Carovana Srl",
            attivita = "Ristorante",
            referente = "Zaccardi Fabio",
            telefono = "",
            note = "Fulvio",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Circonvallazione Giannicolense 226", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FRELA S.r.l.",
            attivita = "Laboratorio Art.",
            referente = "Forte Francesco",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Viale dei Quattro Venti nn. 150/", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ferrazzani Tiziana",
            attivita = "Alimentari",
            referente = "Tiziana Ferrazzani",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "PIAZZA SAN GIOVANNI DI DIO N. 7", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "IL GIRASOLE S.R.L.",
            attivita = "Ristorante",
            referente = "CHEN GUI E",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Palmiro Togliatti 776", zona = "Centocelle", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ISTITUTO RELIGIOSO CHEMIN NEUF",
            attivita = "Casa per Ferie",
            referente = "VLCKOVA JANA",
            telefono = "420 773558475",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Angelo Poliziano 38", zona = "San Giovanni", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "SM IMPORT SRL",
            attivita = "Logistica Alimentare",
            referente = "Alfonsi Simona",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Giovan Battista Molinelli n. 31/A", zona = "Pisana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Dodicidodici Srl",
            attivita = "Abbigliamento",
            referente = "Giampiero Badini",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Albano 48", zona = "Appio/Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "BIG FISH EMPIRE SRLS",
            attivita = "Pescheria",
            referente = "Fabio Buttaroni",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "L.go Irpinia 47, Roma", zona = "Prenestina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Il Baretto",
            attivita = "Bar",
            referente = "Sperandini Giuanluca",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Abate Ugone 27, 00152 Roma", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PRATO LAURO S.r.l.",
            attivita = "Ristorazione",
            referente = "ALIVERNINI DANIELE",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Fratelli Maristi 94", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "901 Tuscolana",
            attivita = "",
            referente = "",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Impresa Finucci",
            attivita = "Servizi",
            referente = "",
            telefono = "",
            note = "APVR",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "Fiumicino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Bazar Le Muse",
            attivita = "",
            referente = "Marco Scozzo",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Santo Bevitore",
            attivita = "Enoteca",
            referente = "PIERPAOLO FODDE",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA SUOR MARIA MAZZARELLO 17", zona = "Appio Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "KEBAB STATION S.R.L.S.",
            attivita = "Somministrazione",
            referente = "Barakat Hany Mohamed Aly",
            telefono = "3804765712",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "C.ne Gianicolense 2", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Retrò",
            attivita = "Somministrazione",
            referente = "Valerio",
            telefono = "3383488331",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Viale dei Colli Portuensi 172", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Pescheria Lin",
            attivita = "Pescheria",
            referente = "",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Marranella n. 20", zona = "Prenestina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "SICILIAINBOCCA IN PRATI S.R.L.",
            attivita = "Ristorazione",
            referente = "Francesca",
            telefono = "3276680969",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA EMILIO FAA DI BRUNO, 26", zona = "San Pietro/Prati", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "GLAM NAILS DI ERIKA CONSAGRA",
            attivita = "Estetica",
            referente = "Erika Consagna",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede San Giovanni", indirizzo = "PIAZZA CAMERINO n. 14", zona = "San Giovanni", principale = true),
                MasterSedeSeed(nome = "Sede Nomentana", indirizzo = "Via Donato Menichella, 58", zona = "Nomentana", principale = false)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Augusta S.r.l.",
            attivita = "Fast Food",
            referente = "Fuga Francesco",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Roma Est - Valle aurelia - Appio", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "La Base SRL",
            attivita = "Ristorazione",
            referente = "Ielasi Francesco",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede San Giovanni", indirizzo = "Via Cavour 268", zona = "San Giovanni", principale = true),
                MasterSedeSeed(nome = "Sede Centro", indirizzo = "Via Cavour 268-270-272", zona = "Centro", principale = false)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "SANITAS PROJECT ECO SYSTEM SRL",
            attivita = "Pulizie",
            referente = "Manetti Marina",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Piazza Rivarola n. 14", zona = "Tivoli", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Awad Abdelrahman Saad",
            attivita = "Bar",
            referente = "Awad Abdelrahman Saad",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.za Ippolito Nievo, 4", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "SILLI AMALIA",
            attivita = "Ristorazione",
            referente = "SILLI AMALIA",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Malnome Snc", zona = "Pisana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "RE LEON SRLS",
            attivita = "Ristorante",
            referente = "Rosa Cardelli",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede San Giovanni", indirizzo = "Via Gallia 11", zona = "San Giovanni", principale = true),
                MasterSedeSeed(nome = "Sede San Giovanni", indirizzo = "Via Nocera Umbra 18", zona = "San Giovanni", principale = false)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Z&L SRLS",
            attivita = "Ristorante",
            referente = "Zhu Wenbin",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA GIGGI SPADUCCI 6", zona = "Talenti", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Mazza Christian",
            attivita = "Pasta all'uovo",
            referente = "Mazza Christian",
            telefono = "3420738184",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Quarto Rubbie 62", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Fiosioterapia Cinecittà",
            attivita = "Fisioterapia",
            referente = "Katia Fiumento",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "DOLCI DESIDERI",
            attivita = "Pasticceria",
            referente = "Desideri Claudio",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "C.ne Gianicolense 141 A/B", zona = "Circ. Giannicolense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PAPETTO Srl",
            attivita = "Ristorante",
            referente = "Benedetti Luciana",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "C.ne Gianicolense 91-91/A", zona = "Circ. Giannicolense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "NUOVA GELCAR SRL",
            attivita = "Macelleria",
            referente = "Galli Remo",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Gallia n. 81", zona = "San Giovanni", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "RISTORANTE D.A.S. snc",
            attivita = "Ristorante",
            referente = "Daut",
            telefono = "3495632515",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via degli Olivi 51", zona = "Centocelle", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Nito srl",
            attivita = "Ristorante",
            referente = "Xu Onjie",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.za della Pace 7", zona = "Maccarese", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ucci di Civitenga Alessandro e Saraceni Luca",
            attivita = "Ristorante",
            referente = "Civitenga Alessandro",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Macerata 87", zona = "Prenestina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "KAIROS HEALTH CARE S.r.l.",
            attivita = "Laboratorio Art.",
            referente = "PIGNATELLI TIZIANA",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Luigi Bartolucci, 9", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "CARUSO IMMOBILIARE",
            attivita = "Immobiliare",
            referente = "Caruso Caterina",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Largo Plinio 2", zona = "Pomezia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PELLUTRI ALESSANDRO",
            attivita = "Chiosco",
            referente = "PELLUTRI ALESSANDRO",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via dell’Almone P. Egeria", zona = "Appio", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PAN DI ZUCCHERO SRL",
            attivita = "Ristorazione",
            referente = "Lepore Chiara",
            telefono = "3331844466",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Panetteria 12", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Mirò 30 srls",
            attivita = "Ristorante",
            referente = "Daniela Conti",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via dei Latini 72", zona = "Termini", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "The Forum Srl",
            attivita = "Ristorante",
            referente = "Proietti Gabriele",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via L’Aquila 6", zona = "Pigneto", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "BET KOSHER SAS DI ALBERTO TERRACINA",
            attivita = "",
            referente = "Alberto Terracina",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA CESARE PASCARELLA 36", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ERRESSE SRL",
            attivita = "Commercio",
            referente = "Soccolini Stefano",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Bartolomeo Chesi 30", zona = "Massimina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "HAPPY  SCHOOL",
            attivita = "Asilo",
            referente = "Beatrice Turrini",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA  DEI  MONTI  LEPINI  15", zona = "M. Sacro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Bombardieri",
            attivita = "",
            referente = "",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede da definire", indirizzo = "", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "STYVAN CAFFE’ S.R.L.",
            attivita = "Bar Ristorante",
            referente = "Napoli Stefano",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA GIAMBATTISTA BASSANI n. 21", zona = "C. Palocco", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "OASI PLUS SRL",
            attivita = "Ristorazione",
            referente = "Albrighi Salvatore Roberto Maria",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Attilio Mori 9", zona = "Pigneto", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ANTI SRLS",
            attivita = "Ristorazione",
            referente = "WALTER DI LERNIA",
            telefono = "3408383524",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Piazza degli Zingari 1", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "MDM FIBER SRLS",
            attivita = "Edilizia",
            referente = "DANIELE VITTIGLIO",
            telefono = "3515523217",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via dell'Usignolo, 25", zona = "Torre Maura", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PESCHERIA 8 & 10",
            attivita = "Pescheria",
            referente = "Matteo De Santis",
            telefono = "3388519941",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Graziano 8", zona = "Aurelia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Vox Populi",
            attivita = "Ristorazione",
            referente = "SIDOTI DANILO",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA DEI VOLSCI 115", zona = "Verano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FAMA S.R.L",
            attivita = "Ristorazione",
            referente = "Castellani Matteo",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA Prenestina 1229", zona = "Prenestina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Friends Srl",
            attivita = "Ristorazione",
            referente = "MONACCHIA MANOLO",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA DI VILLA BONELLI 43", zona = "Magliana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "VIALONE S.R.L.S.",
            attivita = "Catering",
            referente = "Scacia Angelo",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Famiano Nardini", zona = "Q. Italia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Corallo Srl",
            attivita = "Bar",
            referente = "RAHALEVICH TATSIANA",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza Santa Maria Ausiliatrice 48", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "MA.MA. S.r.l.s.",
            attivita = "Ristorazione",
            referente = "Masini Mara",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Viale Europa 312", zona = "Eur", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Associazione Giochiamo a crescere",
            attivita = "Asilo",
            referente = "Claudia Bartolini",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Lorenzo Vidaschi", zona = "Giannicolense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Di Pietro Daniele",
            attivita = "Ambulante",
            referente = "Daniele Di Pietro",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Agriturismo Sanna Angelo",
            attivita = "Ristorante",
            referente = "Sanna Angelo",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Cese 13 Ceprano", zona = "Ceprano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Rosè Srls",
            attivita = "Bar",
            referente = "Cervo Ivan",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Nazionale Tiburtina 289/291", zona = "Tivoli", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Carolyo di Petracci Carolina",
            attivita = "Gelateria",
            referente = "Petraci Carolina",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Giacomo Matteotti  16", zona = "Frascati", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Piretti Caterina",
            attivita = "Ambulante",
            referente = "Piretti Caterina",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Cassia 1101", zona = "Cassia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Hotel Corallo",
            attivita = "Hotel",
            referente = "Busetto Laura",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Gioiosa Marea 140", zona = "Fregene", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ARC INVESTMENTS S.R.L.",
            attivita = "Fast Food",
            referente = "De Crescenzo Alessandro",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Boccea  108", zona = "Boccea", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Fata SRL",
            attivita = "Ristorante",
            referente = "Sisti Roberto",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Lungomare Caio Duilio 20", zona = "Ostia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "MAMMA BASES SRL",
            attivita = "Laboratorio Art.",
            referente = "Garritano Nicola",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Spadola 23", zona = "Frascati", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "BRUCCHI RAFFAELLA",
            attivita = "Estetica",
            referente = "Brucchi Raffaella",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA FERDINANDO PALASCIANO 17", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "QUALITY SERVICES DI PROIETTI ANDREA E C. S.A.S.",
            attivita = "Ristorante",
            referente = "Proietti Andrea",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "LUNGOMARE DUCA DEGLI ABRUZZI 84", zona = "Ostia", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Mito S.A.S.di Norveti Laura e C.",
            attivita = "Gelaterie",
            referente = "Noverti Laura",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Principessa Pignatelli 64/A", zona = "Ciampino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "F.lli Cattani S.A.S. di Cattani Armando",
            attivita = "Ristorante",
            referente = "Cattani Armando",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Alberto Mario 17a/b", zona = "Monteverde", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "GCB S.R.L.S.",
            attivita = "Bar",
            referente = "Pacioni Patrizia",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Giacinto Carini  69", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Cantina Robertiello",
            attivita = "Produzione Olio / Vino",
            referente = "Simone Robertiello",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "CORSO FRANCESCO PETRARCA N. 40", zona = "Viterbo", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FOOD & DRINKS SRLS",
            attivita = "Ristorazione",
            referente = "Giannini Giancarlo",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza G.C.A. Dalla Chiesa n. 8/9", zona = "Fiumicino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "VIP FOOD S.R.L.",
            attivita = "Ristorante",
            referente = "Mondello Maria",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Portuense 369", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Saetta Servizi Srls",
            attivita = "Gestione alberghi",
            referente = "Maretto Roberto",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Capistrono 25/C", zona = "Torre Maura", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Salpa S.R.L.",
            attivita = "Pasticceria",
            referente = "Pannuti Paolo",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Largo Alberto Pepere 25/26", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "A.S.D. MONSTER BILLIARD",
            attivita = "ASD",
            referente = "De Tommaso Paolo",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Augusto Terenzi 2", zona = "Torre Maura", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ambiente & Decoro 2018  Srl",
            attivita = "Ambulante",
            referente = "Garritano Luca",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Commercio & Decoro 2018 S.R.L.",
            attivita = "Ambulante",
            referente = "Garritano Luca",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Desirè E 2012 S.R.L.",
            attivita = "Ambulante",
            referente = "Molinaro Veronica",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "GRUPPO MISTO - DECORO DI ROMA 2018 S.R.L.",
            attivita = "Ambulante",
            referente = "Garritano Luca",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Hilary S.R.L.",
            attivita = "Ambulante",
            referente = "Gianserra Antonella",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Mistery S.R.L.",
            attivita = "Ambulante",
            referente = "Molinaro Sofia",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Ambulante", zona = "Ambulante", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Testi Stefano",
            attivita = "Lab Gastronomia",
            referente = "Testi stefano",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Fabrizio Luscino 100", zona = "Cinecittà", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ARGO FOOD SRL",
            attivita = "Bar",
            referente = "Albrighi Salvatore Roberto Maria",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Ovidio 12", zona = "Vaticano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "POWER SRL",
            attivita = "Ristorante",
            referente = "LI DENGKE",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA GIGGI SPADUCCI 6", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Dolce Voy Marinari S.r.l.",
            attivita = "Pasticceria",
            referente = "Occhiobello",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Corso Trieste 95/B", zona = "Trieste", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Royaltrasporti Srls",
            attivita = "Corrieri",
            referente = "Urso",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via G.B. Molinelli", zona = "Pisana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ENJOY GROUP S.R.L.S.",
            attivita = "Parrucchiero",
            referente = "Rocchi Giuseppina",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Vincenzo Troya, 4/b", zona = "", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Movino",
            attivita = "enoteca",
            referente = "Francesco Salvi",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza Sempione", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Francesco Salvi",
            attivita = "Ristorazione",
            referente = "Francesco Salvi",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza Sempione", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Valica",
            attivita = "Ristorazione",
            referente = "Francesco Salvi",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza Sempione", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ramacciani Patrizia",
            attivita = "Estetica",
            referente = "Ramacciani Patrizia",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA DELL'AEROPORTO 121/C", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Villa Girasole SRL",
            attivita = "Ristorante",
            referente = "HU HAIFENG",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via delle Capannelle 142", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Giano Bistrot",
            attivita = "Ristorante",
            referente = "Zaccardi Alfredo",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via PORTUENSE 222", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Caffè Letterario",
            attivita = "Bar Tavola Calda",
            referente = "Pultrone Vincenzo Antonio",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Ostiense 85", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Sapori di Sicilia Srl",
            attivita = "Bar Tavola Calda",
            referente = "MOHAMMAD RUBEL",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza S. G. Bosco 68", zona = "Cinecittà", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Odontoiatria 3A",
            attivita = "Odontoiatra",
            referente = "Di Simone Anna",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA GUGLIELMO MENGARINI 6/8", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Centro Alessandrino",
            attivita = "Fisioterapia",
            referente = "D'Angelo Maria Grazia",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = false,
            servizioGdpr = true,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Torre Spaccata 110", zona = "Cinecittà", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "A.S.D. TAV BOTTACCIA",
            attivita = "Bar",
            referente = "Celso Giardini",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Antonio Neviani 148", zona = "Castel di Guido", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "LO CHALET 95  S.r.l.",
            attivita = "Ristorazione",
            referente = "Lilli Rita",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Affogalasino 40", zona = "Portuense", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "ACR FOOTBALL CLUB SSD A.R.L.",
            attivita = "Ass. Sportiva",
            referente = "Mariscoli Federica",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Settebagni n. 340", zona = "Settebagni", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "POLISPORTIVA ACADEMY SPORT CENTER SSD A.R.L.",
            attivita = "Ass. Sportiva",
            referente = "Mariscoli Giulia",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Settebagni n. 340", zona = "Settebagni", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Solo Sole Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Arenula 20/A,", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Paglia 40 Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Paglia 40", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Gestioni Ristoranti Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza San Callisto 7/a", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Sempre Noi Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Piazza Scipione Ammirato, 7", zona = "Alberone", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "P.M. e L. Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Piazza Sant'Egidio 12", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Poveri Noi Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "P.zza San Callisto 9/a", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Salvati Erminio S.r.l.",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Paglia 1", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Ancora Noi Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Enna 2", zona = "Appio Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Bravi Bravi Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via del Governo Vecchio 72", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Pane e Amore Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via del Governo Vecchio 86", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Steframarc Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Scala 1", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Godo Roma Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Garibaldi 27/g", zona = "Giannicolo", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Boni e Cari Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Pelliccia 47", zona = "Giannicolo", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "La Taverna Srl",
            attivita = "Ristorazione",
            referente = "Tulli Andrea",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via della Scala 1", zona = "Trastevere", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "EVENTI&SPETTACOLI S.r.l.s.",
            attivita = "Ristorazione",
            referente = "NEAGU BOGDAN",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Viale di Pescina Gagliarda 25/27", zona = "Fiumicino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "GRAMMO EUR SRL",
            attivita = "Bar past tav calda",
            referente = "Bonti Giada",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Tuscolana 946/a", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FIAMMA E POLLO S.r.l.s.",
            attivita = "Ristorazione",
            referente = "CAKA ASIA",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA FLAVIO STILICONE 145", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Monteverde Sporting Village S.S.D.",
            attivita = "Rist. bar tavola calda",
            referente = "Vittorio Florio",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Santorre di Santarosa 68", zona = "Monteverde", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "LA BERTA DI COLONNA CIRO",
            attivita = "Ristorazione",
            referente = "Colonna Ciro",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Ciro da Urbino 16", zona = "Pigneto", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "RORONOA SRL",
            attivita = "Pizza al Taglio",
            referente = "DI VINCENZO ANDREA",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA ALESSANDRO SEVERO 183", zona = "Eur", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "CONFARTIGIANATO ROMA CITTA' METROPOLITANA",
            attivita = "Associazione",
            referente = "Rotondo Andrea",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via del Poggio Laurentino 108", zona = "Eur", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "CONFARTIGIANATOSERVICE ROMA SRL",
            attivita = "SERVIZI",
            referente = "Schina Edoardo",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via del Poggio Laurentino 109", zona = "Eur", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "TOMAS 2000 SRL",
            attivita = "Bar",
            referente = "Tomassi Margherita",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Cesare Baronio 117/121", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "NERI CAR SERVICE SRLS",
            attivita = "Meccanico",
            referente = "Neri Alessio",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via di Ponte Galeria 273/275", zona = "Fiumicino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "CARLOTTA RUSSO",
            attivita = "Estetica",
            referente = "CARLOTTA RUSSO",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Donato Menichella, 59", zona = "Nomentana", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "PUERTO FINO SRLS",
            attivita = "Ristorazione",
            referente = "SUAREZ HERNANDEZ OSCAR ADRIAN",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "TeverEstate 2026 stagionale", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FISIOTERAPIA CINECITTA’",
            attivita = "Fisioterapia",
            referente = "Fiumiento Sonja",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = false,
            servizioGdpr = true,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA LUCIO PAPIRIO 28", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "V.M. S.R.L.",
            attivita = "Commercio",
            referente = "Davadò Stefano",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Muzio Scevola 113", zona = "Centro", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "AMICI S.R.L.",
            attivita = "Bar",
            referente = "MOHAMMAD RUBEL",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Piazza San Giovanni Bosco 68", zona = "Tuscolano", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "YACOUB JEAN CLAUDE",
            attivita = "Odontoiatra",
            referente = "YACOUB JEAN CLAUDE",
            telefono = "",
            note = "",
            servizioHaccp = false,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via Giuseppe Rosso 3", zona = "Balduina", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "MA.LA. S.R.L.",
            attivita = "Ristorazione",
            referente = "Manili Laura",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via del Pigneto", zona = "Pigneto", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "FRASCO SRLS",
            attivita = "Ristorazione",
            referente = "Zappalà Barbara",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = true,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "Via DI Tor Pagnotta n. 374", zona = "Laurentino", principale = true)
            )
        ),
        MasterClienteSeed(
            ragioneSociale = "Diana Di Pinto",
            attivita = "Bar",
            referente = "Diana Di Pinto",
            telefono = "",
            note = "",
            servizioHaccp = true,
            servizioSicurezza = false,
            servizioGdpr = false,
            sedi = listOf(
                MasterSedeSeed(nome = "Sede principale", indirizzo = "VIA SUOR MARIA MAZZARELLO 49/51", zona = "Tuscolano", principale = true)
            )
        )
    )
}
