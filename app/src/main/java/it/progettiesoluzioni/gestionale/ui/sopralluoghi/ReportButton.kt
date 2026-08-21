package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import it.progettiesoluzioni.gestionale.ui.clienti.GestionaleViewModel

@Composable
fun ReportSopralluogoButton(
    sopralluogo: Sopralluogo?,
    verifiche: List<VerificaSopralluogo>,
    nonConformita: List<NonConformita>,
    viewModel: GestionaleViewModel
) {
    val s = sopralluogo ?: return
    val cliente by viewModel.cliente(s.clienteId).collectAsStateWithLifecycle(initialValue = null)
    val sedi by viewModel.sedi(s.clienteId).collectAsStateWithLifecycle(initialValue = emptyList())
    val sede = sedi.firstOrNull { it.id == s.sedeId }
    val context = LocalContext.current
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = cliente != null && sede != null,
        onClick = {
            val c = cliente ?: return@Button
            val se = sede ?: return@Button
            SopralluogoPdfReport.generaECondividi(context, s, c, se, verifiche, nonConformita)
        }
    ) {
        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
        Text("  Genera relazione PDF")
    }
}
