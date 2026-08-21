package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import it.progettiesoluzioni.gestionale.data.model.Cliente
import it.progettiesoluzioni.gestionale.data.model.NonConformita
import it.progettiesoluzioni.gestionale.data.model.Sede
import it.progettiesoluzioni.gestionale.data.model.Sopralluogo
import it.progettiesoluzioni.gestionale.data.model.VerificaSopralluogo
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SopralluogoPdfReport {
    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 42f

    fun generaECondividi(
        context: Context,
        sopralluogo: Sopralluogo,
        cliente: Cliente,
        sede: Sede,
        verifiche: List<VerificaSopralluogo>,
        nonConformita: List<NonConformita>
    ) {
        val file = genera(context, sopralluogo, cliente, sede, verifiche, nonConformita)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Condividi relazione sopralluogo"))
    }

    private fun genera(
        context: Context,
        sopralluogo: Sopralluogo,
        cliente: Cliente,
        sede: Sede,
        verifiche: List<VerificaSopralluogo>,
        nonConformita: List<NonConformita>
    ): File {
        val pdf = PdfDocument()
        val title = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(18, 48, 83); textSize = 18f; typeface = Typeface.DEFAULT_BOLD
        }
        val h2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(18, 48, 83); textSize = 12f; typeface = Typeface.DEFAULT_BOLD
        }
        val body = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 9.5f }
        val small = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.DKGRAY; textSize = 8f }
        val resultPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 8.5f; typeface = Typeface.DEFAULT_BOLD }
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
        val ncByVerifica = nonConformita.associateBy { it.verificaId }

        var pageNo = 0
        var page: PdfDocument.Page? = null
        var y = MARGIN

        fun startPage() {
            page?.let { pdf.finishPage(it) }
            pageNo++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
            y = MARGIN
            val c = page!!.canvas
            c.drawText("PROGETTI E SOLUZIONI", MARGIN, y, title)
            y += 25f
            c.drawText("Relazione di sopralluogo ${sopralluogo.tipoServizio}", MARGIN, y, h2)
            y += 20f
            c.drawLine(MARGIN, y, PAGE_W - MARGIN, y, Paint().apply { color = Color.LTGRAY; strokeWidth = 1f })
            y += 16f
        }

        fun ensure(space: Float) { if (y + space > PAGE_H - 45f) startPage() }

        fun drawWrapped(text: String, paint: Paint = body, indent: Float = 0f, maxWidth: Float = PAGE_W - MARGIN * 2 - indent) {
            if (text.isBlank()) return
            val words = text.replace("\n", " \n ").split(" ")
            var line = ""
            words.forEach { word ->
                if (word == "\n") {
                    ensure(14f); page!!.canvas.drawText(line.trim(), MARGIN + indent, y, paint); y += 13f; line = ""
                } else {
                    val candidate = if (line.isBlank()) word else "$line $word"
                    if (paint.measureText(candidate) > maxWidth && line.isNotBlank()) {
                        ensure(14f); page!!.canvas.drawText(line, MARGIN + indent, y, paint); y += 13f; line = word
                    } else line = candidate
                }
            }
            if (line.isNotBlank()) { ensure(14f); page!!.canvas.drawText(line, MARGIN + indent, y, paint); y += 13f }
        }

        startPage()
        val c = { page!!.canvas }
        drawWrapped("Cliente: ${cliente.ragioneSociale}", h2)
        drawWrapped("Attività: ${cliente.attivita.ifBlank { "non indicata" }}")
        drawWrapped("Sede: ${sede.nome} - ${sede.indirizzoCompleto()}")
        drawWrapped("Data sopralluogo: ${formatter.format(Date(sopralluogo.dataOraEpochMillis))}")
        drawWrapped("Stato: ${if (sopralluogo.stato == "CHIUSO") "Chiuso" else "In corso"}")
        if (sopralluogo.noteGenerali.isNotBlank()) drawWrapped("Note iniziali: ${sopralluogo.noteGenerali}")
        y += 8f

        val conf = verifiche.count { it.esito == "CONFORME" }
        val nc = verifiche.count { it.esito == "NON_CONFORME" }
        val na = verifiche.count { it.esito == "NON_APPLICABILE" }
        val dv = verifiche.count { it.esito == "DA_VERIFICARE" }
        drawWrapped("Esito sintetico", h2)
        drawWrapped("Conformi: $conf   |   Non conformi: $nc   |   Non applicabili: $na   |   Da verificare: $dv")
        y += 10f

        var currentSection = ""
        verifiche.sortedBy { it.ordine }.forEach { v ->
            if (v.sezione != currentSection) {
                currentSection = v.sezione
                ensure(34f)
                y += 6f
                drawWrapped(currentSection, h2)
            }
            ensure(62f)
            drawWrapped("${v.codice} - ${v.titolo}", body)
            resultPaint.color = when (v.esito) {
                "CONFORME" -> Color.rgb(38, 113, 58)
                "NON_CONFORME" -> Color.rgb(190, 45, 45)
                "NON_APPLICABILE" -> Color.DKGRAY
                else -> Color.rgb(154, 91, 0)
            }
            drawWrapped("Esito: ${labelEsito(v.esito)}", resultPaint, 10f)
            if (v.riferimentoNormativo.isNotBlank()) drawWrapped("Rif.: ${v.riferimentoNormativo}", small, 10f)
            if (v.note.isNotBlank()) drawWrapped("Note: ${v.note}", small, 10f)
            ncByVerifica[v.id]?.let { n ->
                drawWrapped("NC: ${n.descrizione.ifBlank { "non descritta" }}", small, 10f)
                if (n.azioneRichiesta.isNotBlank()) drawWrapped("Azione richiesta: ${n.azioneRichiesta}", small, 10f)
                drawWrapped("Priorità: ${n.priorita} - Stato: ${n.stato}", small, 10f)
            }
            y += 5f
            c().drawLine(MARGIN, y, PAGE_W - MARGIN, y, Paint().apply { color = Color.rgb(225,225,225); strokeWidth = 0.6f })
            y += 7f
        }

        if (nonConformita.isNotEmpty()) {
            ensure(50f)
            y += 8f
            drawWrapped("Riepilogo non conformità", h2)
            nonConformita.forEachIndexed { index, n ->
                ensure(55f)
                drawWrapped("NC ${index + 1} - ${n.descrizione.ifBlank { "Descrizione non compilata" }}", body)
                if (n.azioneRichiesta.isNotBlank()) drawWrapped("Azione correttiva: ${n.azioneRichiesta}", small, 10f)
                n.termineEpochMillis?.let { drawWrapped("Termine: ${SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date(it))}", small, 10f) }
                drawWrapped("Priorità: ${n.priorita} - Stato: ${n.stato} - Efficacia: ${n.verificaEfficacia}", small, 10f)
                if (n.noteVerifica.isNotBlank()) drawWrapped("Verifica finale: ${n.noteVerifica}", small, 10f)
                y += 6f
            }
        }

        ensure(65f)
        y += 15f
        drawWrapped("Conclusione", h2)
        drawWrapped("La presente relazione riepiloga le verifiche registrate nell'app durante il sopralluogo. Le voci indicate come 'Da verificare' rappresentano elementi non conclusi alla data di emissione.")
        y += 18f
        drawWrapped("Firma tecnico: ______________________________________", small)
        y += 10f
        drawWrapped("Firma referente aziendale (se prevista): __________________________", small)

        page?.let { pdf.finishPage(it) }
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val safeName = cliente.ragioneSociale.replace(Regex("[^A-Za-z0-9_-]+"), "_").take(35)
        val file = File(dir, "Relazione_${sopralluogo.tipoServizio}_${safeName}_${sopralluogo.id}.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    private fun labelEsito(esito: String) = when (esito) {
        "CONFORME" -> "Conforme"
        "NON_CONFORME" -> "Non conforme"
        "NON_APPLICABILE" -> "Non applicabile"
        else -> "Da verificare"
    }
}
