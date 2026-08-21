package it.progettiesoluzioni.gestionale.ui.scadenze

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScadenzeScreen(viewModel: GestionaleViewModel, onNuova: () -> Unit) {
    val items by viewModel.attivitaScadenze.collectAsStateWithLifecycle()
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var filtro by remember { mutableStateOf("APERTE") }
    val clientiMap = clienti.associateBy { it.id }
    val now = System.currentTimeMillis()
    val filtered = items.filter {
        when (filtro) {
            "APERTE" -> it.stato != "COMPLETATA"
            "SCADUTE" -> it.stato != "COMPLETATA" && (it.scadenzaEpochMillis ?: Long.MAX_VALUE) < now
            "30_GG" -> it.stato != "COMPLETATA" && it.scadenzaEpochMillis?.let { d -> d >= now && d <= now + 30L * 24 * 60 * 60 * 1000 } == true
            "COMPLETATE" -> it.stato == "COMPLETATA"
            else -> true
        }
    }
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALY) }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onNuova) { Icon(Icons.Default.Add, contentDescription = "Nuova attività") } }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Scadenze e attività", style = MaterialTheme.typography.headlineSmall, color = BrandNavy)
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(selected = filtro == "APERTE", onClick = { filtro = "APERTE" }, label = { Text("Aperte") })
                FilterChip(selected = filtro == "SCADUTE", onClick = { filtro = "SCADUTE" }, label = { Text("Scadute") })
                FilterChip(selected = filtro == "30_GG", onClick = { filtro = "30_GG" }, label = { Text("30 gg") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                FilterChip(selected = filtro == "COMPLETATE", onClick = { filtro = "COMPLETATE" }, label = { Text("Completate") })
                FilterChip(selected = filtro == "TUTTE", onClick = { filtro = "TUTTE" }, label = { Text("Tutte") })
            }
            if (filtered.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                    Text("Nessuna attività nel filtro selezionato.", Modifier.padding(18.dp))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { item ->
                        val due = item.scadenzaEpochMillis
                        val overdue = item.stato != "COMPLETATA" && due != null && due < now
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (overdue) Color(0xFFFFECEC) else MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.weight(1f)) {
                                        Text(item.titolo, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                                        Text(clientiMap[item.clienteId]?.ragioneSociale ?: "Cliente non disponibile", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.eliminaAttivita(item) }) { Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error) }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                    Tag(item.servizio)
                                    Tag(item.priorita)
                                }
                                if (item.descrizione.isNotBlank()) Text(item.descrizione)
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = if (overdue) MaterialTheme.colorScheme.error else BrandNavy)
                                    Spacer(Modifier.width(6.dp))
                                    Text(due?.let { fmt.format(Date(it)) } ?: "Nessuna scadenza", color = if (overdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                                }
                                if (item.stato != "COMPLETATA") {
                                    Button(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.aggiornaAttivita(item.copy(stato = "COMPLETATA", completataEpochMillis = System.currentTimeMillis())) }) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null); Text("  Segna completata")
                                    }
                                } else {
                                    Text("Completata", color = Color(0xFF26713A), style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun Tag(text: String) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(14.dp)) {
        Text(text.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, Modifier.padding(horizontal = 9.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = BrandNavy)
    }
}
