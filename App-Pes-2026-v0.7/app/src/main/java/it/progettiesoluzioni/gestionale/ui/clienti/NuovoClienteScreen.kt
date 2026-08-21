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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.GdprContainer
import it.progettiesoluzioni.gestionale.ui.theme.GdprPurple
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange

@Composable
fun NuovoClienteScreen(
    viewModel: GestionaleViewModel,
    onSaved: () -> Unit
) {
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

    var nomeSede by remember { mutableStateOf("Sede principale") }
    var indirizzo by remember { mutableStateOf("") }
    var civico by remember { mutableStateOf("") }
    var cap by remember { mutableStateOf("") }
    var comune by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nuovo cliente", style = MaterialTheme.typography.headlineMedium, color = BrandNavy)
        Text("Inserisci l'anagrafica e la prima sede operativa", color = MaterialTheme.colorScheme.onSurfaceVariant)

        SectionCard("Anagrafica") {
            Campo("Ragione sociale *", ragioneSociale) { ragioneSociale = it }
            Campo("Nome commerciale", nomeCommerciale) { nomeCommerciale = it }
            Campo("Partita IVA", partitaIva) { partitaIva = it }
            Campo("Codice fiscale", codiceFiscale) { codiceFiscale = it }
            Campo("Codice ATECO", codiceAteco) { codiceAteco = it }
            Campo("Attività esercitata", attivita, singleLine = false) { attivita = it }
        }

        SectionCard("Referente") {
            Campo("Legale rappresentante", legaleRappresentante) { legaleRappresentante = it }
            Campo("Telefono", telefono) { telefono = it }
            Campo("E-mail", email) { email = it }
            Campo("PEC", pec) { pec = it }
        }

        SectionCard("Servizi attivi") {
            ServizioCheck("HACCP", haccp, HaccpGreen, HaccpContainer) { haccp = it }
            ServizioCheck("Sicurezza D.Lgs. 81/08", sicurezza, SafetyOrange, SafetyContainer) { sicurezza = it }
            ServizioCheck("GDPR", gdpr, GdprPurple, GdprContainer) { gdpr = it }
        }

        SectionCard("Sede principale") {
            Text(
                "L'indirizzo sarà utilizzato anche dal comando Naviga.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
            Campo("Nome sede", nomeSede) { nomeSede = it }
            Campo("Indirizzo", indirizzo) { indirizzo = it }
            Campo("Civico", civico) { civico = it }
            Campo("CAP", cap) { cap = it }
            Campo("Comune", comune) { comune = it }
            Campo("Provincia", provincia) { provincia = it }
        }

        SectionCard("Note") {
            Campo("Note", note, singleLine = false) { note = it }
        }

        errore?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp),
            onClick = {
                if (ragioneSociale.isBlank()) {
                    errore = "La ragione sociale è obbligatoria."
                } else {
                    errore = null
                    viewModel.salvaCliente(
                        Cliente(
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
                        ),
                        Sede(
                            clienteId = 0,
                            nome = nomeSede.ifBlank { "Sede principale" }.trim(),
                            indirizzo = indirizzo.trim(),
                            civico = civico.trim(),
                            cap = cap.trim(),
                            comune = comune.trim(),
                            provincia = provincia.trim(),
                            principale = true
                        ),
                        onSaved
                    )
                }
            }
        ) {
            Text("SALVA CLIENTE")
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
            content()
        }
    }
}

@Composable
private fun Campo(label: String, value: String, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
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
private fun ServizioCheck(
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
            Text(
                label,
                modifier = Modifier.padding(top = 12.dp, start = 4.dp),
                color = if (checked) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
