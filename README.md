# P&S Gestionale v0.8.0

Versione successiva alla v0.7.3. Il modulo Lavoratori è stato escluso su richiesta.

## Novità
- Scadenze e attività operative collegate ai clienti.
- Filtri: aperte, scadute, entro 30 giorni, completate, tutte.
- Priorità e servizio (Generale, HACCP, Sicurezza, GDPR).
- Possibilità di completare o eliminare un'attività.
- Archivio documenti con scelta file Android, cliente, sede facoltativa, servizio, categoria, scadenza e note.
- Apertura dei documenti registrati dall'app.
- Schede servizio HACCP / Sicurezza / GDPR accessibili dalla scheda cliente.
- Ogni scheda servizio riepiloga attività, documenti e sopralluoghi pertinenti.
- Dashboard aggiornata con sopralluoghi reali, attività scadute, attività entro 30 giorni e prossime attività.
- Esportazione backup JSON con clienti, sedi, attività, documenti, sopralluoghi, verifiche e non conformità.
- Il backup conserva i riferimenti ai file/foto, ma non incorpora i file binari originali.
- Migrazione Room automatica database v4 -> v5: i dati esistenti vengono mantenuti.

## Non incluso
- Modulo Lavoratori (escluso dalla roadmap).
- Ripristino automatico del backup: da implementare dopo verifica del formato di esportazione.

## Build GitHub Actions
Artifact: `PS-Gestionale-v0.8-debug`

Configurazione invariata:
- compileSdk 37
- targetSdk 37
- Android SDK `platforms;android-37.0`
- Gradle 9.5.0
- Room 2.8.4 + KSP
