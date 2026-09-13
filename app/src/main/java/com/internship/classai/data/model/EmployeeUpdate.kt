package com.internship.classai.data.model

data class EmployeeUpdate(
    val schoolId: Int,
    val fullName: String,
    val mobile: String,
    val userId: String,
    val password: String? = null
)
