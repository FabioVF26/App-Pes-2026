package it.progettiesoluzioni.gestionale.ui.sopralluoghi

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SopralluoghiScreen(
    viewModel: GestionaleViewModel,
    onNuovoHaccp: () -> Unit,
    onNuovoSicurezza: () -> Unit,
    onApri: (Long, String) -> Unit
) {
    val sopralluoghi by viewModel.sopralluoghi.collectAsStateWithLifecycle()
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    val clientiMap = clienti.associateBy { it.id }
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = BrandNavy,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.White)
                        Text("Sopralluoghi", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    }
                    Text(
                        "Avvia e gestisci le verifiche operative presso le sedi dei clienti.",
                        color = Color.White.copy(alpha = 0.82f)
                    )
                    Button(onClick = onNuovoHaccp) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("  Nuovo sopralluogo HACCP")
                    }
                    Button(onClick = onNuovoSicurezza) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("  Nuovo sopralluogo Sicurezza")
                    }
                }
            }
        }

        if (sopralluoghi.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        "Non sono ancora presenti sopralluoghi.",
                        modifier = Modifier.padding(18.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(sopralluoghi, key = { it.id }) { sopralluogo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onApri(sopralluogo.id, sopralluogo.tipoServizio) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val isSafety = sopralluogo.tipoServizio == "SICUREZZA"
                            Surface(color = if (isSafety) SafetyContainer else HaccpContainer, shape = RoundedCornerShape(20.dp)) {
                                Text(
                                    if (isSafety) "SICUREZZA" else sopralluogo.tipoServizio,
                                    color = if (isSafety) SafetyOrange else HaccpGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                color = if (sopralluogo.stato == "CHIUSO") Color(0xFFE4F4E8) else Color(0xFFFFF1D8),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    if (sopralluogo.stato == "CHIUSO") "Chiuso" else "In corso",
                                    color = if (sopralluogo.stato == "CHIUSO") Color(0xFF26713A) else Color(0xFF9A5B00),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Text(
                            clientiMap[sopralluogo.clienteId]?.ragioneSociale ?: "Cliente #${sopralluogo.clienteId}",
                            style = MaterialTheme.typography.titleMedium,
                            color = BrandNavy
                        )
                        Text(formatter.format(Date(sopralluogo.dataOraEpochMillis)), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
