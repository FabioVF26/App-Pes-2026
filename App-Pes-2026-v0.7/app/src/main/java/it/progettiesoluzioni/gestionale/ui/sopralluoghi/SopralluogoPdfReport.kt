package it.progettiesoluzioni.gestionale.ui.sopralluoghi

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import it.progettiesoluzioni.gestionale.R
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
import kotlin.math.min

object SopralluogoPdfReport {
    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 38f
    private const val FOOTER_H = 35f

    private val NAVY = Color.rgb(9, 35, 82)
    private val BLUE = Color.rgb(29, 102, 176)
    private val CYAN = Color.rgb(25, 171, 207)
    private val GREEN = Color.rgb(37, 128, 65)
    private val RED = Color.rgb(190, 44, 44)
    private val ORANGE = Color.rgb(187, 104, 16)
    private val DARK = Color.rgb(40, 47, 55)
    private val MID = Color.rgb(91, 101, 111)
    private val LIGHT = Color.rgb(239, 244, 248)
    private val LINE = Color.rgb(218, 224, 230)

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
            putExtra(Intent.EXTRA_SUBJECT, "Relazione sopralluogo ${cliente.ragioneSociale}")
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
        val dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY)
        val dateOnly = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)
        val ncByVerifica = nonConformita.associateBy { it.verificaId }
        val reportCode = "PS-${sopralluogo.tipoServizio.take(3).uppercase(Locale.ITALY)}-${dateOnly.format(Date(sopralluogo.dataOraEpochMillis)).replace("/", "")}-${sopralluogo.id}"

        val pTitle = paint(20f, NAVY, true)
        val pSubtitle = paint(12.5f, NAVY, true)
        val pH2 = paint(12f, NAVY, true)
        val pBody = paint(9.2f, DARK, false)
        val pBodyBold = paint(9.2f, DARK, true)
        val pSmall = paint(7.8f, MID, false)
        val pSmallBold = paint(7.8f, DARK, true)
        val pWhite = paint(8.6f, Color.WHITE, true)

        var pageNo = 0
        var page: PdfDocument.Page? = null
        var y = MARGIN
        var currentCanvas: Canvas? = null

        fun drawFooter() {
            val c = currentCanvas ?: return
            val footerY = PAGE_H - 24f
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINE; strokeWidth = 0.7f }
            c.drawLine(MARGIN, PAGE_H - FOOTER_H, PAGE_W - MARGIN, PAGE_H - FOOTER_H, linePaint)
            c.drawText("Progetti e Soluzioni - Relazione tecnica di sopralluogo", MARGIN, footerY, pSmall)
            val pageText = "Pag. $pageNo"
            c.drawText(pageText, PAGE_W - MARGIN - pSmall.measureText(pageText), footerY, pSmall)
        }

        fun startPage(repeatHeader: Boolean = true) {
            page?.let {
                drawFooter()
                pdf.finishPage(it)
            }
            pageNo++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNo).create())
            currentCanvas = page!!.canvas
            y = MARGIN
            if (repeatHeader) {
                drawCompactHeader(currentCanvas!!, context, sopralluogo.tipoServizio, reportCode, pSmall, pSmallBold)
                y = 82f
            }
        }

        fun ensure(space: Float) {
            if (y + space > PAGE_H - FOOTER_H - 8f) startPage(true)
        }

        fun wrappedHeight(text: String, paint: Paint, width: Float, lineH: Float = 12f): Float =
            wrapLines(text, paint, width).size.coerceAtLeast(1) * lineH

        fun drawWrapped(text: String, paint: Paint = pBody, x: Float = MARGIN, width: Float = PAGE_W - MARGIN * 2, lineH: Float = 12f) {
            if (text.isBlank()) return
            val lines = wrapLines(text, paint, width)
            lines.forEach { line ->
                ensure(lineH + 2f)
                currentCanvas!!.drawText(line, x, y, paint)
                y += lineH
            }
        }

        fun labelChip(label: String, value: Int, color: Int, x: Float, yy: Float, w: Float) {
            val c = currentCanvas!!
            val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = withAlpha(color, 24); style = Paint.Style.FILL }
            val border = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = withAlpha(color, 90); style = Paint.Style.STROKE; strokeWidth = 0.8f }
            val rect = RectF(x, yy, x + w, yy + 43f)
            c.drawRoundRect(rect, 8f, 8f, bg)
            c.drawRoundRect(rect, 8f, 8f, border)
            val valuePaint = paint(17f, color, true)
            val labelPaint = paint(7.4f, color, true)
            c.drawText(value.toString(), x + 10f, yy + 20f, valuePaint)
            c.drawText(label, x + 10f, yy + 34f, labelPaint)
        }

        // Pagina 1: intestazione completa e sintesi
        startPage(false)
        val c = currentCanvas!!
        drawMainHeader(c, context, sopralluogo.tipoServizio, reportCode, pTitle, pSubtitle, pSmall)
        y = 132f

        drawSectionBar(c, y, "DATI DEL SOPRALLUOGO", NAVY, pWhite)
        y += 27f
        val boxTop = y
        val boxH = 112f
        c.drawRoundRect(RectF(MARGIN, boxTop, PAGE_W - MARGIN, boxTop + boxH), 7f, 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
        c.drawRoundRect(RectF(MARGIN, boxTop, PAGE_W - MARGIN, boxTop + boxH), 7f, 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINE; style = Paint.Style.STROKE; strokeWidth = 0.8f })

        var metaY = boxTop + 18f
        fun meta(label: String, value: String, boldValue: Boolean = false) {
            c.drawText(label, MARGIN + 12f, metaY, pSmallBold)
            val xVal = MARGIN + 112f
            val usePaint = if (boldValue) pBodyBold else pBody
            val lines = wrapLines(value.ifBlank { "Non indicato" }, usePaint, PAGE_W - MARGIN - 12f - xVal)
            lines.take(2).forEachIndexed { idx, line -> c.drawText(line, xVal, metaY + idx * 11f, usePaint) }
            metaY += maxOf(17f, lines.take(2).size * 11f + 4f)
        }
        meta("Cliente", cliente.ragioneSociale, true)
        meta("Attività", cliente.attivita)
        meta("Sede", listOf(sede.nome, sede.indirizzoCompleto()).filter { it.isNotBlank() }.joinToString(" - "))
        meta("Data e ora", dateTime.format(Date(sopralluogo.dataOraEpochMillis)))
        meta("Stato", if (sopralluogo.stato == "CHIUSO") "Chiuso" else "In corso")
        y = boxTop + boxH + 15f

        val conf = verifiche.count { it.esito == "CONFORME" }
        val ncCount = verifiche.count { it.esito == "NON_CONFORME" }
        val na = verifiche.count { it.esito == "NON_APPLICABILE" }
        val dv = verifiche.count { it.esito == "DA_VERIFICARE" }

        drawSectionBar(c, y, "ESITO SINTETICO", NAVY, pWhite)
        y += 34f
        val gap = 7f
        val chipW = (PAGE_W - MARGIN * 2 - gap * 3) / 4
        labelChip("CONFORMI", conf, GREEN, MARGIN, y, chipW)
        labelChip("NON CONFORMI", ncCount, RED, MARGIN + (chipW + gap), y, chipW)
        labelChip("NON APPLICABILI", na, MID, MARGIN + (chipW + gap) * 2, y, chipW)
        labelChip("DA VERIFICARE", dv, ORANGE, MARGIN + (chipW + gap) * 3, y, chipW)
        y += 57f

        if (sopralluogo.noteGenerali.isNotBlank()) {
            drawSectionBar(c, y, "NOTE GENERALI", BLUE, pWhite)
            y += 26f
            val noteH = wrappedHeight(sopralluogo.noteGenerali, pBody, PAGE_W - MARGIN * 2 - 22f) + 18f
            c.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + noteH), 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT })
            val oldY = y
            y += 14f
            drawWrapped(sopralluogo.noteGenerali, pBody, MARGIN + 11f, PAGE_W - MARGIN * 2 - 22f)
            y = maxOf(y + 5f, oldY + noteH + 8f)
        }

        if (nonConformita.isNotEmpty()) {
            ensure(70f)
            drawSectionBar(currentCanvas!!, y, "NON CONFORMITÀ RILEVATE", RED, pWhite)
            y += 28f
            nonConformita.forEachIndexed { i, n ->
                val linked = verifiche.firstOrNull { it.id == n.verificaId }
                val titleText = "NC ${i + 1}${linked?.let { " - ${it.codice}" } ?: ""}: ${n.descrizione.ifBlank { "Descrizione non compilata" }}"
                val h = 34f + wrappedHeight(titleText, pBodyBold, PAGE_W - MARGIN * 2 - 22f) +
                    wrappedHeight(n.azioneRichiesta, pSmall, PAGE_W - MARGIN * 2 - 22f) +
                    if (n.sanzionePossibile.isNotBlank()) 26f else 0f
                ensure(h + 12f)
                val top = y
                currentCanvas!!.drawRoundRect(RectF(MARGIN, top, PAGE_W - MARGIN, top + h), 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(RED, 12) })
                currentCanvas!!.drawRect(RectF(MARGIN, top, MARGIN + 4f, top + h), Paint(Paint.ANTI_ALIAS_FLAG).apply { color = RED })
                y += 14f
                drawWrapped(titleText, pBodyBold, MARGIN + 11f, PAGE_W - MARGIN * 2 - 22f)
                if (n.azioneRichiesta.isNotBlank()) drawWrapped("Azione richiesta: ${n.azioneRichiesta}", pSmall, MARGIN + 11f, PAGE_W - MARGIN * 2 - 22f, 10.5f)
                val term = n.termineEpochMillis?.let { dateOnly.format(Date(it)) } ?: "non indicato"
                drawWrapped("Priorità: ${labelPriorita(n.priorita)}  |  Stato: ${labelStatoNc(n.stato)}  |  Termine: $term", pSmallBold, MARGIN + 11f, PAGE_W - MARGIN * 2 - 22f, 10.5f)
                if (n.sanzionePossibile.isNotBlank()) drawWrapped("Possibile sanzione (indicativa): ${n.sanzionePossibile}", pSmall, MARGIN + 11f, PAGE_W - MARGIN * 2 - 22f, 10.5f)
                y = maxOf(y + 8f, top + h + 8f)
            }
        }

        // Corpo verifiche
        ensure(35f)
        drawSectionBar(currentCanvas!!, y, "ESITO DELLE VERIFICHE", NAVY, pWhite)
        y += 33f

        var currentSection = ""
        verifiche.sortedBy { it.ordine }.forEach { v ->
            if (v.sezione != currentSection) {
                currentSection = v.sezione
                ensure(34f)
                y += 3f
                currentCanvas!!.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + 23f), 4f, 4f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT })
                currentCanvas!!.drawText(currentSection.uppercase(Locale.ITALY), MARGIN + 9f, y + 15f, pH2)
                y += 31f
            }

            val n = ncByVerifica[v.id]
            val titleLines = wrapLines("${v.codice} - ${v.titolo}", pBody, PAGE_W - MARGIN * 2 - 20f)
            var cardH = 16f + titleLines.size * 11.5f + 20f
            if (v.riferimentoNormativo.isNotBlank()) cardH += wrapLines(v.riferimentoNormativo, pSmall, PAGE_W - MARGIN * 2 - 28f).size * 10f + 3f
            if (v.note.isNotBlank()) cardH += wrapLines(v.note, pSmall, PAGE_W - MARGIN * 2 - 28f).size * 10f + 3f
            if (n != null) {
                cardH += 24f
                if (n.azioneRichiesta.isNotBlank()) cardH += wrapLines(n.azioneRichiesta, pSmall, PAGE_W - MARGIN * 2 - 28f).size * 10f
            }
            cardH += 10f
            ensure(cardH + 6f)

            val top = y
            val accent = esitoColor(v.esito)
            val bg = if (v.esito == "NON_CONFORME") withAlpha(RED, 10) else Color.WHITE
            currentCanvas!!.drawRoundRect(RectF(MARGIN, top, PAGE_W - MARGIN, top + cardH), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bg })
            currentCanvas!!.drawRoundRect(RectF(MARGIN, top, PAGE_W - MARGIN, top + cardH), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINE; style = Paint.Style.STROKE; strokeWidth = 0.7f })
            currentCanvas!!.drawRect(RectF(MARGIN, top, MARGIN + 3.5f, top + cardH), Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent })
            y += 13f
            drawWrapped("${v.codice} - ${v.titolo}", pBody, MARGIN + 10f, PAGE_W - MARGIN * 2 - 20f, 11.5f)

            val chipText = labelEsito(v.esito)
            val chipPaint = paint(7.5f, accent, true)
            val chipWidth = chipPaint.measureText(chipText) + 16f
            currentCanvas!!.drawRoundRect(RectF(MARGIN + 10f, y, MARGIN + 10f + chipWidth, y + 17f), 8f, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(accent, 24) })
            currentCanvas!!.drawText(chipText, MARGIN + 18f, y + 11.5f, chipPaint)
            y += 23f
            if (v.riferimentoNormativo.isNotBlank()) drawWrapped("Riferimento: ${v.riferimentoNormativo}", pSmall, MARGIN + 12f, PAGE_W - MARGIN * 2 - 24f, 10f)
            if (v.note.isNotBlank()) drawWrapped("Osservazioni: ${v.note}", pSmall, MARGIN + 12f, PAGE_W - MARGIN * 2 - 24f, 10f)
            n?.let {
                drawWrapped("Non conformità: ${it.descrizione.ifBlank { "non descritta" }}", pSmallBold, MARGIN + 12f, PAGE_W - MARGIN * 2 - 24f, 10f)
                if (it.azioneRichiesta.isNotBlank()) drawWrapped("Azione correttiva: ${it.azioneRichiesta}", pSmall, MARGIN + 12f, PAGE_W - MARGIN * 2 - 24f, 10f)
            }
            y = maxOf(y + 5f, top + cardH + 6f)
        }

        // Schede dettagliate NC con foto
        if (nonConformita.isNotEmpty()) {
            startPage(true)
            drawSectionBar(currentCanvas!!, y, "SCHEDE DI NON CONFORMITÀ", RED, pWhite)
            y += 34f
            nonConformita.forEachIndexed { index, n ->
                val v = verifiche.firstOrNull { it.id == n.verificaId }
                ensure(160f)
                val heading = "NC ${index + 1}${v?.let { " - ${it.codice}" } ?: ""}"
                currentCanvas!!.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + 28f), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = RED })
                currentCanvas!!.drawText(heading, MARGIN + 10f, y + 18f, pWhite)
                y += 39f

                if (v != null) {
                    drawWrapped(v.titolo, pBodyBold)
                    if (v.riferimentoNormativo.isNotBlank()) drawWrapped("Riferimento normativo: ${v.riferimentoNormativo}", pSmall, lineH = 10f)
                    y += 4f
                }

                drawKeyValueBlock(currentCanvas!!, MARGIN, y, PAGE_W - MARGIN * 2, listOf(
                    "Descrizione" to n.descrizione.ifBlank { "Non compilata" },
                    "Azione correttiva richiesta" to n.azioneRichiesta.ifBlank { "Non indicata" },
                    "Priorità" to labelPriorita(n.priorita),
                    "Stato" to labelStatoNc(n.stato),
                    "Termine" to (n.termineEpochMillis?.let { dateOnly.format(Date(it)) } ?: "Non indicato"),
                    "Verifica efficacia" to labelEfficacia(n.verificaEfficacia)
                ), pSmallBold, pBody, ::wrapLines).also { y += it + 8f }

                if (n.noteVerifica.isNotBlank()) {
                    ensure(40f)
                    drawWrapped("Esito / note della verifica finale", pSmallBold)
                    drawWrapped(n.noteVerifica, pBody)
                    y += 5f
                }

                if (n.sanzionePossibile.isNotBlank()) {
                    val sanH = wrappedHeight(n.sanzionePossibile, pSmall, PAGE_W - MARGIN * 2 - 22f, 10f) + 34f
                    ensure(sanH + 8f)
                    val top = y
                    currentCanvas!!.drawRoundRect(RectF(MARGIN, top, PAGE_W - MARGIN, top + sanH), 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(ORANGE, 18) })
                    currentCanvas!!.drawText("POSSIBILE SANZIONE - INDICAZIONE DA VERIFICARE", MARGIN + 10f, top + 16f, paint(7.6f, ORANGE, true))
                    y = top + 31f
                    drawWrapped(n.sanzionePossibile, pSmall, MARGIN + 10f, PAGE_W - MARGIN * 2 - 20f, 10f)
                    y = maxOf(y + 6f, top + sanH + 8f)
                }

                val before = loadBitmap(context, n.fotoUri)
                val after = loadBitmap(context, n.fotoRisoluzioneUri)
                if (before != null || after != null) {
                    ensure(210f)
                    drawWrapped("Documentazione fotografica", pH2)
                    y += 4f
                    val available = PAGE_W - MARGIN * 2
                    val photoGap = 12f
                    val boxW = if (before != null && after != null) (available - photoGap) / 2 else available
                    val boxH = 168f
                    var x = MARGIN
                    if (before != null) {
                        drawPhotoBox(currentCanvas!!, before, x, y, boxW, boxH, "Rilievo / prima dell'intervento", pSmallBold)
                        x += boxW + photoGap
                    }
                    if (after != null) {
                        drawPhotoBox(currentCanvas!!, after, x, y, boxW, boxH, "Dopo l'intervento correttivo", pSmallBold)
                    }
                    before?.recycle()
                    after?.recycle()
                    y += boxH + 22f
                }

                ensure(22f)
                currentCanvas!!.drawLine(MARGIN, y, PAGE_W - MARGIN, y, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINE; strokeWidth = 0.8f })
                y += 18f
            }
        }

        // Conclusioni e firme
        ensure(165f)
        drawSectionBar(currentCanvas!!, y, "CONCLUSIONI", NAVY, pWhite)
        y += 30f
        val conclusion = buildConclusion(conf, ncCount, na, dv, nonConformita)
        drawWrapped(conclusion, pBody, lineH = 12f)
        y += 10f

        if (nonConformita.any { it.sanzionePossibile.isNotBlank() }) {
            val disclaimer = "Le indicazioni sanzionatorie riportate nella relazione hanno esclusivamente funzione di supporto tecnico. L'effettiva applicabilità della sanzione, il soggetto obbligato, il precetto violato, l'importo e gli eventuali aggiornamenti devono essere verificati sulla fattispecie concreta e sulla normativa vigente alla data della contestazione."
            val dh = wrappedHeight(disclaimer, pSmall, PAGE_W - MARGIN * 2 - 22f, 10f) + 30f
            ensure(dh + 10f)
            val top = y
            currentCanvas!!.drawRoundRect(RectF(MARGIN, top, PAGE_W - MARGIN, top + dh), 6f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT })
            currentCanvas!!.drawText("NOTA SULLE SANZIONI", MARGIN + 10f, top + 15f, pSmallBold)
            y = top + 29f
            drawWrapped(disclaimer, pSmall, MARGIN + 10f, PAGE_W - MARGIN * 2 - 20f, 10f)
            y = maxOf(y + 5f, top + dh + 10f)
        }

        ensure(95f)
        val sigTop = y + 10f
        currentCanvas!!.drawText("Tecnico / Consulente", MARGIN, sigTop, pSmallBold)
        currentCanvas!!.drawText("Referente aziendale", PAGE_W / 2 + 20f, sigTop, pSmallBold)
        currentCanvas!!.drawLine(MARGIN, sigTop + 45f, PAGE_W / 2 - 20f, sigTop + 45f, Paint().apply { color = MID; strokeWidth = 0.7f })
        currentCanvas!!.drawLine(PAGE_W / 2 + 20f, sigTop + 45f, PAGE_W - MARGIN, sigTop + 45f, Paint().apply { color = MID; strokeWidth = 0.7f })
        currentCanvas!!.drawText("Firma", MARGIN, sigTop + 57f, pSmall)
        currentCanvas!!.drawText("Firma (se prevista)", PAGE_W / 2 + 20f, sigTop + 57f, pSmall)
        y = sigTop + 70f

        page?.let {
            drawFooter()
            pdf.finishPage(it)
        }

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val safeName = cliente.ragioneSociale.replace(Regex("[^A-Za-z0-9_-]+"), "_").take(35)
        val file = File(dir, "Relazione_${sopralluogo.tipoServizio}_${safeName}_${sopralluogo.id}.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    private fun drawMainHeader(canvas: Canvas, context: Context, tipo: String, code: String, title: Paint, subtitle: Paint, small: Paint) {
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.ps_logo_splash)
        val logoW = 96f
        val logoH = 68f
        if (logo != null) {
            drawBitmapFit(canvas, logo, RectF(MARGIN, 22f, MARGIN + logoW, 22f + logoH))
            logo.recycle()
        }
        canvas.drawText("RELAZIONE DI SOPRALLUOGO", MARGIN + 112f, 47f, title)
        canvas.drawText(serviceTitle(tipo), MARGIN + 112f, 68f, subtitle)
        canvas.drawText("Codice relazione: $code", MARGIN + 112f, 85f, small)
        val band = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = NAVY }
        canvas.drawRect(0f, 105f, PAGE_W.toFloat(), 112f, band)
        canvas.drawRect(0f, 112f, PAGE_W.toFloat(), 116f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = CYAN })
    }

    private fun drawCompactHeader(canvas: Canvas, context: Context, tipo: String, code: String, small: Paint, smallBold: Paint) {
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.ps_logo_splash)
        if (logo != null) {
            drawBitmapFit(canvas, logo, RectF(MARGIN, 18f, MARGIN + 62f, 18f + 43f))
            logo.recycle()
        }
        canvas.drawText("RELAZIONE DI SOPRALLUOGO - ${serviceTitle(tipo)}", MARGIN + 74f, 39f, paint(10f, NAVY, true))
        canvas.drawText(code, MARGIN + 74f, 54f, small)
        canvas.drawLine(MARGIN, 68f, PAGE_W - MARGIN, 68f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = NAVY; strokeWidth = 1.2f })
    }

    private fun drawSectionBar(canvas: Canvas, y: Float, text: String, color: Int, whitePaint: Paint) {
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_W - MARGIN, y + 22f), 4f, 4f, Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
        canvas.drawText(text, MARGIN + 9f, y + 15f, whitePaint)
    }

    private fun drawKeyValueBlock(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        rows: List<Pair<String, String>>,
        labelPaint: Paint,
        valuePaint: Paint,
        wrapper: (String, Paint, Float) -> List<String>
    ): Float {
        val labelW = 145f
        val lineH = 11f
        var yy = y
        val heights = rows.map { (_, value) -> maxOf(24f, wrapper(value, valuePaint, width - labelW - 20f).size * lineH + 10f) }
        val total = heights.sum()
        canvas.drawRoundRect(RectF(x, y, x + width, y + total), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
        canvas.drawRoundRect(RectF(x, y, x + width, y + total), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINE; style = Paint.Style.STROKE; strokeWidth = 0.7f })
        rows.forEachIndexed { index, (label, value) ->
            val h = heights[index]
            canvas.drawRect(RectF(x, yy, x + labelW, yy + h), Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT })
            canvas.drawText(label, x + 8f, yy + 15f, labelPaint)
            val lines = wrapper(value, valuePaint, width - labelW - 20f)
            lines.forEachIndexed { li, line -> canvas.drawText(line, x + labelW + 8f, yy + 15f + li * lineH, valuePaint) }
            yy += h
            if (index != rows.lastIndex) canvas.drawLine(x, yy, x + width, yy, Paint().apply { color = LINE; strokeWidth = 0.5f })
        }
        return total
    }

    private fun drawPhotoBox(canvas: Canvas, bitmap: Bitmap, x: Float, y: Float, w: Float, h: Float, caption: String, captionPaint: Paint) {
        val imageRect = RectF(x, y, x + w, y + h - 21f)
        canvas.drawRoundRect(RectF(x, y, x + w, y + h), 5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LIGHT })
        drawBitmapCropCenter(canvas, bitmap, imageRect)
        canvas.drawText(caption, x + 7f, y + h - 7f, captionPaint)
    }

    private fun drawBitmapFit(canvas: Canvas, bitmap: Bitmap, dst: RectF) {
        val scale = min(dst.width() / bitmap.width, dst.height() / bitmap.height)
        val w = bitmap.width * scale
        val h = bitmap.height * scale
        val left = dst.left + (dst.width() - w) / 2f
        val top = dst.top + (dst.height() - h) / 2f
        canvas.drawBitmap(bitmap, null, RectF(left, top, left + w, top + h), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }

    private fun drawBitmapCropCenter(canvas: Canvas, bitmap: Bitmap, dst: RectF) {
        val srcRatio = bitmap.width.toFloat() / bitmap.height
        val dstRatio = dst.width() / dst.height()
        val src = if (srcRatio > dstRatio) {
            val newW = (bitmap.height * dstRatio).toInt()
            val left = (bitmap.width - newW) / 2
            android.graphics.Rect(left, 0, left + newW, bitmap.height)
        } else {
            val newH = (bitmap.width / dstRatio).toInt()
            val top = (bitmap.height - newH) / 2
            android.graphics.Rect(0, top, bitmap.width, top + newH)
        }
        canvas.save()
        canvas.clipRect(dst)
        canvas.drawBitmap(bitmap, src, dst, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        canvas.restore()
    }

    private fun loadBitmap(context: Context, uriString: String): Bitmap? {
        if (uriString.isBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    val maxSide = maxOf(info.size.width, info.size.height)
                    if (maxSide > 1800) {
                        val scale = 1800f / maxSide
                        decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                    }
                }
            } else {
                context.contentResolver.openInputStream(uri)?.use { input -> BitmapFactory.decodeStream(input) }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun wrapLines(text: String, paint: Paint, width: Float): List<String> {
        if (text.isBlank()) return emptyList()
        val output = mutableListOf<String>()
        text.replace("\r", "").split("\n").forEach { paragraph ->
            if (paragraph.isBlank()) {
                output.add("")
                return@forEach
            }
            val words = paragraph.trim().split(Regex("\\s+"))
            var line = ""
            words.forEach { word ->
                val candidate = if (line.isBlank()) word else "$line $word"
                if (paint.measureText(candidate) <= width || line.isBlank()) {
                    line = candidate
                } else {
                    output.add(line)
                    line = word
                }
            }
            if (line.isNotBlank()) output.add(line)
        }
        return output
    }

    private fun paint(size: Float, color: Int, bold: Boolean): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = size
        typeface = if (bold) Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD) else Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        isSubpixelText = true
    }

    private fun withAlpha(color: Int, alpha: Int): Int = Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))

    private fun serviceTitle(tipo: String): String = when (tipo.uppercase(Locale.ITALY)) {
        "SICUREZZA" -> "SICUREZZA SUL LAVORO"
        "HACCP" -> "IGIENE E SICUREZZA ALIMENTARE - HACCP"
        "GDPR" -> "PROTEZIONE DEI DATI PERSONALI - GDPR"
        else -> tipo.uppercase(Locale.ITALY)
    }

    private fun esitoColor(esito: String): Int = when (esito) {
        "CONFORME" -> GREEN
        "NON_CONFORME" -> RED
        "NON_APPLICABILE" -> MID
        else -> ORANGE
    }

    private fun labelEsito(esito: String): String = when (esito) {
        "CONFORME" -> "Conforme"
        "NON_CONFORME" -> "Non conforme"
        "NON_APPLICABILE" -> "Non applicabile"
        else -> "Da verificare"
    }

    private fun labelPriorita(value: String): String = when (value.uppercase(Locale.ITALY)) {
        "ALTA" -> "Alta"
        "BASSA" -> "Bassa"
        else -> "Media"
    }

    private fun labelStatoNc(value: String): String = when (value.uppercase(Locale.ITALY)) {
        "IN_CORSO", "IN CORSO" -> "In corso"
        "RISOLTA" -> "Risolta"
        "CHIUSA" -> "Chiusa"
        else -> "Aperta"
    }

    private fun labelEfficacia(value: String): String = when (value.uppercase(Locale.ITALY)) {
        "EFFICACE" -> "Efficace"
        "NON_EFFICACE", "NON EFFICACE" -> "Non efficace"
        else -> "Da verificare"
    }

    private fun buildConclusion(conf: Int, nc: Int, na: Int, dv: Int, nonConformita: List<NonConformita>): String {
        val base = "Il sopralluogo ha registrato $conf verifiche conformi, $nc non conformi, $na non applicabili e $dv da verificare."
        val ncPart = when {
            nc == 0 -> " Non sono state rilevate non conformità nelle verifiche registrate."
            nc == 1 -> " È stata rilevata 1 non conformità, per la quale sono riportate le azioni correttive e gli eventuali termini di adeguamento."
            else -> " Sono state rilevate $nc non conformità, per le quali sono riportate le azioni correttive e gli eventuali termini di adeguamento."
        }
        val pending = nonConformita.count { it.stato != "CHIUSA" }
        val pendingPart = if (pending > 0) " Alla data di emissione risultano ancora aperte o in gestione $pending non conformità." else if (nonConformita.isNotEmpty()) " Alla data di emissione tutte le non conformità registrate risultano chiuse." else ""
        val verifyPart = if (dv > 0) " Le voci 'Da verificare' non costituiscono un giudizio di conformità e richiedono ulteriore riscontro." else ""
        return base + ncPart + pendingPart + verifyPart
    }
}
