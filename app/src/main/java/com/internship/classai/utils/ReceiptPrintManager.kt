package com.internship.classai.utils

import android.content.Context
import com.internship.classai.data.model.PaymentRecord

object ReceiptPrintManager {

    fun printReceipt(

        context: Context,

        payment: PaymentRecord

    ): Boolean {

        return try {

            val receiptText = ReceiptFormatter.format(payment)

            BluetoothPrinter.print(

                context = context,

                text = receiptText

            )

            true

        } catch (e: Exception) {

            android.util.Log.e("PRINT_ERROR", "Printing failed", e)

            e.printStackTrace()

            false

        }

    }

}