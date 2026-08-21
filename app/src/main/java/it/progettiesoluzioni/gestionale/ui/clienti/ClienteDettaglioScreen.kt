package it.progettiesoluzioni.gestionale.ui.clienti

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.platform.LocalContext
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
fun ClienteDettaglioScreen(
    clienteId: Long,
    viewModel: GestionaleViewModel,
    onModificaCliente: () -> Unit,
    onAggiungiSede: () -> Unit,
    onNuovoSopralluogoHaccp: () -> Unit,
    onNuovoSopralluogoSicurezza: () -> Unit
) {
    val cliente by viewModel.cliente(clienteId).collectAsStateWithLifecycle(initialValue = null)
    val sedi by viewModel.sedi(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BrandNavy,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        cliente?.ragioneSociale ?: "Cliente",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    cliente?.nomeCommerciale?.takeIf { it.isNotBlank() }?.let {
                        Spacer(Modifier.height(3.dp))
                        Text(it, color = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = onModificaCliente) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        Text("  Modifica cliente", color = Color.White)
                    }
                }
            }
        }

        item {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Servizi attivi", style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                    cliente?.let {
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            if (it.servizioHaccp) ServiceChip("HACCP", HaccpGreen, HaccpContainer)
                            if (it.servizioSicurezza) ServiceChip("Sicurezza", SafetyOrange, SafetyContainer)
                            if (it.servizioGdpr) ServiceChip("GDPR", GdprPurple, GdprContainer)
                        }
                        if (!it.servizioHaccp && !it.servizioSicurezza && !it.servizioGdpr) {
                            Text("Nessun servizio attivo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sedi operative", style = MaterialTheme.typography.titleLarge, color = BrandNavy)
                OutlinedButton(onClick = onAggiungiSede) {
                    Icon(Icons.Default.AddLocationAlt, contentDescription = null)
                    Text("  Aggiungi")
                }
            }
        }

        if (sedi.isEmpty()) {
            item { Text("Nessuna sede registrata.") }
        } else {
            items(sedi, key = { it.id }) { sede ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Column {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(sede.nome, style = MaterialTheme.typography.titleMedium)
                                    if (sede.principale) {
                                        ServiceChip("Principale", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer)
                                    }
                                }
                                Text(
                                    sede.indirizzoCompleto().ifBlank { "Indirizzo non inserito" },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = sede.indirizzoCompleto().isNotBlank(),
                            onClick = { navigaVerso(context, sede.indirizzoCompleto()) }
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null)
                            Text("  Naviga verso la sede")
                        }
                    }
                }
            }
        }

        item {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EDF7)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = BrandNavy)
                    Text("Sopralluoghi", style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                    Text(
                        "Avvia un sopralluogo HACCP o Sicurezza collegato al cliente e alla sede, con checklist, note, non conformità e relazione PDF.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (cliente?.servizioHaccp == true) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNuovoSopralluogoHaccp
                        ) {
                            Text("Avvia sopralluogo HACCP")
                        }
                    }
                    if (cliente?.servizioSicurezza == true) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNuovoSopralluogoSicurezza
                        ) {
                            Text("Avvia sopralluogo Sicurezza")
                        }
                    }
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
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(indirizzo)}")
        )
        context.startActivity(web)
    }
}
