package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.progettiesoluzioni.gestionale.ui.theme.BrandNavy
import it.progettiesoluzioni.gestionale.ui.theme.DeadlineContainer
import it.progettiesoluzioni.gestionale.ui.theme.DeadlineRed

/**
 * Intestazione cliccabile di una sezione della checklist. Mostra il titolo, quante voci
 * sono state verificate sul totale e, se presenti, quante sono non conformi, cosi' anche
 * a sezione chiusa si vede a colpo d'occhio a che punto si e'.
 */
@Composable
fun IntestazioneSezione(
    titolo: String,
    totale: Int,
    completate: Int,
    nonConformi: Int,
    espansa: Boolean,
    coloreAccento: Color,
    coloreContenitore: Color,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onToggle),
        color = coloreContenitore,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    titolo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavy
                )
                Text(
                    "$completate / $totale verificate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (completate == totale) coloreAccento else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (nonConformi > 0) {
                Surface(color = DeadlineContainer, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        "NC $nonConformi",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeadlineRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Icon(
                imageVector = if (espansa) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (espansa) "Comprimi sezione" else "Espandi sezione",
                tint = coloreAccento
            )
        }
    }
}

/** Pulsante unico per aprire o chiudere tutte le sezioni in un colpo solo. */
@Composable
fun ControlloEspandiTutto(tutteAperte: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onToggle) {
            Text(if (tutteAperte) "Comprimi tutto" else "Espandi tutto")
        }
    }
}
