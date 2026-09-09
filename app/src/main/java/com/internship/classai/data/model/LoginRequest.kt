package com.internship.classai.data.model

data class LoginRequest(
    val userId: String,
    val password: String,
    val role: String
)