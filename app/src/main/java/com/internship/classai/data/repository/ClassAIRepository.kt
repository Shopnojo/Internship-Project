package com.internship.classai.data.repository

import com.internship.classai.data.model.PaymentRequest
import com.internship.classai.data.remote.ApiService

class ClassAIRepository(
    private val apiService: ApiService
) {

    suspend fun getStudents() =
        apiService.getStudents()

    suspend fun getClasses() =
        apiService.getClasses()

    suspend fun getSections() =
        apiService.getSections()

    suspend fun getStudentDues(studentId: Int) =
        apiService.getStudentDues(studentId)

    suspend fun makePayment(
        dueId: Int,
        paymentMode: String,
        transactionNo: String?,
        remarks: String?
    ) =
        apiService.makePayment(
            PaymentRequest(
                due_id = dueId,
                payment_mode = paymentMode,
                transaction_no = transactionNo,
                remarks = remarks
            )
        )
}