package it.progettiesoluzioni.gestionale.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ScadenzaScheduler {
    private const val UNIQUE_WORK = "controllo_scadenze_giornaliero"

    fun programma(context: Context) {
        val work = PeriodicWorkRequestBuilder<ScadenzaWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    fun controllaOra(context: Context) {
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<ScadenzaWorker>().build())
    }
}
