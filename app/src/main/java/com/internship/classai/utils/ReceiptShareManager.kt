package com.internship.classai.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import com.internship.classai.R
import com.internship.classai.data.model.PaymentRecord
import java.io.File

object ReceiptShareManager {

    fun shareReceipt(
        context: Context,
        payment: PaymentRecord
    ): Boolean {

        return try {

            val receiptsDirectory = File(
                context.cacheDir,
                "receipts"
            )

            if (!receiptsDirectory.exists()) {
                receiptsDirectory.mkdirs()
            }

            val pdfFile = File(
                receiptsDirectory,
                "Receipt_${payment.receiptNumber}.pdf"
            )

            val document = android.graphics.pdf.PdfDocument()

            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo
                .Builder(
                    595,
                    842,
                    1
                )
                .create()

            val page = document.startPage(pageInfo)

            val canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            var y = 55f

            //==================================================
            // LOGO HELPER
            //==================================================

            fun drawableToBitmap(
                drawable: Drawable,
                width: Int,
                height: Int
            ): Bitmap {

                val bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )

                val drawableCanvas = Canvas(bitmap)

                drawable.setBounds(
                    0,
                    0,
                    width,
                    height
                )

                drawable.draw(drawableCanvas)

                return bitmap
            }

            //==================================================
            // HEADER LOGOS
            //==================================================

            val logoDrawable = ContextCompat.getDrawable(
                context,
                R.drawable.classai_logo
            )

            if (logoDrawable != null) {

                // Small logo on the left
                val leftLogo = drawableToBitmap(
                    logoDrawable,
                    90,
                    45
                )

                canvas.drawBitmap(
                    leftLogo,
                    50f,
                    35f,
                    paint
                )

                // Large logo on the right
                val rightLogo = drawableToBitmap(
                    logoDrawable,
                    150,
                    75
                )

                canvas.drawBitmap(
                    rightLogo,
                    395f,
                    25f,
                    paint
                )
            }

            // Leave enough space below the logos
            y = 125f

            //==================================================
            // TEXT HELPER
            //==================================================

            fun drawText(
                text: String,
                size: Float,
                bold: Boolean = false
            ) {

                paint.textSize = size

                paint.typeface = if (bold) {
                    Typeface.DEFAULT_BOLD
                } else {
                    Typeface.DEFAULT
                }

                canvas.drawText(
                    text,
                    50f,
                    y,
                    paint
                )

                y += size + 12f
            }

            fun drawDivider() {

                paint.strokeWidth = 1f

                canvas.drawLine(
                    50f,
                    y,
                    545f,
                    y,
                    paint
                )

                y += 20f
            }

            //==================================================
            // SCHOOL HEADER
            //==================================================

            drawText(
                "RCC PUBLIC SCHOOL",
                22f,
                true
            )

            drawText(
                "School Address",
                12f
            )

            drawText(
                "Contact Number",
                12f
            )

            y += 8f

            drawDivider()

            //==================================================
            // RECEIPT INFORMATION
            //==================================================

            drawText(
                "Receipt No.: ${payment.receiptNumber}",
                13f
            )

            drawText(
                "Date: ${payment.paymentDate}",
                13f
            )

            y += 5f

            drawDivider()

            //==================================================
            // STUDENT INFORMATION
            //==================================================

            drawText(
                "Student Name: ${payment.studentName}",
                13f
            )

            drawText(
                "Student ID: ${payment.studentId}",
                13f
            )

            drawText(
                "Class: ${payment.className}",
                13f
            )

            drawText(
                "Section: ${payment.sectionName}",
                13f
            )

            y += 5f

            drawDivider()

            //==================================================
            // PAYMENT INFORMATION
            //==================================================

            drawText(
                "Fee Month: ${payment.month}",
                13f
            )

            drawText(
                "Payable: ₹${payment.amountPaid}",
                13f
            )

            drawText(
                "Penalty: ₹0",
                13f
            )

            drawText(
                "Waiver: ₹0",
                13f
            )

            y += 5f

            drawDivider()

            //==================================================
            // TOTAL
            //==================================================

            drawText(
                "TOTAL PAID",
                15f,
                true
            )

            paint.textSize = 26f
            paint.typeface = Typeface.DEFAULT_BOLD

            canvas.drawText(
                "₹${payment.amountPaid}",
                50f,
                y,
                paint
            )

            y += 38f

            drawDivider()

            //==================================================
            // PAYMENT METHOD
            //==================================================

            drawText(
                "Payment Method: ${payment.paymentMethod}",
                13f
            )

            y += 15f

            drawText(
                "Thank You!",
                15f,
                true
            )

            drawText(
                "This is a computer generated receipt.",
                11f
            )

            document.finishPage(page)

            pdfFile.outputStream().use { outputStream ->
                document.writeTo(outputStream)
            }

            document.close()

            //==================================================
            // SHARE
            //==================================================

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(
                Intent.ACTION_SEND
            ).apply {

                type = "application/pdf"

                putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            val chooser = Intent.createChooser(
                shareIntent,
                "Share Receipt"
            )

            context.startActivity(chooser)

            true

        } catch (e: Exception) {

            android.util.Log.e(
                "RECEIPT_SHARE",
                "Unable to share receipt",
                e
            )

            false
        }
    }
}