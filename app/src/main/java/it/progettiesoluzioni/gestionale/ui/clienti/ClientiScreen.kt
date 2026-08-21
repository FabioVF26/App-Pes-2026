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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange

@Composable
fun ClientiScreen(
    viewModel: GestionaleViewModel,
    onNuovoCliente: () -> Unit,
    onClienteClick: (Long) -> Unit
) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var ricerca by remember { mutableStateOf("") }

    val filtrati = remember(clienti, ricerca) {
        if (ricerca.isBlank()) clienti
        else clienti.filter { cliente ->
            listOf(
                cliente.ragioneSociale,
                cliente.nomeCommerciale,
                cliente.partitaIva,
                cliente.codiceFiscale,
                cliente.legaleRappresentante
            ).any { it.contains(ricerca.trim(), ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuovoCliente,
                containerColor = BrandNavy,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuovo cliente")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Clienti", style = MaterialTheme.typography.headlineMedium, color = BrandNavy)
            Text(
                "Anagrafiche e servizi attivi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = ricerca,
                onValueChange = { ricerca = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Cerca cliente") },
                placeholder = { Text("Ragione sociale, P.IVA, referente...") },
                shape = RoundedCornerShape(16.dp)
            )

            if (filtrati.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        if (ricerca.isBlank()) "Nessun cliente presente. Usa + per inserire il primo cliente."
                        else "Nessun cliente corrisponde alla ricerca.",
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtrati, key = { it.id }) { cliente ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClienteClick(cliente.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(cliente.ragioneSociale, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                                    if (cliente.nomeCommerciale.isNotBlank()) {
                                        Text(cliente.nomeCommerciale, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (cliente.servizioHaccp) ServiceChip("HACCP", HaccpGreen, HaccpContainer)
                                        if (cliente.servizioSicurezza) ServiceChip("Sicurezza", SafetyOrange, SafetyContainer)
                                        if (cliente.servizioGdpr) ServiceChip("GDPR", GdprPurple, GdprContainer)
                                    }
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceChip(label: String, color: Color, container: Color) {
    Surface(color = container, shape = RoundedCornerShape(50)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}
