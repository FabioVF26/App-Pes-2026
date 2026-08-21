package it.progettiesoluzioni.gestionale.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandBlue
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange

@Composable
fun DashboardScreen(viewModel: GestionaleViewModel) {
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    val numeroClienti by viewModel.numeroClienti.collectAsStateWithLifecycle()
    val haccp = clienti.count { it.servizioHaccp }
    val sicurezza = clienti.count { it.servizioSicurezza }
    val gdpr = clienti.count { it.servizioGdpr }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandNavy,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 3.dp
        ) {
            Column(Modifier.padding(22.dp)) {
                Text(
                    "P&S Gestionale",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "HACCP • Sicurezza • GDPR",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.82f)
                )
            }
        }

        Text("Situazione generale", style = MaterialTheme.typography.titleLarge)

        StatCard(
            title = "Clienti attivi",
            value = numeroClienti.toString(),
            icon = Icons.Default.Business,
            accent = BrandBlue,
            container = Color(0xFFE6F0FB)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard("HACCP", haccp.toString(), Icons.Default.Restaurant, HaccpGreen, HaccpContainer, Modifier.weight(1f))
            StatCard("Sicurezza", sicurezza.toString(), Icons.Default.Security, SafetyOrange, SafetyContainer, Modifier.weight(1f))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard("GDPR", gdpr.toString(), Icons.Default.PrivacyTip, GdprPurple, GdprContainer, Modifier.weight(1f))
            StatCard("Sopralluoghi", "0", Icons.Default.Assignment, BrandNavy, Color(0xFFE7EBF6), Modifier.weight(1f))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Attività operative", style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                Text(
                    "Le prossime versioni mostreranno qui scadenze, sopralluoghi programmati e non conformità aperte per ciascuna sede.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accent: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = accent)
            Text(title, style = MaterialTheme.typography.labelLarge, color = accent)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
