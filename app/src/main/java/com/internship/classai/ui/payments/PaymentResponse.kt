package com.internship.classai.data.model

data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val due_id: Int? = null,
    val student_id: Int? = null,
    val transaction_no: String? = null,
    val amount: Int? = null,
    val penalty: Int? = null,
    val waiver: Int? = null,
    val net_amount: Int? = null,
    val payment_date: String? = null,
    val payment_mode: String? = null
)