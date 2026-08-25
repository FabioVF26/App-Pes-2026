package it.progettiesoluzioni.gestionale.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.progettiesoluzioni.gestionale.data.database.AppDatabase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ScadenzaWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = try {
        val db = AppDatabase.getInstance(applicationContext)
        val zona = ZoneId.systemDefault()
        val oggi = LocalDate.now(zona)
        val da = oggi.atStartOfDay(zona).toInstant().toEpochMilli()
        val a = oggi.plusDays(31).atStartOfDay(zona).toInstant().toEpochMilli() - 1
        val soglie = setOf(30L, 15L, 7L, 1L, 0L)

        db.attivitaScadenzaDao().attiveConScadenzaTra(da, a).forEach { item ->
            val scadenza = item.scadenzaEpochMillis ?: return@forEach
            val data = Instant.ofEpochMilli(scadenza).atZone(zona).toLocalDate()
            val giorni = ChronoUnit.DAYS.between(oggi, data)
            if (giorni in soglie) {
                val cliente = db.clienteDao().clientePerId(item.clienteId)?.ragioneSociale ?: "Cliente"
                val intestazione = when (giorni) {
                    0L -> "Scadenza oggi"
                    1L -> "Scadenza domani"
                    else -> "Scadenza tra $giorni giorni"
                }
                NotificationHelper.mostra(
                    applicationContext,
                    item.id,
                    giorni,
                    intestazione,
                    "$cliente • ${item.titolo}"
                )
            }
        }
        Result.success()
    } catch (_: Throwable) {
        Result.retry()
    }
}
