# P&S Gestionale v0.10.0 — Modulo promemoria scadenze

Integrazione eseguita direttamente sul progetto completo v0.9.

## Modifiche principali
- Riutilizzata l'entità esistente `AttivitaScadenza` (nessuna tabella duplicata).
- Aggiunto `promemoriaAttivo` con valore predefinito `true`.
- Database Room aggiornato da versione 5 a versione 6 con migrazione non distruttiva.
- Aggiunto WorkManager per il controllo giornaliero delle scadenze.
- Promemoria previsti a 30, 15, 7 e 1 giorno e il giorno della scadenza.
- Aggiunto controllo immediato all'avvio dell'app e dopo il salvataggio di una nuova scadenza.
- Aggiunto canale notifiche Android e gestione permesso `POST_NOTIFICATIONS` per Android 13+.
- Tocco della notifica: apertura della sezione `Scadenze`.
- Popup in dashboard quando sono presenti scadenze superate o entro 7 giorni.
- Flag `PROMEMORIA` visualizzato nella scheda di una scadenza calendarizzata con promemoria attivo.
- Backup JSON esteso con `promemoriaAttivo`; i backup v0.9 restano importabili (default `true`).
- Versione app aggiornata a 0.10.0 / versionCode 15.
- Artifact GitHub Actions rinominato in `PS-Gestionale-v0.10-debug`.

## Nota sulla compilazione
Il progetto mantiene il workflow GitHub Actions già presente nella v0.9. Nell'ambiente di generazione locale non è installato Gradle/Android SDK, pertanto la compilazione APK deve essere eseguita dal workflow GitHub incluso nel progetto.
