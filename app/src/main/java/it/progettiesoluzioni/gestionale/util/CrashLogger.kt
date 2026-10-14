package it.progettiesoluzioni.gestionale.util

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Salva su file l'errore che provoca la chiusura imprevista dell'app, cosi' al riavvio
 * si puo' leggere (e copiare) senza bisogno di collegare il telefono a un computer.
 */
object CrashLogger {
    private const val NOME_FILE = "ultimo_crash.txt"
    private var installato = false

    fun installa(context: Context) {
        if (installato) return
        installato = true
        val appContext = context.applicationContext
        val gestorePrecedente = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, errore ->
            try {
                val data = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY).format(Date())
                val testo = "Data: $data\nThread: ${thread.name}\n\n${Log.getStackTraceString(errore)}"
                File(appContext.filesDir, NOME_FILE).writeText(testo)
            } catch (_: Throwable) {
                // Non deve mai impedire la normale gestione dell'errore.
            }
            gestorePrecedente?.uncaughtException(thread, errore)
        }
    }

    /** Restituisce l'ultimo errore registrato (se c'e') e lo cancella, cosi' viene mostrato una sola volta. */
    fun leggiEPulisci(context: Context): String? {
        val file = File(context.applicationContext.filesDir, NOME_FILE)
        if (!file.exists()) return null
        val testo = runCatching { file.readText() }.getOrNull()
        file.delete()
        return testo?.takeIf { it.isNotBlank() }
    }
}
