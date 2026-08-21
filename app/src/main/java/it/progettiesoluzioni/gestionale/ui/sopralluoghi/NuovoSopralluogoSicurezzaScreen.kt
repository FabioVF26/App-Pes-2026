package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
    val clientiServizio = remember(clienti) { clienti.filter { it.servizioSicurezza } }
    var clienteId by rememberSaveable { mutableLongStateOf(clientePreselezionato ?: 0L) }
    var ricerca by rememberSaveable { mutableStateOf("") }
    var mostraRicerca by rememberSaveable { mutableStateOf(clientePreselezionato == null) }
    val clienteSelezionato = clienti.firstOrNull { it.id == clienteId }
    val sedi by viewModel.sedi(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    var sedeId by rememberSaveable { mutableLongStateOf(0L) }
    var note by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val risultati = remember(clientiServizio, ricerca) {
        val q = ricerca.trim()
        if (q.length < 2) emptyList()
        else clientiServizio.filter { c ->
            c.ragioneSociale.contains(q, ignoreCase = true) ||
                c.nomeCommerciale.contains(q, ignoreCase = true) ||
                c.legaleRappresentante.contains(q, ignoreCase = true)
        }.take(20)
    }

    LaunchedEffect(clienteId, sedi) {
        if (sedi.none { it.id == sedeId }) {
            sedeId = sedi.firstOrNull { it.principale }?.id ?: sedi.firstOrNull()?.id ?: 0L
        }
    }

    LaunchedEffect(clienteId) {
        if (clienteId > 0) {
            mostraRicerca = false
            ricerca = ""
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Nuovo sopralluogo Sicurezza", style = MaterialTheme.typography.headlineSmall, color = BrandNavy)
            Text(
                if (clienteId > 0) "Cliente selezionato. Scegli la sede e avvia subito il sopralluogo."
                else "Cerca il cliente per ragione sociale e selezionalo.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (clienteSelezionato != null && !mostraRicerca) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Cliente selezionato", style = MaterialTheme.typography.labelLarge, color = BrandNavy)
                        Text(clienteSelezionato.ragioneSociale, style = MaterialTheme.typography.titleMedium)
                        if (clienteSelezionato.nomeCommerciale.isNotBlank()) {
                            Text(clienteSelezionato.nomeCommerciale, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                mostraRicerca = true
                                clienteId = 0L
                                sedeId = 0L
                            }
                        ) { Text("Cambia cliente") }
                    }
                }
            }
        } else {
            item {
                OutlinedTextField(
                    value = ricerca,
                    onValueChange = { ricerca = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cerca cliente") },
                    placeholder = { Text("Digita almeno 2 caratteri") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
            }
            if (ricerca.trim().length >= 2) {
                if (risultati.isEmpty()) {
                    item { Text("Nessun cliente Sicurezza trovato.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    risultati.forEach { cliente ->
                        item(key = "cliente-${cliente.id}") {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { clienteId = cliente.id },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    RadioButton(selected = false, onClick = { clienteId = cliente.id })
                                    Column {
                                        Text(cliente.ragioneSociale, style = MaterialTheme.typography.titleSmall)
                                        if (cliente.nomeCommerciale.isNotBlank()) {
                                            Text(cliente.nomeCommerciale, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (clienteId > 0) {
            item { Text("Sede operativa", style = MaterialTheme.typography.titleMedium, color = BrandNavy) }
            if (sedi.isEmpty()) {
                item { Text("Il cliente non ha sedi disponibili.", color = MaterialTheme.colorScheme.error) }
            } else {
                sedi.forEach { sede ->
                    item(key = "sede-${sede.id}") {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { sedeId = sede.id },
                            colors = CardDefaults.cardColors(
                                containerColor = if (sedeId == sede.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
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
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sedeId > 0,
                    onClick = { viewModel.creaSopralluogoSicurezza(clienteId, sedeId, note, onCreated) }
                ) {
                    Text("Avvia sopralluogo Sicurezza")
                }
            }

            item {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note iniziali (facoltative)") },
                    minLines = 2
                )
            }
        }
    }
}
