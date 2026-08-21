package it.progettiesoluzioni.gestionale.ui.documenti

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentiScreen(viewModel: GestionaleViewModel, onNuovo: () -> Unit) {
    val documenti by viewModel.documenti.collectAsStateWithLifecycle()
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val clientiMap = clienti.associateBy { it.id }
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALY) }
    val filtered = documenti.filter { d -> query.isBlank() || d.titolo.contains(query, true) || d.categoria.contains(query, true) || (clientiMap[d.clienteId]?.ragioneSociale?.contains(query, true) == true) }

    Scaffold(floatingActionButton = { FloatingActionButton(onClick = onNuovo) { Icon(Icons.Default.Add, contentDescription = "Aggiungi documento") } }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Documenti", style = MaterialTheme.typography.headlineSmall, color = BrandNavy)
            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Cerca documento o cliente") }, singleLine = true)
            if (filtered.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) { Text("Nessun documento registrato.", Modifier.padding(18.dp)) }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { d ->
                        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = BrandNavy)
                                        Column { Text(d.titolo, style = MaterialTheme.typography.titleMedium, color = BrandNavy); Text(clientiMap[d.clienteId]?.ragioneSociale ?: "Cliente non disponibile", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    }
                                    IconButton(onClick = { viewModel.eliminaDocumento(d) }) { Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error) }
                                }
                                Text("${d.servizio} • ${d.categoria}", style = MaterialTheme.typography.labelLarge)
                                d.scadenzaEpochMillis?.let { Text("Scadenza: ${fmt.format(Date(it))}") }
                                if (d.note.isNotBlank()) Text(d.note, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (d.uri.isNotBlank()) {
                                    androidx.compose.material3.OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                                        runCatching {
                                            val intent = Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(d.uri), "*/*"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                                            context.startActivity(intent)
                                        }
                                    }) { Icon(Icons.Default.OpenInNew, contentDescription = null); Text("  Apri documento") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
