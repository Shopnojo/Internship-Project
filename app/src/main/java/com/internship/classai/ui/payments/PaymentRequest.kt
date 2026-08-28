package com.internship.classai.data.model

data class PaymentRequest(
    val due_id: Int,
    val payment_mode: String,
    val transaction_no: String? = null,
    val remarks: String? = null
)