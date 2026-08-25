package it.progettiesoluzioni.gestionale.ui.servizi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServizioClienteScreen(
    clienteId: Long,
    tipo: String,
    viewModel: GestionaleViewModel,
    onNuovaAttivita: () -> Unit,
    onNuovoDocumento: () -> Unit,
    onNuovoSopralluogo: (() -> Unit)? = null,
    onApriSopralluogo: (Long, String) -> Unit = { _, _ -> }
) {
    val cliente by viewModel.cliente(clienteId).collectAsStateWithLifecycle(initialValue = null)
    val attivita by viewModel.attivitaCliente(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val documenti by viewModel.documentiCliente(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val sopralluoghi by viewModel.sopralluoghiCliente(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val t = tipo.uppercase()
    val accent = when(t) { "HACCP" -> HaccpGreen; "SICUREZZA" -> SafetyOrange; "GDPR" -> GdprPurple; else -> BrandNavy }
    val container = when(t) { "HACCP" -> HaccpContainer; "SICUREZZA" -> SafetyContainer; "GDPR" -> GdprContainer; else -> Color(0xFFE7EBF6) }
    val attFil = attivita.filter { it.servizio == t || it.servizio == "GENERALE" }
    val docFil = documenti.filter { it.servizio == t || it.servizio == "GENERALE" }
    val soprFil = sopralluoghi.filter { it.tipoServizio == t }
    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
    val now = System.currentTimeMillis()
    val attAperte = attFil.filter { it.stato != "COMPLETATA" }
    val attScadute = attAperte.count { it.scadenzaEpochMillis?.let { d -> d < now } == true }
    val docScaduti = docFil.count { it.scadenzaEpochMillis?.let { d -> d < now } == true }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Surface(color = container, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Scheda servizio $t", style = MaterialTheme.typography.headlineSmall, color = accent)
                    Text(cliente?.ragioneSociale ?: "", style = MaterialTheme.typography.titleMedium)
                    Text("Centro operativo del servizio: attività, documenti e storico.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat("Attività", attAperte.size, Icons.Default.Event, accent, Modifier.weight(1f))
                MiniStat("Documenti", docFil.size, Icons.Default.Description, accent, Modifier.weight(1f))
                if (t != "GDPR") MiniStat("Sopralluoghi", soprFil.size, Icons.Default.Assignment, accent, Modifier.weight(1f))
            }
        }
        if (attScadute > 0 || docScaduti > 0) {
            item {
                Surface(color = Color(0xFFFFF1E8), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(13.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = SafetyOrange)
                        Text("Attenzione: $attScadute attività scadute e $docScaduti documenti con scadenza superata.")
                    }
                }
            }
        }
        item { Text("Azioni rapide", style = MaterialTheme.typography.titleLarge, color = BrandNavy) }
        item {
            Button(onClick = onNuovaAttivita, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Event, contentDescription = null)
                Text("  Nuova attività / scadenza")
            }
        }
        item {
            OutlinedButton(onClick = onNuovoDocumento, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Description, contentDescription = null)
                Text("  Aggiungi documento")
            }
        }
        if (t != "GDPR" && onNuovoSopralluogo != null) {
            item {
                OutlinedButton(onClick = onNuovoSopralluogo, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("  Avvia sopralluogo $t")
                }
            }
        }

        item { Text("Attività e scadenze", style = MaterialTheme.typography.titleLarge, color = BrandNavy) }
        if (attFil.isEmpty()) item { Text("Nessuna attività registrata.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(attFil.take(12), key = { "a${it.id}" }) { a ->
            val scaduta = a.stato != "COMPLETATA" && a.scadenzaEpochMillis?.let { it < now } == true
            Card(colors = CardDefaults.cardColors(containerColor = if (scaduta) Color(0xFFFFF0F0) else MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(a.titolo, style = MaterialTheme.typography.titleMedium)
                    Text(a.scadenzaEpochMillis?.let { "Scadenza ${fmt.format(Date(it))}" } ?: "Senza scadenza", color = if (scaduta) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if (a.stato == "COMPLETATA") "Completata" else a.priorita.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        item { Text("Documenti", style = MaterialTheme.typography.titleLarge, color = BrandNavy) }
        if (docFil.isEmpty()) item { Text("Nessun documento registrato.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(docFil.take(12), key = { "d${it.id}" }) { d ->
            val scaduto = d.scadenzaEpochMillis?.let { it < now } == true
            Card(colors = CardDefaults.cardColors(containerColor = if (scaduto) Color(0xFFFFF0F0) else MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(d.titolo, style = MaterialTheme.typography.titleMedium)
                    Text(d.categoria, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    d.scadenzaEpochMillis?.let { Text("Scadenza ${fmt.format(Date(it))}", color = if (scaduto) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium) }
                }
            }
        }

        if (t != "GDPR") {
            item { Text("Sopralluoghi", style = MaterialTheme.typography.titleLarge, color = BrandNavy) }
            if (soprFil.isEmpty()) item { Text("Nessun sopralluogo registrato.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(soprFil.take(12), key = { "s${it.id}" }) { s ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().clickable { onApriSopralluogo(s.id, s.tipoServizio) }
                ) {
                    Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Sopralluogo ${fmt.format(Date(s.dataOraEpochMillis))}", style = MaterialTheme.typography.titleMedium)
                        Text(if (s.stato == "CHIUSO") "Chiuso" else "In corso", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tocca per aprire", style = MaterialTheme.typography.labelSmall, color = accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(title: String, value: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = accent)
            Text(value.toString(), style = MaterialTheme.typography.headlineSmall, color = accent)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}
