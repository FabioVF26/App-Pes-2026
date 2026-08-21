package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.SafetyContainer
import it.progettiesoluzioni.gestionale.ui.theme.SafetyOrange
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SopralluogoSicurezzaScreen(
    sopralluogoId: Long,
    viewModel: GestionaleViewModel,
    onBack: () -> Unit
) {
    val sopralluogo by viewModel.sopralluogo(sopralluogoId).collectAsStateWithLifecycle(initialValue = null)
    val verifiche by viewModel.verifiche(sopralluogoId).collectAsStateWithLifecycle(initialValue = emptyList())
    val nonConformita by viewModel.nonConformita(sopralluogoId).collectAsStateWithLifecycle(initialValue = emptyList())
    var messaggio by remember { mutableStateOf<String?>(null) }
    var mostraConfermaChiusura by remember { mutableStateOf(false) }
    var mostraConfermaEliminazione by remember { mutableStateOf(false) }

    val ncMap = nonConformita.associateBy { it.verificaId }
    val complete = verifiche.count { it.esito != "DA_VERIFICARE" }
    val daVerificare = verifiche.count { it.esito == "DA_VERIFICARE" }
    val ncCount = verifiche.count { it.esito == "NON_CONFORME" }
    val ncAperte = nonConformita.count { it.stato != "CHIUSA" }
    val sopralluogoChiuso = sopralluogo?.stato == "CHIUSO"

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
                    Text("Sopralluogo Sicurezza", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text("Verifiche completate: $complete / ${verifiche.size}", color = Color.White.copy(alpha = 0.86f))
                    Text("Non conformità rilevate: $ncCount · ancora aperte: $ncAperte", color = Color.White.copy(alpha = 0.86f))
                    Surface(color = SafetyContainer, shape = RoundedCornerShape(18.dp)) {
                        Text(
                            if (sopralluogoChiuso) "SOPRALLUOGO CHIUSO" else "SOPRALLUOGO IN CORSO",
                            color = SafetyOrange,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    if (sopralluogoChiuso && ncAperte > 0) {
                        Text(
                            "Le non conformità restano aggiornabili anche dopo la chiusura del sopralluogo.",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
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
                    verificaReadOnly = sopralluogoChiuso,
                    onAggiornaVerifica = viewModel::aggiornaVerifica,
                    onSalvaNc = viewModel::salvaNonConformita,
                    onRimuoviNc = viewModel::rimuoviNonConformita
                )
            }
        }

        item {
            ReportSopralluogoButton(
                sopralluogo = sopralluogo,
                verifiche = verifiche,
                nonConformita = nonConformita,
                viewModel = viewModel
            )
        }

        if (!sopralluogoChiuso) {
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val corrente = sopralluogo ?: return@Button
                        if (daVerificare > 0) {
                            mostraConfermaChiusura = true
                        } else {
                            viewModel.chiudiSopralluogo(corrente) { ok ->
                                messaggio = if (ok) "Sopralluogo chiuso correttamente." else "Impossibile chiudere il sopralluogo."
                            }
                        }
                    }
                ) { Text("Chiudi sopralluogo") }
            }
        }

        item {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { mostraConfermaEliminazione = true }
            ) {
                Text("Elimina sopralluogo", color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onBack) {
                Text("Torna all'elenco")
            }
        }
    }

    if (mostraConfermaEliminazione) {
        AlertDialog(
            onDismissRequest = { mostraConfermaEliminazione = false },
            title = { Text("Eliminare il sopralluogo?") },
            text = {
                Text("L'eliminazione è definitiva e rimuove anche verifiche, non conformità e fotografie collegate al sopralluogo.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostraConfermaEliminazione = false
                        viewModel.eliminaSopralluogo(sopralluogoId) { onBack() }
                    }
                ) { Text("Elimina", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { mostraConfermaEliminazione = false }) { Text("Annulla") }
            }
        )
    }

    if (mostraConfermaChiusura) {
        AlertDialog(
            onDismissRequest = { mostraConfermaChiusura = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostraConfermaChiusura = false
                        val corrente = sopralluogo ?: return@TextButton
                        viewModel.chiudiSopralluogo(corrente) { ok ->
                            messaggio = if (ok) {
                                "Sopralluogo chiuso con $daVerificare verifiche ancora da verificare."
                            } else {
                                "Impossibile chiudere il sopralluogo."
                            }
                        }
                    }
                ) { Text("Chiudi comunque") }
            },
            dismissButton = {
                TextButton(onClick = { mostraConfermaChiusura = false }) { Text("Annulla") }
            },
            title = { Text("Chiudere il sopralluogo?") },
            text = {
                Text(
                    "Sono presenti $daVerificare verifiche ancora impostate su ‘Da verificare’. " +
                        "Puoi chiudere comunque il sopralluogo; le voci incomplete resteranno visibili nello storico."
                )
            }
        )
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
    verificaReadOnly: Boolean,
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
                Text(
                    verifica.riferimentoNormativo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EsitoChip("Conforme", "CONFORME", verifica, verificaReadOnly, onAggiornaVerifica)
                EsitoChip("NC", "NON_CONFORME", verifica, verificaReadOnly, onAggiornaVerifica)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EsitoChip("N.A.", "NON_APPLICABILE", verifica, verificaReadOnly, onAggiornaVerifica)
                EsitoChip("Da verificare", "DA_VERIFICARE", verifica, verificaReadOnly, onAggiornaVerifica)
            }

            if (!verificaReadOnly) {
                OutlinedButton(onClick = { espansa = !espansa }) {
                    Text(if (espansa) "Nascondi note" else "Note e dettagli")
                }
            }

            if (espansa || verificaReadOnly && (verifica.note.isNotBlank() || verifica.esito == "NON_CONFORME")) {
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
                    readOnly = verificaReadOnly,
                    minLines = 2
                )
            }

            if (verifica.esito == "NON_CONFORME") {
                NonConformitaEditor(
                    sopralluogoId = verifica.sopralluogoId,
                    verifica = verifica,
                    valore = nonConformita,
                    readOnly = nonConformita?.stato == "CHIUSA",
                    onSave = onSalvaNc
                )
            } else if (nonConformita != null && !verificaReadOnly) {
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
    val context = LocalContext.current
    var descrizione by rememberSaveable(verifica.id, "descrizione") { mutableStateOf(valore?.descrizione ?: "") }
    var azione by rememberSaveable(verifica.id, "azione") { mutableStateOf(valore?.azioneRichiesta ?: "") }
    var sanzionePossibile by rememberSaveable(verifica.id, "sanzione") { mutableStateOf(valore?.sanzionePossibile?.takeIf { it.isNotBlank() } ?: SanzioniSicurezza.proposta(verifica.codice)) }
    var priorita by rememberSaveable(verifica.id, "priorita") { mutableStateOf(valore?.priorita ?: "MEDIA") }
    var stato by rememberSaveable(verifica.id, "stato") { mutableStateOf(valore?.stato ?: "APERTA") }
    var termine by rememberSaveable(verifica.id, "termine") { mutableStateOf(valore?.termineEpochMillis) }
    var fotoUri by rememberSaveable(verifica.id, "fotoUri") { mutableStateOf(valore?.fotoUri ?: "") }
    var fotoRisoluzioneUri by rememberSaveable(verifica.id, "fotoRisoluzioneUri") { mutableStateOf(valore?.fotoRisoluzioneUri ?: "") }
    var verificaEfficacia by rememberSaveable(verifica.id, "verificaEfficacia") { mutableStateOf(valore?.verificaEfficacia ?: "DA_VERIFICARE") }
    var noteVerifica by rememberSaveable(verifica.id, "noteVerifica") { mutableStateOf(valore?.noteVerifica ?: "") }
    var dataRisoluzione by rememberSaveable(verifica.id, "dataRisoluzione") { mutableStateOf(valore?.dataRisoluzioneEpochMillis) }
    var dataChiusura by rememberSaveable(verifica.id, "dataChiusura") { mutableStateOf(valore?.dataChiusuraEpochMillis) }
    var fotoInAttesa by remember { mutableStateOf<Uri?>(null) }
    var fotoRisoluzioneInAttesa by remember { mutableStateOf<Uri?>(null) }

    fun salva() {
        onSave(
            NonConformita(
                id = valore?.id ?: 0,
                sopralluogoId = sopralluogoId,
                verificaId = verifica.id,
                descrizione = descrizione,
                azioneRichiesta = azione,
                sanzionePossibile = sanzionePossibile,
                priorita = priorita,
                stato = stato,
                termineEpochMillis = termine,
                fotoUri = fotoUri,
                fotoRisoluzioneUri = fotoRisoluzioneUri,
                verificaEfficacia = verificaEfficacia,
                noteVerifica = noteVerifica,
                dataRisoluzioneEpochMillis = dataRisoluzione,
                dataChiusuraEpochMillis = dataChiusura
            )
        )
    }

    val fotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) {
            fotoUri = fotoInAttesa?.toString().orEmpty()
            salva()
        }
        fotoInAttesa = null
    }
    val fotoRisoluzioneLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) {
            fotoRisoluzioneUri = fotoRisoluzioneInAttesa?.toString().orEmpty()
            salva()
        }
        fotoRisoluzioneInAttesa = null
    }

    LaunchedEffect(valore) {
        if (valore != null) {
            descrizione = valore.descrizione
            azione = valore.azioneRichiesta
            val propostaAutomatica = SanzioniSicurezza.proposta(verifica.codice)
            sanzionePossibile = valore.sanzionePossibile.ifBlank { propostaAutomatica }
            if (valore.sanzionePossibile.isBlank() && propostaAutomatica.isNotBlank() && !readOnly) {
                onSave(valore.copy(sanzionePossibile = propostaAutomatica))
            }
            priorita = valore.priorita
            stato = valore.stato
            termine = valore.termineEpochMillis
            fotoUri = valore.fotoUri
            fotoRisoluzioneUri = valore.fotoRisoluzioneUri
            verificaEfficacia = valore.verificaEfficacia
            noteVerifica = valore.noteVerifica
            dataRisoluzione = valore.dataRisoluzioneEpochMillis
            dataChiusura = valore.dataChiusuraEpochMillis
        } else if (!readOnly) {
            salva()
        }
    }

    Surface(color = Color(0xFFFFF1EE), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Non conformità", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
                NcStatusBadge(stato)
            }

            OutlinedTextField(
                value = descrizione,
                onValueChange = { descrizione = it; salva() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descrizione della non conformità") },
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

            OutlinedTextField(
                value = sanzionePossibile,
                onValueChange = { sanzionePossibile = it; salva() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Possibile sanzione (indicativa)") },
                supportingText = { Text("Proposta automatica da verificare sulla fattispecie concreta e sulla norma vigente.") },
                readOnly = readOnly,
                minLines = 3
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

            Text("Stato della NC", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "APERTA" to "Aperta",
                    "IN_LAVORAZIONE" to "In corso",
                    "RISOLTA" to "Risolta"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = stato == value,
                        onClick = {
                            if (!readOnly) {
                                stato = value
                                if (value == "RISOLTA" && dataRisoluzione == null) dataRisoluzione = System.currentTimeMillis()
                                if (value != "RISOLTA") {
                                    dataRisoluzione = null
                                    verificaEfficacia = "DA_VERIFICARE"
                                }
                                salva()
                            }
                        },
                        label = { Text(label) },
                        enabled = !readOnly
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance().apply { termine?.let { timeInMillis = it } }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val selected = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, day)
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            termine = selected.timeInMillis
                            salva()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                enabled = !readOnly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (termine == null) "Imposta termine di risoluzione" else "Termine: ${formatDate(termine)}")
            }

            PhotoSection(
                title = "Evidenza fotografica della non conformità",
                uriString = fotoUri,
                buttonLabel = if (fotoUri.isBlank()) "Scatta foto" else "Aggiorna foto",
                enabled = !readOnly,
                onTakePhoto = {
                    val uri = createImageUri(context, "nc_${verifica.id}_")
                    fotoInAttesa = uri
                    fotoLauncher.launch(uri)
                },
                onRemove = {
                    fotoUri = ""
                    salva()
                }
            )

            if (stato == "RISOLTA" || stato == "CHIUSA") {
                Surface(color = Color.White.copy(alpha = 0.72f), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Verifica della risoluzione", style = MaterialTheme.typography.titleSmall, color = BrandNavy)
                        if (dataRisoluzione != null) {
                            Text("Risoluzione registrata il ${formatDate(dataRisoluzione)}", style = MaterialTheme.typography.bodySmall)
                        }

                        PhotoSection(
                            title = "Fotografia dopo l'azione correttiva",
                            uriString = fotoRisoluzioneUri,
                            buttonLabel = if (fotoRisoluzioneUri.isBlank()) "Scatta foto di risoluzione" else "Aggiorna foto di risoluzione",
                            enabled = !readOnly,
                            onTakePhoto = {
                                val uri = createImageUri(context, "nc_risolta_${verifica.id}_")
                                fotoRisoluzioneInAttesa = uri
                                fotoRisoluzioneLauncher.launch(uri)
                            },
                            onRemove = {
                                fotoRisoluzioneUri = ""
                                salva()
                            }
                        )

                        Text("Efficacia dell'azione correttiva", style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "EFFICACE" to "Efficace",
                                "NON_EFFICACE" to "Non efficace",
                                "DA_VERIFICARE" to "Da verificare"
                            ).forEach { (value, label) ->
                                FilterChip(
                                    selected = verificaEfficacia == value,
                                    onClick = { if (!readOnly) { verificaEfficacia = value; salva() } },
                                    label = { Text(label) },
                                    enabled = !readOnly
                                )
                            }
                        }

                        OutlinedTextField(
                            value = noteVerifica,
                            onValueChange = { noteVerifica = it; salva() },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Esito verifica / note di chiusura") },
                            readOnly = readOnly,
                            minLines = 2
                        )

                        if (stato != "CHIUSA" && !readOnly) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                enabled = verificaEfficacia == "EFFICACE",
                                onClick = {
                                    stato = "CHIUSA"
                                    dataChiusura = System.currentTimeMillis()
                                    salva()
                                }
                            ) { Text("Chiudi non conformità") }

                            if (verificaEfficacia == "NON_EFFICACE") {
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        stato = "IN_LAVORAZIONE"
                                        dataRisoluzione = null
                                        verificaEfficacia = "DA_VERIFICARE"
                                        salva()
                                    }
                                ) { Text("Riapri e prosegui azioni correttive") }
                            }
                        }

                        if (stato == "CHIUSA" && dataChiusura != null) {
                            Text(
                                "NC chiusa il ${formatDate(dataChiusura)}",
                                color = SafetyOrange,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NcStatusBadge(stato: String) {
    val (label, color) = when (stato) {
        "IN_LAVORAZIONE" -> "IN CORSO" to Color(0xFFEF6C00)
        "RISOLTA" -> "RISOLTA" to Color(0xFF1565C0)
        "CHIUSA" -> "CHIUSA" to SafetyOrange
        else -> "APERTA" to MaterialTheme.colorScheme.error
    }
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(16.dp)) {
        Text(label, color = color, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PhotoSection(
    title: String,
    uriString: String,
    buttonLabel: String,
    enabled: Boolean,
    onTakePhoto: () -> Unit,
    onRemove: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        if (uriString.isNotBlank()) {
            UriImage(uriString)
        }
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onTakePhoto, enabled = enabled) {
            Text(buttonLabel)
        }
        if (uriString.isNotBlank() && enabled) {
            TextButton(onClick = onRemove) { Text("Rimuovi fotografia") }
        }
    }
}

@Composable
private fun UriImage(uriString: String) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        decodePreviewBitmap(context, Uri.parse(uriString))
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Fotografia non conformità",
            modifier = Modifier.fillMaxWidth().height(190.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Text("Fotografia non disponibile", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}

private fun createImageUri(context: android.content.Context, prefix: String): Uri {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    if (!dir.exists()) dir.mkdirs()
    val file = File.createTempFile(prefix, ".jpg", dir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun formatDate(epochMillis: Long?): String {
    if (epochMillis == null) return "—"
    return SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(epochMillis))
}


private fun decodePreviewBitmap(context: android.content.Context, uri: Uri): android.graphics.Bitmap? {
    return runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > 1200 || bounds.outHeight / sample > 1200) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }.getOrNull()
}
