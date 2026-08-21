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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClienteDettaglioScreen(
    clienteId: Long,
    viewModel: GestionaleViewModel,
    onModificaCliente: () -> Unit,
    onAggiungiSede: () -> Unit,
    onNuovoSopralluogoHaccp: () -> Unit,
    onNuovoSopralluogoSicurezza: () -> Unit,
    onApriSopralluogo: (Long, String) -> Unit,
    onApriServizio: (String) -> Unit
) {
    val cliente by viewModel.cliente(clienteId).collectAsStateWithLifecycle(initialValue = null)
    val sedi by viewModel.sedi(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val sopralluoghi by viewModel.sopralluoghiCliente(clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY) }
    var sopralluogoDaEliminare by remember { mutableStateOf<Sopralluogo?>(null) }
    val sediMap = sedi.associateBy { it.id }

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
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        cliente?.ragioneSociale ?: "Cliente",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    cliente?.nomeCommerciale?.takeIf { it.isNotBlank() }?.let {
                        Text(it, color = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(onClick = onModificaCliente) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        Text("  Modifica cliente", color = Color.White)
                    }
                    cliente?.let { c ->
                        if (c.servizioHaccp) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onNuovoSopralluogoHaccp,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandNavy)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null)
                                Text("  Avvia sopralluogo HACCP")
                            }
                        }
                        if (c.servizioSicurezza) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onNuovoSopralluogoSicurezza,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandNavy)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null)
                                Text("  Avvia sopralluogo Sicurezza")
                            }
                        }
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
                        if (it.servizioHaccp) OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onApriServizio("HACCP") }) { Text("Apri scheda HACCP") }
                        if (it.servizioSicurezza) OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onApriServizio("SICUREZZA") }) { Text("Apri scheda Sicurezza") }
                        if (it.servizioGdpr) OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { onApriServizio("GDPR") }) { Text("Apri scheda GDPR") }
                        if (!it.servizioHaccp && !it.servizioSicurezza && !it.servizioGdpr) {
                            Text("Nessun servizio attivo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sopralluoghi effettuati", style = MaterialTheme.typography.titleLarge, color = BrandNavy)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "${sopralluoghi.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = BrandNavy
                    )
                }
            }
        }

        if (sopralluoghi.isEmpty()) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        "Nessun sopralluogo registrato per questo cliente.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(sopralluoghi, key = { "sopralluogo-${it.id}" }) { sopralluogo ->
                val isSafety = sopralluogo.tipoServizio == "SICUREZZA"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                ServiceChip(
                                    if (isSafety) "Sicurezza" else "HACCP",
                                    if (isSafety) SafetyOrange else HaccpGreen,
                                    if (isSafety) SafetyContainer else HaccpContainer
                                )
                                Surface(
                                    color = if (sopralluogo.stato == "CHIUSO") Color(0xFFE4F4E8) else Color(0xFFFFF1D8),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        if (sopralluogo.stato == "CHIUSO") "Chiuso" else "In corso",
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        color = if (sopralluogo.stato == "CHIUSO") Color(0xFF26713A) else Color(0xFF9A5B00)
                                    )
                                }
                            }
                            IconButton(onClick = { sopralluogoDaEliminare = sopralluogo }) {
                                Icon(Icons.Default.Delete, contentDescription = "Elimina sopralluogo", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        Text(
                            formatter.format(Date(sopralluogo.dataOraEpochMillis)),
                            style = MaterialTheme.typography.titleMedium,
                            color = BrandNavy
                        )
                        Text(
                            sediMap[sopralluogo.sedeId]?.let { "${it.nome} - ${it.indirizzoCompleto()}" }
                                ?: "Sede non disponibile",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onApriSopralluogo(sopralluogo.id, sopralluogo.tipoServizio) }
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null)
                            Text("  Apri sopralluogo")
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
            items(sedi, key = { "sede-${it.id}" }) { sede ->
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
    }

    sopralluogoDaEliminare?.let { sopralluogo ->
        AlertDialog(
            onDismissRequest = { sopralluogoDaEliminare = null },
            title = { Text("Eliminare il sopralluogo?") },
            text = {
                Text(
                    "Il sopralluogo del ${formatter.format(Date(sopralluogo.dataOraEpochMillis))} e tutte le verifiche, non conformità e fotografie collegate verranno eliminati definitivamente."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminaSopralluogo(sopralluogo.id)
                        sopralluogoDaEliminare = null
                    }
                ) { Text("Elimina", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { sopralluogoDaEliminare = null }) { Text("Annulla") }
            }
        )
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
