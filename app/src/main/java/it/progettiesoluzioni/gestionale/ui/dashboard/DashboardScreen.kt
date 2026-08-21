package it.progettiesoluzioni.gestionale.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel

@Composable
fun DashboardScreen(viewModel: GestionaleViewModel) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    val numeroClienti by viewModel.numeroClienti.collectAsStateWithLifecycle()
    val haccp = clienti.count { it.servizioHaccp }
    val sicurezza = clienti.count { it.servizioSicurezza }
    val gdpr = clienti.count { it.servizioGdpr }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("P&S Gestionale", style = MaterialTheme.typography.headlineMedium)
        Text("Gestione HACCP • Sicurezza • GDPR", style = MaterialTheme.typography.bodyLarge)

        StatCard("Clienti attivi", numeroClienti.toString())
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("HACCP", haccp.toString(), Modifier.weight(1f))
            StatCard("Sicurezza", sicurezza.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("GDPR", gdpr.toString(), Modifier.weight(1f))
            StatCard("Sopralluoghi", "0", Modifier.weight(1f))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Prossimo sviluppo", style = MaterialTheme.typography.titleMedium)
                Text("Scadenze, sopralluoghi e non conformità saranno collegati alla singola sede operativa.")
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
