package com.internship.classai.utils

import com.internship.classai.data.model.PaymentRecord

object ReceiptFormatter {

    fun format(payment: PaymentRecord): String {

        val sb = StringBuilder()

        sb.append(center("CLASSAI PUBLIC SCHOOL"))
        sb.append("\n")

        sb.append(center("School Address"))
        sb.append("\n")

        sb.append(center("Contact Number"))
        sb.append("\n")

        sb.append("--------------------------------\n")

        sb.append("Receipt No : ${payment.receiptNumber}\n")
        sb.append("Date       : ${payment.paymentDate}\n")

        sb.append("--------------------------------\n")

        sb.append("Student    : ${payment.studentName}\n")
        sb.append("Student ID : ${payment.studentId}\n")
        sb.append("Class      : ${payment.className}\n")
        sb.append("Section    : ${payment.sectionName}\n")

        sb.append("--------------------------------\n")

        sb.append("Fee Month  : ${payment.month}\n")
        sb.append("Amount     : ₹${payment.amountPaid}\n")
        sb.append("Method     : ${payment.paymentMethod}\n")

        sb.append("--------------------------------\n")

        sb.append(center("TOTAL PAID"))
        sb.append("\n")
        sb.append(center("₹${payment.amountPaid}"))
        sb.append("\n")

        sb.append("--------------------------------\n")

        sb.append(center("Thank You"))
        sb.append("\n")

        sb.append(center("Visit Again"))
        sb.append("\n\n\n")

        return sb.toString()

    }

    private fun center(text: String): String {

        val width = 32

        if (text.length >= width)
            return text

        val padding = (width - text.length) / 2

        return " ".repeat(padding) + text

    }

}