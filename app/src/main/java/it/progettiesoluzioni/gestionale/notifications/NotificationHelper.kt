package it.progettiesoluzioni.gestionale.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.progettiesoluzioni.gestionale.MainActivity

object NotificationHelper {
    const val CHANNEL_ID = "scadenze"

    fun creaCanale(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scadenze e promemoria",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Promemoria HACCP, Sicurezza, GDPR e attività calendarizzate" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    fun mostra(context: Context, scadenzaId: String, giorni: Long, titolo: String, corpo: String) {
        if (giaInviata(context, scadenzaId, giorni)) return
        creaCanale(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            action = MainActivity.ACTION_OPEN_SCADENZE
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            (scadenzaId.hashCode() * 31 + giorni.toInt()),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titolo)
            .setContentText(corpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(corpo))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        if (Build.VERSION.SDK_INT < 33 || ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(scadenzaId.hashCode() * 31 + giorni.coerceAtLeast(0).toInt(), notification)
            segnaInviata(context, scadenzaId, giorni)
        }
    }

    private fun key(scadenzaId: String, giorni: Long) = "${scadenzaId}_$giorni"
    private fun giaInviata(context: Context, scadenzaId: String, giorni: Long): Boolean =
        context.getSharedPreferences("ps_scadenze_notifiche", Context.MODE_PRIVATE).getBoolean(key(scadenzaId, giorni), false)
    private fun segnaInviata(context: Context, scadenzaId: String, giorni: Long) {
        context.getSharedPreferences("ps_scadenze_notifiche", Context.MODE_PRIVATE).edit().putBoolean(key(scadenzaId, giorni), true).apply()
    }
}
