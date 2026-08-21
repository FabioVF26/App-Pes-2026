package it.progettiesoluzioni.gestionale.ui.clienti

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ClienteDettaglioScreen(clienteId: Long, viewModel: GestionaleViewModel) {
    val cliente by viewModel.cliente(clienteId).collectAsStateWithLifecycle(initialValue = null)
    val sedi by viewModel.sedi(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(cliente?.ragioneSociale ?: "Cliente", style = MaterialTheme.typography.headlineMedium)
            cliente?.nomeCommerciale?.takeIf { it.isNotBlank() }?.let { Text(it) }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Servizi", style = MaterialTheme.typography.titleMedium)
                    cliente?.let {
                        Text("HACCP: ${if (it.servizioHaccp) "Attivo" else "Non attivo"}")
                        Text("Sicurezza: ${if (it.servizioSicurezza) "Attivo" else "Non attivo"}")
                        Text("GDPR: ${if (it.servizioGdpr) "Attivo" else "Non attivo"}")
                    }
                }
            }
        }
        item { Text("Sedi operative", style = MaterialTheme.typography.titleLarge) }
        if (sedi.isEmpty()) {
            item { Text("Nessuna sede registrata.") }
        } else {
            items(sedi, key = { it.id }) { sede ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(sede.nome, style = MaterialTheme.typography.titleMedium)
                        Text(sede.indirizzoCompleto().ifBlank { "Indirizzo non inserito" })
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                enabled = sede.indirizzoCompleto().isNotBlank(),
                                onClick = { navigaVerso(context, sede.indirizzoCompleto()) }
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null)
                                Text(" Naviga")
                            }
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sopralluoghi", style = MaterialTheme.typography.titleMedium)
                    Text("Struttura già predisposta nel database. Le checklist HACCP, Sicurezza e GDPR saranno implementate nella fase successiva con campi specifici e riferimenti normativi verificati.")
                }
            }
        }
    }
}

private fun navigaVerso(context: Context, indirizzo: String) {
    val uri = Uri.parse("geo:0,0?q=${Uri.encode(indirizzo)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(indirizzo)}"))
        context.startActivity(web)
    }
}
