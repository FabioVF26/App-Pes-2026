package it.progettiesoluzioni.gestionale.ui.dashboard

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandBlue
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange
import it.progettiesoluzioni.gestionale.util.BackupExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: GestionaleViewModel) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    val numeroClienti by viewModel.numeroClienti.collectAsStateWithLifecycle()
    val sopralluoghi by viewModel.sopralluoghi.collectAsStateWithLifecycle()
    val attivita by viewModel.attivitaScadenze.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haccp = clienti.count { it.servizioHaccp }
    val sicurezza = clienti.count { it.servizioSicurezza }
    val gdpr = clienti.count { it.servizioGdpr }
    val now = System.currentTimeMillis()
    val scadute = attivita.count { it.stato != "COMPLETATA" && it.scadenzaEpochMillis?.let { d -> d < now } == true }
    val entro30 = attivita.count { it.stato != "COMPLETATA" && it.scadenzaEpochMillis?.let { d -> d in now..(now + 30L * 24 * 60 * 60 * 1000) } == true }
    val aperte = attivita.filter { it.stato != "COMPLETATA" }.take(5)
    val clientiMap = clienti.associateBy { it.id }
    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Surface(modifier = Modifier.fillMaxWidth(), color = BrandNavy, shape = RoundedCornerShape(24.dp), shadowElevation = 3.dp) {
                Column(Modifier.padding(22.dp)) {
                    Text("P&S Gestionale", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                    Spacer(Modifier.height(4.dp)); Text("HACCP • Sicurezza • GDPR", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.82f))
                }
            }
        }
        item { Text("Situazione generale", style = MaterialTheme.typography.titleLarge) }
        item { StatCard("Clienti attivi", numeroClienti.toString(), Icons.Default.Business, BrandBlue, Color(0xFFE6F0FB)) }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) { StatCard("HACCP", haccp.toString(), Icons.Default.Restaurant, HaccpGreen, HaccpContainer, Modifier.weight(1f)); StatCard("Sicurezza", sicurezza.toString(), Icons.Default.Security, SafetyOrange, SafetyContainer, Modifier.weight(1f)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) { StatCard("GDPR", gdpr.toString(), Icons.Default.PrivacyTip, GdprPurple, GdprContainer, Modifier.weight(1f)); StatCard("Sopralluoghi", sopralluoghi.size.toString(), Icons.Default.Assignment, BrandNavy, Color(0xFFE7EBF6), Modifier.weight(1f)) } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) { StatCard("Scadute", scadute.toString(), Icons.Default.Event, Color(0xFFC62828), Color(0xFFFFE8E8), Modifier.weight(1f)); StatCard("Entro 30 gg", entro30.toString(), Icons.Default.Event, Color(0xFF9A5B00), Color(0xFFFFF1D8), Modifier.weight(1f)) } }
        item { Text("Prossime attività", style = MaterialTheme.typography.titleLarge, color = BrandNavy) }
        if (aperte.isEmpty()) item { Text("Nessuna attività aperta.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(aperte, key = { it.id }) { a ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) { Text(a.titolo, style = MaterialTheme.typography.titleMedium); Text(clientiMap[a.clienteId]?.ragioneSociale ?: "Cliente", color = MaterialTheme.colorScheme.onSurfaceVariant); Text(a.scadenzaEpochMillis?.let { "Scadenza ${fmt.format(Date(it))}" } ?: "Senza scadenza", style = MaterialTheme.typography.labelMedium) }
            }
        }
        item {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.creaBackup { snapshot ->
                    val uri = BackupExporter.creaBackup(context, snapshot)
                    val intent = Intent(Intent.ACTION_SEND).apply { type = "application/json"; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); putExtra(Intent.EXTRA_SUBJECT, "Backup P&S Gestionale") }
                    context.startActivity(Intent.createChooser(intent, "Esporta backup dati"))
                }
            }) { Icon(Icons.Default.Backup, contentDescription = null); Text("  Esporta backup dati") }
        }
        item { Text("Il backup esporta anagrafiche, sedi, attività, metadati documenti e sopralluoghi. I file documentali originali non sono incorporati.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: ImageVector, accent: Color, container: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = container), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(icon, contentDescription = null, tint = accent); Text(title, style = MaterialTheme.typography.labelLarge, color = accent); Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface) }
    }
}
