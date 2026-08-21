package it.progettiesoluzioni.gestionale.ui.documenti

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.DocumentoCliente
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NuovoDocumentoScreen(viewModel: GestionaleViewModel, onSaved: () -> Unit) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf<Cliente?>(null) }
    var titolo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Altro") }
    var servizio by remember { mutableStateOf("GENERALE") }
    var note by remember { mutableStateOf("") }
    var uri by remember { mutableStateOf<Uri?>(null) }
    var scadenza by remember { mutableStateOf<Long?>(null) }
    var sedeId by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.ITALY) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { selected ->
        if (selected != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(selected, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            uri = selected
            if (titolo.isBlank()) titolo = selected.lastPathSegment?.substringAfterLast('/') ?: "Documento"
        }
    }
    val risultati = if (query.length < 2 || cliente != null) emptyList() else clienti.filter { it.ragioneSociale.contains(query, true) }.take(10)
    val sedi = if (cliente != null) viewModel.sedi(cliente!!.id).collectAsStateWithLifecycle(initialValue = emptyList()).value else emptyList()

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Aggiungi documento", style = MaterialTheme.typography.headlineSmall, color = BrandNavy) }
        item { OutlinedTextField(cliente?.ragioneSociale ?: query, { query = it; cliente = null }, label = { Text("Cliente *") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
        items(risultati, key = { it.id }) { c -> Text(c.ragioneSociale, Modifier.fillMaxWidth().clickable { cliente = c; query = c.ragioneSociale; sedeId = null }.padding(12.dp)) }
        if (sedi.isNotEmpty()) {
            item {
                Text("Sede (facoltativa)", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = sedeId == null, onClick = { sedeId = null }, label = { Text("Tutte / generale") })
                    sedi.forEach { sede -> FilterChip(selected = sedeId == sede.id, onClick = { sedeId = sede.id }, label = { Text(sede.nome + if (sede.indirizzoCompleto().isNotBlank()) " - ${sede.indirizzoCompleto()}" else "") }) }
                }
            }
        }
        item { OutlinedTextField(titolo, { titolo = it }, label = { Text("Titolo *") }, modifier = Modifier.fillMaxWidth()) }
        item {
            Text("Servizio", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("GENERALE","HACCP","SICUREZZA","GDPR").forEach { s -> FilterChip(selected = servizio == s, onClick = { servizio = s }, label = { Text(s.lowercase().replaceFirstChar { it.uppercase() }) }) } }
        }
        item { OutlinedTextField(categoria, { categoria = it }, label = { Text("Categoria (es. DVR, Piano HACCP, Informativa)") }, modifier = Modifier.fillMaxWidth()) }
        item { Button(modifier = Modifier.fillMaxWidth(), onClick = { picker.launch(arrayOf("application/pdf","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","image/*","text/*")) }) { Text(uri?.let { "File selezionato ✓" } ?: "Seleziona file") } }
        item { Button(modifier = Modifier.fillMaxWidth(), onClick = {
            val cal = Calendar.getInstance(); scadenza?.let { cal.timeInMillis = it }
            DatePickerDialog(context, { _, y, m, d -> cal.set(y,m,d,12,0,0); cal.set(Calendar.MILLISECOND,0); scadenza = cal.timeInMillis }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }) { Text(scadenza?.let { "Scadenza: ${fmt.format(Date(it))}" } ?: "Scadenza documento (facoltativa)") } }
        item { OutlinedTextField(note, { note = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth(), minLines = 2) }
        item { Button(enabled = cliente != null && titolo.isNotBlank(), modifier = Modifier.fillMaxWidth(), onClick = { viewModel.salvaDocumento(DocumentoCliente(clienteId = cliente!!.id, sedeId = sedeId, servizio = servizio, categoria = categoria.ifBlank { "Altro" }, titolo = titolo.trim(), uri = uri?.toString() ?: "", scadenzaEpochMillis = scadenza, note = note.trim()), onSaved) }) { Text("Salva documento") } }
        item { Text("Nota: l'archivio registra il collegamento al file scelto sul dispositivo; il backup dati non incorpora automaticamente il file originale.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}
