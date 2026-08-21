package it.progettiesoluzioni.gestionale.ui.scadenze

import android.app.DatePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import it.progettiesoluzioni.gestionale.data.model.AttivitaScadenza
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NuovaAttivitaScreen(viewModel: GestionaleViewModel, onSaved: () -> Unit) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var titolo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var servizio by remember { mutableStateOf("GENERALE") }
    var priorita by remember { mutableStateOf("MEDIA") }
    var scadenza by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALY) }
    val risultati = if (query.length < 2 || cliente != null) emptyList() else clienti.filter { it.ragioneSociale.contains(query, true) }.take(10)

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Nuova attività / scadenza", style = MaterialTheme.typography.headlineSmall, color = BrandNavy) }
        item {
            OutlinedTextField(value = cliente?.ragioneSociale ?: query, onValueChange = { query = it; cliente = null }, label = { Text("Cliente *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        items(risultati, key = { it.id }) { c ->
            Text(c.ragioneSociale, Modifier.fillMaxWidth().clickable { cliente = c; query = c.ragioneSociale }.padding(12.dp))
        }
        item { OutlinedTextField(titolo, { titolo = it }, label = { Text("Titolo *") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(descrizione, { descrizione = it }, label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
        item {
            Text("Servizio", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("GENERALE","HACCP","SICUREZZA","GDPR").forEach { s -> FilterChip(selected = servizio == s, onClick = { servizio = s }, label = { Text(s.lowercase().replaceFirstChar { it.uppercase() }) }) }
            }
        }
        item {
            Text("Priorità", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("BASSA","MEDIA","ALTA").forEach { p -> FilterChip(selected = priorita == p, onClick = { priorita = p }, label = { Text(p.lowercase().replaceFirstChar { it.uppercase() }) }) } }
        }
        item {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                val cal = Calendar.getInstance(); scadenza?.let { cal.timeInMillis = it }
                DatePickerDialog(context, { _, y, m, d -> cal.set(y,m,d,12,0,0); cal.set(Calendar.MILLISECOND,0); scadenza = cal.timeInMillis }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }) { Text(scadenza?.let { "Scadenza: ${fmt.format(Date(it))}" } ?: "Imposta scadenza") }
        }
        item {
            Button(enabled = cliente != null && titolo.isNotBlank(), modifier = Modifier.fillMaxWidth(), onClick = {
                viewModel.salvaAttivita(AttivitaScadenza(clienteId = cliente!!.id, servizio = servizio, titolo = titolo.trim(), descrizione = descrizione.trim(), priorita = priorita, scadenzaEpochMillis = scadenza), onSaved)
            }) { Text("Salva attività") }
        }
    }
}
