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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.HaccpContainer
import it.progettiesoluzioni.gestionale.ui.theme.HaccpGreen

@Composable
fun SopralluogoHaccpScreen(
    sopralluogoId: Long,
    viewModel: GestionaleViewModel,
    onBack: () -> Unit
) {
    val sopralluogo by viewModel.sopralluogo(sopralluogoId).collectAsStateWithLifecycle(initialValue = null)
    val verifiche by viewModel.verifiche(sopralluogoId).collectAsStateWithLifecycle(initialValue = emptyList())
    val nonConformita by viewModel.nonConformita(sopralluogoId).collectAsStateWithLifecycle(initialValue = emptyList())
    var messaggio by remember { mutableStateOf<String?>(null) }

    val ncMap = nonConformita.associateBy { it.verificaId }
    val complete = verifiche.count { it.esito != "DA_VERIFICARE" }
    val ncCount = verifiche.count { it.esito == "NON_CONFORME" }

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
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sopralluogo HACCP", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text("Verifiche completate: $complete / ${verifiche.size}", color = Color.White.copy(alpha = 0.86f))
                    Text("Non conformità rilevate: $ncCount", color = Color.White.copy(alpha = 0.86f))
                    Surface(color = HaccpContainer, shape = RoundedCornerShape(18.dp)) {
                        Text(
                            if (sopralluogo?.stato == "CHIUSO") "CHIUSO" else "IN CORSO",
                            color = HaccpGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        verifiche.groupBy { it.sezione }.forEach { (sezione, gruppo) ->
            item(key = "sec-$sezione") {
                Text(sezione, style = MaterialTheme.typography.titleLarge, color = BrandNavy)
            }
            items(gruppo, key = { it.id }) { verifica ->
                VerificaCard(
                    verifica = verifica,
                    nonConformita = ncMap[verifica.id],
                    readOnly = sopralluogo?.stato == "CHIUSO",
                    onAggiornaVerifica = viewModel::aggiornaVerifica,
                    onSalvaNc = viewModel::salvaNonConformita,
                    onRimuoviNc = viewModel::rimuoviNonConformita
                )
            }
        }

        if (sopralluogo?.stato != "CHIUSO") {
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val corrente = sopralluogo ?: return@Button
                        viewModel.chiudiSopralluogo(corrente) { ok ->
                            messaggio = if (ok) "Sopralluogo chiuso correttamente." else "Prima della chiusura assegna un esito a tutte le verifiche."
                        }
                    }
                ) { Text("Chiudi sopralluogo") }
            }
        }

        item {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onBack) {
                Text("Torna all'elenco")
            }
        }
    }

    messaggio?.let { testo ->
        AlertDialog(
            onDismissRequest = { messaggio = null },
            confirmButton = { TextButton(onClick = { messaggio = null }) { Text("OK") } },
            title = { Text("Sopralluogo") },
            text = { Text(testo) }
        )
    }
}

@Composable
private fun VerificaCard(
    verifica: VerificaSopralluogo,
    nonConformita: NonConformita?,
    readOnly: Boolean,
    onAggiornaVerifica: (VerificaSopralluogo) -> Unit,
    onSalvaNc: (NonConformita) -> Unit,
    onRimuoviNc: (Long) -> Unit
) {
    var espansa by rememberSaveable(verifica.id) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${verifica.codice} · ${verifica.titolo}", style = MaterialTheme.typography.titleMedium)
            if (verifica.riferimentoNormativo.isNotBlank()) {
                Text(verifica.riferimentoNormativo, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EsitoChip("Conforme", "CONFORME", verifica, readOnly, onAggiornaVerifica)
                EsitoChip("NC", "NON_CONFORME", verifica, readOnly, onAggiornaVerifica)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EsitoChip("N.A.", "NON_APPLICABILE", verifica, readOnly, onAggiornaVerifica)
                EsitoChip("Da verificare", "DA_VERIFICARE", verifica, readOnly, onAggiornaVerifica)
            }

            if (!readOnly) {
                OutlinedButton(onClick = { espansa = !espansa }) {
                    Text(if (espansa) "Nascondi note" else "Note e dettagli")
                }
            }

            if (espansa || readOnly && (verifica.note.isNotBlank() || verifica.esito == "NON_CONFORME")) {
                var note by rememberSaveable(verifica.id, "note") { mutableStateOf(verifica.note) }
                LaunchedEffect(verifica.note) { if (note != verifica.note) note = verifica.note }
                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        onAggiornaVerifica(verifica.copy(note = it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note verifica") },
                    readOnly = readOnly,
                    minLines = 2
                )
            }

            if (verifica.esito == "NON_CONFORME") {
                NonConformitaEditor(
                    sopralluogoId = verifica.sopralluogoId,
                    verifica = verifica,
                    valore = nonConformita,
                    readOnly = readOnly,
                    onSave = onSalvaNc
                )
            } else if (nonConformita != null && !readOnly) {
                LaunchedEffect(verifica.esito) { onRimuoviNc(verifica.id) }
            }
        }
    }
}

@Composable
private fun EsitoChip(
    label: String,
    value: String,
    verifica: VerificaSopralluogo,
    readOnly: Boolean,
    onUpdate: (VerificaSopralluogo) -> Unit
) {
    FilterChip(
        selected = verifica.esito == value,
        onClick = { if (!readOnly) onUpdate(verifica.copy(esito = value)) },
        label = { Text(label) },
        enabled = !readOnly
    )
}

@Composable
private fun NonConformitaEditor(
    sopralluogoId: Long,
    verifica: VerificaSopralluogo,
    valore: NonConformita?,
    readOnly: Boolean,
    onSave: (NonConformita) -> Unit
) {
    var descrizione by rememberSaveable(verifica.id, "descrizione") { mutableStateOf(valore?.descrizione ?: "") }
    var azione by rememberSaveable(verifica.id, "azione") { mutableStateOf(valore?.azioneRichiesta ?: "") }
    var priorita by rememberSaveable(verifica.id, "priorita") { mutableStateOf(valore?.priorita ?: "MEDIA") }

    LaunchedEffect(valore?.id) {
        if (valore != null) {
            descrizione = valore.descrizione
            azione = valore.azioneRichiesta
            priorita = valore.priorita
        } else if (!readOnly) {
            onSave(
                NonConformita(
                    sopralluogoId = sopralluogoId,
                    verificaId = verifica.id,
                    priorita = priorita
                )
            )
        }
    }

    fun salva() {
        onSave(
            NonConformita(
                id = valore?.id ?: 0,
                sopralluogoId = sopralluogoId,
                verificaId = verifica.id,
                descrizione = descrizione,
                azioneRichiesta = azione,
                priorita = priorita,
                stato = valore?.stato ?: "APERTA",
                termineEpochMillis = valore?.termineEpochMillis,
                fotoUri = valore?.fotoUri ?: ""
            )
        )
    }

    Surface(color = Color(0xFFFFF1EE), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("Non conformità", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it; salva() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descrizione") },
                readOnly = readOnly,
                minLines = 2
            )
            OutlinedTextField(
                value = azione,
                onValueChange = { azione = it; salva() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Azione correttiva richiesta") },
                readOnly = readOnly,
                minLines = 2
            )
            Text("Priorità", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("BASSA", "MEDIA", "ALTA").forEach { p ->
                    FilterChip(
                        selected = priorita == p,
                        onClick = { if (!readOnly) { priorita = p; salva() } },
                        label = { Text(p.lowercase().replaceFirstChar { it.uppercase() }) },
                        enabled = !readOnly
                    )
                }
            }
        }
    }
}
