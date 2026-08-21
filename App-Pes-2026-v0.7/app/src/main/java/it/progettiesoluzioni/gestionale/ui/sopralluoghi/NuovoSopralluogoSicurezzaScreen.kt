package it.progettiesoluzioni.gestionale.ui.sopralluoghi

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy

@Composable
fun NuovoSopralluogoSicurezzaScreen(
    viewModel: GestionaleViewModel,
    clientePreselezionato: Long? = null,
    onCreated: (Long) -> Unit
) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var clienteId by remember { mutableLongStateOf(clientePreselezionato ?: 0L) }
    val sedi by viewModel.sedi(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    var sedeId by remember { mutableLongStateOf(0L) }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(clienteId, sedi) {
        if (sedi.none { it.id == sedeId }) sedeId = sedi.firstOrNull { it.principale }?.id ?: sedi.firstOrNull()?.id ?: 0L
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Nuovo sopralluogo Sicurezza", style = MaterialTheme.typography.headlineSmall, color = BrandNavy)
            Text(
                "Seleziona cliente e sede. La checklist viene creata automaticamente e potrà essere compilata anche in più momenti.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { Text("Cliente", style = MaterialTheme.typography.titleMedium, color = BrandNavy) }
        items(clienti, key = { "cliente-${it.id}" }) { cliente ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { clienteId = cliente.id },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RadioButton(selected = clienteId == cliente.id, onClick = { clienteId = cliente.id })
                    Column {
                        Text(cliente.ragioneSociale, style = MaterialTheme.typography.titleSmall)
                        if (cliente.nomeCommerciale.isNotBlank()) Text(cliente.nomeCommerciale, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (clienteId > 0) {
            item { Text("Sede operativa", style = MaterialTheme.typography.titleMedium, color = BrandNavy) }
            if (sedi.isEmpty()) {
                item { Text("Il cliente non ha sedi disponibili.", color = MaterialTheme.colorScheme.error) }
            } else {
                items(sedi, key = { "sede-${it.id}" }) { sede ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { sedeId = sede.id },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RadioButton(selected = sedeId == sede.id, onClick = { sedeId = sede.id })
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Column {
                                Text(sede.nome, style = MaterialTheme.typography.titleSmall)
                                Text(sede.indirizzoCompleto(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note iniziali") },
                minLines = 3
            )
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = clienteId > 0 && sedeId > 0,
                onClick = { viewModel.creaSopralluogoSicurezza(clienteId, sedeId, note, onCreated) }
            ) {
                Text("Avvia sopralluogo Sicurezza")
            }
        }
    }
}
