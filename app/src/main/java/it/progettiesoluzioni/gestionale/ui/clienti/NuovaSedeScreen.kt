package it.progettiesoluzioni.gestionale.ui.clienti

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy

@Composable
fun NuovaSedeScreen(
    clienteId: Long,
    viewModel: GestionaleViewModel,
    onSaved: () -> Unit
) {
    var nome by remember { mutableStateOf("Sede operativa") }
    var indirizzo by remember { mutableStateOf("") }
    var civico by remember { mutableStateOf("") }
    var cap by remember { mutableStateOf("") }
    var comune by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nuova sede", style = MaterialTheme.typography.headlineMedium, color = BrandNavy)
        Text(
            "Aggiungi un'unità operativa. L'indirizzo sarà utilizzato anche dalla funzione Naviga.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SedeCampo("Nome sede *", nome) { nome = it }
                SedeCampo("Indirizzo", indirizzo) { indirizzo = it }
                SedeCampo("Civico", civico) { civico = it }
                SedeCampo("CAP", cap) { cap = it }
                SedeCampo("Comune", comune) { comune = it }
                SedeCampo("Provincia", provincia) { provincia = it }
                SedeCampo("Note", note, singleLine = false) { note = it }
            }
        }

        errore?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (nome.isBlank()) {
                    errore = "Il nome della sede è obbligatorio."
                } else {
                    viewModel.aggiungiSede(
                        Sede(
                            clienteId = clienteId,
                            nome = nome.trim(),
                            indirizzo = indirizzo.trim(),
                            civico = civico.trim(),
                            cap = cap.trim(),
                            comune = comune.trim(),
                            provincia = provincia.trim(),
                            note = note.trim(),
                            principale = false
                        ),
                        onSaved
                    )
                }
            }
        ) {
            Text("SALVA SEDE")
        }
    }
}

@Composable
private fun SedeCampo(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
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
