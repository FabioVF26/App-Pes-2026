# Correzioni v0.11.0

Correzioni applicate alla versione con Firebase Authentication:

- `versionCode` aggiornato a 16;
- `versionName` aggiornato a 0.11.0;
- artifact GitHub Actions aggiornato a `PS-Gestionale-v0.11-debug`;
- dashboard aggiornata a v0.11.0;
- `firebase-auth-ktx` sostituito con `firebase-auth`;
- dipendenza Firestore rimossa perché non ancora utilizzata;
- eliminata la registrazione libera dall'interfaccia;
- account previsti come creati/abilitati dall'amministratore;
- gestione errori Firebase basata su classi di eccezione;
- eliminato l'uso di `kotlinx.coroutines.tasks.await` senza dipendenza dedicata;
- mantenuti Room v6, migrazione 5→6, promemoria e notifiche della v0.10.

## Build

Il progetto conserva il workflow GitHub Actions con Gradle 9.5.0 e Android SDK 37.
La validazione definitiva deve essere effettuata eseguendo `:app:assembleDebug` nel workflow.
