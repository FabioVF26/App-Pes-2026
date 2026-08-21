package it.progettiesoluzioni.gestionale.ui.clienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.Sede

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Nuovo cliente", style = MaterialTheme.typography.headlineMedium)
        Text("Anagrafica", style = MaterialTheme.typography.titleMedium)
        Campo("Ragione sociale *", ragioneSociale) { ragioneSociale = it }
        Campo("Nome commerciale", nomeCommerciale) { nomeCommerciale = it }
        Campo("Partita IVA", partitaIva) { partitaIva = it }
        Campo("Codice fiscale", codiceFiscale) { codiceFiscale = it }
        Campo("Codice ATECO", codiceAteco) { codiceAteco = it }
        Campo("Attività esercitata", attivita, singleLine = false) { attivita = it }

        HorizontalDivider(Modifier.padding(vertical = 6.dp))
        Text("Referente", style = MaterialTheme.typography.titleMedium)
        Campo("Legale rappresentante", legaleRappresentante) { legaleRappresentante = it }
        Campo("Telefono", telefono) { telefono = it }
        Campo("E-mail", email) { email = it }
        Campo("PEC", pec) { pec = it }

        HorizontalDivider(Modifier.padding(vertical = 6.dp))
        Text("Servizi attivi", style = MaterialTheme.typography.titleMedium)
        ServizioCheck("HACCP", haccp) { haccp = it }
        ServizioCheck("Sicurezza D.Lgs. 81/08", sicurezza) { sicurezza = it }
        ServizioCheck("GDPR", gdpr) { gdpr = it }

        HorizontalDivider(Modifier.padding(vertical = 6.dp))
        Text("Sede principale", style = MaterialTheme.typography.titleMedium)
        Text("L'indirizzo della sede sarà utilizzato anche dal comando Naviga.")
        Campo("Nome sede", nomeSede) { nomeSede = it }
        Campo("Indirizzo", indirizzo) { indirizzo = it }
        Campo("Civico", civico) { civico = it }
        Campo("CAP", cap) { cap = it }
        Campo("Comune", comune) { comune = it }
        Campo("Provincia", provincia) { provincia = it }

        HorizontalDivider(Modifier.padding(vertical = 6.dp))
        Campo("Note", note, singleLine = false) { note = it }

        errore?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
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
private fun Campo(label: String, value: String, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ServizioCheck(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, modifier = Modifier.padding(top = 12.dp))
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}
