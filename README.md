# P&S Gestionale Android

Versione iniziale del gestionale mobile Progetti e Soluzioni per servizi HACCP, Sicurezza sul lavoro e GDPR.

## Funzioni v0.1

- Dashboard riepilogativa
- Archivio clienti
- Inserimento nuovo cliente
- Servizi HACCP / Sicurezza / GDPR
- Sede principale associata al cliente
- Pulsante **Naviga** verso la sede tramite app di navigazione installata
- Database locale Room
- Predisposizione tabella `sopralluoghi`
- Build APK automatica con GitHub Actions

## Struttura futura sopralluoghi

Il database è predisposto per collegare ogni sopralluogo a:

`Cliente -> Sede -> Tipo servizio -> Sopralluogo`

Le checklist operative HACCP, Sicurezza e GDPR verranno aggiunte in una fase successiva, verificando la normativa vigente e distinguendo obblighi normativi, evidenze, non conformità e buone prassi.

## Build su GitHub

1. Creare un nuovo repository GitHub.
2. Caricare tutto il contenuto di questa cartella nella root del repository.
3. Aprire la scheda **Actions**.
4. Selezionare **Build Android APK**.
5. Eseguire il workflow con **Run workflow**, oppure effettuare un push sul branch `main`.
6. Al termine, scaricare l'artifact `PS-Gestionale-v0.1-debug`.
7. All'interno dell'artifact è presente `app-debug.apk`.

Il workflow installa Gradle 9.5.0 direttamente su GitHub Actions, quindi non è necessario includere il `gradle-wrapper.jar` nel repository.

## Requisiti tecnici

- Android minSdk 26
- compileSdk / targetSdk 37
- Kotlin 2.3.21
- Android Gradle Plugin 9.3.0
- Jetpack Compose BOM 2026.08.00
- Room 2.8.4
