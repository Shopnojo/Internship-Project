package com.internship.classai.data.model

data class PaymentRecord(

    val receiptNumber: String,

    val transactionId: String,

    val studentId: Int,

    val studentName: String,

    val className: String,

    val sectionName: String,

    val month: String,

    val amountPaid: Int,

    val paymentMethod: String,

    val paymentDate: String,

    val remarks: String,

    val collectedBy: String,

    val timestamp: Long

)