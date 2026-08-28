package com.internship.classai.data.model

data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val due_id: Int?,
    val student_id: Int?,
    val amount: Double?,
    val payment_date: String?,
    val payment_mode: String?
)