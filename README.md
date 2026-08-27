# P&S Gestionale v0.9.0

Versione successiva alla v0.8.0. Il modulo Lavoratori resta escluso dalla roadmap.

## Novità principali
- Ripristino del backup JSON direttamente dalla Dashboard.
- Selezione del file backup con il file picker Android.
- Verifica del formato e delle relazioni interne prima del ripristino.
- Conferma esplicita prima di sostituire i dati presenti nell'app.
- Ripristino transazionale di clienti, sedi, attività, documenti, sopralluoghi, verifiche e non conformità, mantenendo gli ID e le relazioni originarie.
- In caso di file non valido il database esistente non viene modificato.
- Le schede servizio HACCP / Sicurezza / GDPR diventano operative, con azioni rapide per nuova attività e nuovo documento.
- Nelle schede HACCP e Sicurezza è possibile avviare direttamente un nuovo sopralluogo.
- Le schede servizio evidenziano attività scadute e documenti con scadenza superata.
- Lo storico sopralluoghi della scheda servizio è apribile direttamente con un tocco.
- Creando attività o documenti dalla scheda servizio, cliente e servizio risultano già preselezionati.
- Dashboard con versione visibile v0.9.0 e sezione dedicata Backup e ripristino.

## Backup: nota importante
Il backup JSON contiene i dati gestionali e i riferimenti URI ai file/foto. PDF, immagini e documenti originali non vengono incorporati nel JSON. Dopo il ripristino i collegamenti ai file funzionano solo se le risorse originali sono ancora disponibili e accessibili sul dispositivo.

## Database
Nessuna nuova migrazione Room rispetto alla v0.8.0: lo schema resta alla versione 5.

## Build GitHub Actions
Artifact: `PS-Gestionale-v0.9-debug`

Configurazione invariata:
- compileSdk 37
- targetSdk 37
- Android SDK `platforms;android-37.0`
- Gradle 9.5.0
- Room 2.8.4 + KSP



## v0.11.0 — Firebase Authentication

- accesso con email/password tramite Firebase Authentication;
- recupero password via email;
- registrazione libera rimossa: gli account vengono creati/abilitati dall’amministratore;
- dipendenza aggiornata a `firebase-auth` (modulo principale, senza `-ktx`);
- Firestore rimosso finché non viene realmente utilizzato;
- gestione errori basata sulle eccezioni Firebase;
- versione applicazione `0.11.0` (`versionCode 16`).

## v0.10.0 — Promemoria scadenze
- notifiche locali automatiche a 30, 15, 7 e 1 giorno e il giorno della scadenza;
- popup in dashboard per scadenze superate o entro 7 giorni;
- migrazione Room 5→6 senza cancellazione dati;
- apertura della schermata Scadenze toccando la notifica;
- backup compatibile con il nuovo flag promemoria.
