package com.internship.classai.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.internship.classai.R
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

object BluetoothPrinter {

    private val printerUUID: UUID =
        UUID.fromString(
            "00001101-0000-1000-8000-00805F9B34FB"
        )

    private const val PAPER_WIDTH = 384

    fun print(
        context: Context,
        text: String
    ) {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException(
                "BLUETOOTH_CONNECT permission not granted."
            )
        }

        val adapter = BluetoothAdapter.getDefaultAdapter()
            ?: throw Exception("Bluetooth not supported.")

        if (!adapter.isEnabled) {
            throw Exception("Bluetooth is disabled.")
        }

        val pairedDevices = adapter.bondedDevices

        if (pairedDevices.isEmpty()) {
            throw Exception("No paired printer found.")
        }

        val printer = findPrinter(pairedDevices)
            ?: throw Exception("No thermal printer found.")

        adapter.cancelDiscovery()

        val socket =
            printer.createRfcommSocketToServiceRecord(
                printerUUID
            )

        try {

            socket.connect()

            Thread.sleep(300)

            val outputStream = socket.outputStream

            //==================================================
            // CREATE COMPLETE RECEIPT AS ONE IMAGE
            //==================================================

            val receiptBitmap = createReceiptBitmap(
                context = context,
                text = text
            )

            //==================================================
            // INITIALIZE PRINTER
            //==================================================

            outputStream.write(
                byteArrayOf(
                    0x1B,
                    0x40
                )
            )

            Thread.sleep(100)

            //==================================================
            // CENTER ALIGN
            //==================================================

            outputStream.write(
                byteArrayOf(
                    0x1B,
                    0x61,
                    0x01
                )
            )

            //==================================================
            // PRINT RECEIPT IMAGE
            //==================================================

            printBitmap(
                outputStream = outputStream,
                bitmap = receiptBitmap
            )

            //==================================================
            // FEED PAPER
            //==================================================

            outputStream.write(
                byteArrayOf(
                    0x1B,
                    0x64,
                    0x04
                )
            )

            outputStream.flush()

            receiptBitmap.recycle()

        } finally {

            try {
                socket.close()
            } catch (_: IOException) {
            }
        }
    }

    //==========================================================
    // CREATE COMPLETE RECEIPT BITMAP
    //==========================================================

    private fun createReceiptBitmap(
        context: Context,
        text: String
    ): Bitmap {

        val lines = text
            .replace("\r", "")
            .split("\n")

        val logo = drawableToBitmap(
            context = context,
            drawableId = R.drawable.classai_logo,
            targetWidth = 220
        )

        val lineHeight = 25
        val topPadding = 20
        val bottomPadding = 30
        val logoGap = 15

        val textHeight =
            lines.size * lineHeight

        val totalHeight =
            topPadding +
                    logo.height +
                    logoGap +
                    textHeight +
                    bottomPadding

        val bitmap = Bitmap.createBitmap(
            PAPER_WIDTH,
            totalHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        // White background
        canvas.drawColor(Color.WHITE)

        //==================================================
        // PAINT
        //==================================================

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f
            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.NORMAL
            )
            textAlign = Paint.Align.CENTER
        }

        //==================================================
        // LOGO
        //==================================================

        val logoLeft =
            (PAPER_WIDTH - logo.width) / 2f

        canvas.drawBitmap(
            logo,
            logoLeft,
            topPadding.toFloat(),
            paint
        )

        //==================================================
        // RECEIPT TEXT
        //==================================================

        var y =
            topPadding +
                    logo.height +
                    logoGap +
                    18

        for (line in lines) {

            val trimmedLine = line.trimEnd()

            if (trimmedLine.isNotEmpty()) {

                val isSeparator =
                    trimmedLine.all {
                        it == '-' ||
                                it == '=' ||
                                it == '_'
                    }

                if (isSeparator) {

                    paint.textSize = 16f
                    paint.typeface =
                        Typeface.DEFAULT

                } else {

                    paint.textSize = 20f

                    paint.typeface =
                        if (
                            trimmedLine.contains(
                                "TOTAL PAID",
                                ignoreCase = true
                            )
                        ) {
                            Typeface.DEFAULT_BOLD
                        } else {
                            Typeface.DEFAULT
                        }
                }

                canvas.drawText(
                    trimmedLine,
                    PAPER_WIDTH / 2f,
                    y.toFloat(),
                    paint
                )
            }

            y += lineHeight
        }

        return bitmap
    }

    //==========================================================
    // DRAWABLE → BITMAP
    //==========================================================

    private fun drawableToBitmap(
        context: Context,
        drawableId: Int,
        targetWidth: Int
    ): Bitmap {

        val drawable: Drawable =
            ContextCompat.getDrawable(
                context,
                drawableId
            ) ?: throw Exception(
                "Unable to load ClassAI logo."
            )

        val intrinsicWidth =
            if (drawable.intrinsicWidth > 0) {
                drawable.intrinsicWidth
            } else {
                targetWidth
            }

        val intrinsicHeight =
            if (drawable.intrinsicHeight > 0) {
                drawable.intrinsicHeight
            } else {
                targetWidth
            }

        val targetHeight =
            (
                    targetWidth.toFloat() *
                            intrinsicHeight.toFloat() /
                            intrinsicWidth.toFloat()
                    ).toInt()

        val bitmap = Bitmap.createBitmap(
            targetWidth,
            targetHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        drawable.setBounds(
            0,
            0,
            targetWidth,
            targetHeight
        )

        drawable.draw(canvas)

        return bitmap
    }

    //==========================================================
    // PRINT BITMAP IN SMALL ESC/POS CHUNKS
    //==========================================================

    private fun printBitmap(
        outputStream: OutputStream,
        bitmap: Bitmap
    ) {

        val width = bitmap.width

        // Keep every raster section small and compatible.
        val chunkHeight = 24

        var top = 0

        while (top < bitmap.height) {

            val height =
                minOf(
                    chunkHeight,
                    bitmap.height - top
                )

            val widthBytes =
                (width + 7) / 8

            val imageData =
                ByteArray(
                    widthBytes * height
                )

            var index = 0

            for (y in 0 until height) {

                for (xByte in 0 until widthBytes) {

                    var byteValue = 0

                    for (bit in 0..7) {

                        val x =
                            xByte * 8 + bit

                        if (x >= width) {
                            continue
                        }

                        val pixel =
                            bitmap.getPixel(
                                x,
                                top + y
                            )

                        val red =
                            Color.red(pixel)

                        val green =
                            Color.green(pixel)

                        val blue =
                            Color.blue(pixel)

                        val alpha =
                            Color.alpha(pixel)

                        val brightness =
                            (
                                    red +
                                            green +
                                            blue
                                    ) / 3

                        val isBlack =
                            alpha > 100 &&
                                    brightness < 160

                        if (isBlack) {

                            byteValue =
                                byteValue or
                                        (
                                                1 shl
                                                        (7 - bit)
                                                )
                        }
                    }

                    imageData[index] =
                        byteValue.toByte()

                    index++
                }
            }

            //==================================================
            // GS v 0
            //==================================================

            val command =
                ByteArray(
                    8 + imageData.size
                )

            command[0] = 0x1D
            command[1] = 0x76
            command[2] = 0x30
            command[3] = 0x00

            command[4] =
                (widthBytes and 0xFF).toByte()

            command[5] =
                ((widthBytes shr 8) and 0xFF).toByte()

            command[6] =
                (height and 0xFF).toByte()

            command[7] =
                ((height shr 8) and 0xFF).toByte()

            System.arraycopy(
                imageData,
                0,
                command,
                8,
                imageData.size
            )

            outputStream.write(command)

            // Give the printer a moment between raster blocks.
            outputStream.flush()

            Thread.sleep(25)

            top += height
        }
    }

    //==========================================================
    // FIND PRINTER
    //==========================================================

    private fun findPrinter(
        devices: Set<BluetoothDevice>
    ): BluetoothDevice? {

        return devices.firstOrNull {
            it.name == "PSF588"
        }
    }
}