package it.progettiesoluzioni.gestionale.ui.clienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange

@Composable
fun ModificaClienteScreen(
    clienteId: Long,
    viewModel: GestionaleViewModel,
    onSaved: () -> Unit,
    onArchived: () -> Unit
) {
    val cliente by viewModel.cliente(clienteId).collectAsStateWithLifecycle(initialValue = null)

    var ragioneSociale by remember { mutableStateOf("") }
    var nomeCommerciale by remember { mutableStateOf("") }
    var partitaIva by remember { mutableStateOf("") }
    var codiceFiscale by remember { mutableStateOf("") }
    var codiceAteco by remember { mutableStateOf("") }
    var attivita by remember { mutableStateOf("") }
    var legaleRappresentante by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pec by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var haccp by remember { mutableStateOf(false) }
    var sicurezza by remember { mutableStateOf(false) }
    var gdpr by remember { mutableStateOf(false) }
    var errore by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cliente?.id) {
        cliente?.let {
            ragioneSociale = it.ragioneSociale
            nomeCommerciale = it.nomeCommerciale
            partitaIva = it.partitaIva
            codiceFiscale = it.codiceFiscale
            codiceAteco = it.codiceAteco
            attivita = it.attivita
            legaleRappresentante = it.legaleRappresentante
            telefono = it.telefono
            email = it.email
            pec = it.pec
            note = it.note
            haccp = it.servizioHaccp
            sicurezza = it.servizioSicurezza
            gdpr = it.servizioGdpr
        }
    }

    val current = cliente ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Modifica cliente", style = MaterialTheme.typography.headlineMedium, color = BrandNavy)

        EditSection("Anagrafica") {
            EditCampo("Ragione sociale *", ragioneSociale) { ragioneSociale = it }
            EditCampo("Nome commerciale", nomeCommerciale) { nomeCommerciale = it }
            EditCampo("Partita IVA", partitaIva) { partitaIva = it }
            EditCampo("Codice fiscale", codiceFiscale) { codiceFiscale = it }
            EditCampo("Codice ATECO", codiceAteco) { codiceAteco = it }
            EditCampo("Attività esercitata", attivita, false) { attivita = it }
        }

        EditSection("Referente") {
            EditCampo("Legale rappresentante", legaleRappresentante) { legaleRappresentante = it }
            EditCampo("Telefono", telefono) { telefono = it }
            EditCampo("E-mail", email) { email = it }
            EditCampo("PEC", pec) { pec = it }
        }

        EditSection("Servizi attivi") {
            EditServizio("HACCP", haccp, HaccpGreen, HaccpContainer) { haccp = it }
            EditServizio("Sicurezza D.Lgs. 81/08", sicurezza, SafetyOrange, SafetyContainer) { sicurezza = it }
            EditServizio("GDPR", gdpr, GdprPurple, GdprContainer) { gdpr = it }
        }

        EditSection("Note") {
            EditCampo("Note", note, false) { note = it }
        }

        errore?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (ragioneSociale.isBlank()) {
                    errore = "La ragione sociale è obbligatoria."
                } else {
                    val aggiornato = current.copy(
                        ragioneSociale = ragioneSociale.trim(),
                        nomeCommerciale = nomeCommerciale.trim(),
                        partitaIva = partitaIva.trim(),
                        codiceFiscale = codiceFiscale.trim(),
                        codiceAteco = codiceAteco.trim(),
                        attivita = attivita.trim(),
                        legaleRappresentante = legaleRappresentante.trim(),
                        telefono = telefono.trim(),
                        email = email.trim(),
                        pec = pec.trim(),
                        note = note.trim(),
                        servizioHaccp = haccp,
                        servizioSicurezza = sicurezza,
                        servizioGdpr = gdpr
                    )
                    viewModel.aggiornaCliente(aggiornato, onSaved)
                }
            }
        ) { Text("SALVA MODIFICHE") }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            onClick = { viewModel.archiviaCliente(current, onArchived) }
        ) {
            Text("ARCHIVIA CLIENTE", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun EditSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
            content()
        }
    }
}

@Composable
private fun EditCampo(label: String, value: String, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun EditServizio(
    label: String,
    checked: Boolean,
    color: Color,
    container: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (checked) container else MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, modifier = Modifier.padding(top = 12.dp, start = 4.dp), color = if (checked) color else MaterialTheme.colorScheme.onSurfaceVariant)
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
