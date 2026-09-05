package com.abdellahshabat.fatora.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.abdellahshabat.fatora.RecentTransactionItem
import com.abdellahshabat.fatora.TransactionDirection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * تصدير سجل عمليات عميل واحد كملف PDF بسيط (صفحة A4 وحدة)، وحفظه مباشرة
 * بمجلد التنزيلات عبر MediaStore - بدون الحاجة لأي صلاحيات تخزين خاصة
 * على أندرويد 10+ (Scoped Storage).
 *
 * ملاحظة: الرسم هون بسيط جداً (نص عادي بـ Canvas) - كافي لمرحلة الـ MVP.
 * لو احتجنا تصميم PDF أنيق أكتر بالمستقبل (شعار، جدول حقيقي، صفحات متعددة)
 * هاد المكان الوحيد يلي بده يتعدّل.
 */
object CustomerPdfExporter {

    fun exportCustomerHistory(
        context: Context,
        customerName: String,
        balance: Double,
        transactions: List<RecentTransactionItem>
    ): Uri? {
        val pdfDocument = PdfDocument()

        // أبعاد A4 تقريبية بالنقاط (72 نقطة = إنش واحد)
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val rightMargin = (pageWidth - 40).toFloat()

        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        val textPaint = Paint().apply {
            textSize = 14f
            textAlign = Paint.Align.RIGHT
        }
        val grayPaint = Paint().apply {
            textSize = 11f
            color = Color.GRAY
            textAlign = Paint.Align.RIGHT
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 50f

        canvas.drawText("سجل عمليات: $customerName", rightMargin, y, titlePaint)
        y += 30f

        canvas.drawText("الرصيد الحالي: ${balance.toCleanString()} ₪", rightMargin, y, textPaint)
        y += 20f

        val generatedAt = SimpleDateFormat("d/M/yyyy - h:mm a", Locale("ar")).format(Date())
        canvas.drawText("تاريخ الإصدار: $generatedAt", rightMargin, y, grayPaint)
        y += 30f

        canvas.drawLine(40f, y, pageWidth - 40f, y, linePaint)
        y += 25f

        if (transactions.isEmpty()) {
            canvas.drawText("لا يوجد عمليات مسجلة لهذا العميل", rightMargin, y, textPaint)
        } else {
            val maxY = pageHeight - 50f

            for ((index, item) in transactions.withIndex()) {
                if (y > maxY) {
                    // بمرحلة الـ MVP بنكتفي بصفحة وحدة. لو عدد العمليات كبير
                    // ورح يتجاوز صفحة، هاد المكان يلي لازم نضيف فيه صفحات إضافية لاحقاً.
                    canvas.drawText("... والمزيد (تجاوز حد الصفحة الواحدة)", rightMargin, y, grayPaint)
                    break
                }

                val sign = if (item.direction == TransactionDirection.DEBT) "+" else "-"
                val line = "${index + 1}.  ${item.label}   $sign${item.amount.toCleanString()} ${item.currency}"
                canvas.drawText(line, rightMargin, y, textPaint)
                y += 22f
            }
        }

        pdfDocument.finishPage(page)

        val fileName = "fatora_${customerName}_${System.currentTimeMillis()}.pdf"
        val savedUri = savePdfToDownloads(context, pdfDocument, fileName)

        pdfDocument.close()

        return savedUri
    }

    private fun savePdfToDownloads(
        context: Context,
        pdfDocument: PdfDocument,
        fileName: String
    ): Uri? {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Files.getContentUri("external")
        }

        val itemUri = resolver.insert(collectionUri, contentValues) ?: return null

        resolver.openOutputStream(itemUri)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }

        return itemUri
    }

    private fun Double.toCleanString(): String {
        return if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
    }
}