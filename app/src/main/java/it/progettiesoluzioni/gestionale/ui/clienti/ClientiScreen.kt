package it.progettiesoluzioni.gestionale.ui.clienti

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ClientiScreen(
    viewModel: GestionaleViewModel,
    onNuovoCliente: () -> Unit,
    onClienteClick: (Long) -> Unit
) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNuovoCliente) {
                Icon(Icons.Default.Add, contentDescription = "Nuovo cliente")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Clienti", style = MaterialTheme.typography.headlineMedium)
            if (clienti.isEmpty()) {
                Text("Nessun cliente presente. Usa + per inserire il primo cliente.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(clienti, key = { it.id }) { cliente ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onClienteClick(cliente.id) }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(cliente.ragioneSociale, style = MaterialTheme.typography.titleMedium)
                                if (cliente.nomeCommerciale.isNotBlank()) {
                                    Text(cliente.nomeCommerciale)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                                    if (cliente.servizioHaccp) Text("HACCP")
                                    if (cliente.servizioSicurezza) Text("SICUREZZA")
                                    if (cliente.servizioGdpr) Text("GDPR")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
