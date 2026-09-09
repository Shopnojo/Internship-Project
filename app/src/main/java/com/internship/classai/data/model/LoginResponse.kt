package com.internship.classai.data.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: String? = null,
    val role: String? = null,
    val fullName: String? = null
)