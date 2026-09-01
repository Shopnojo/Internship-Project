package com.internship.classai.data.model

data class Due(
    val id: Int,
    val studentId: Int,
    val month: String,
    val dueDate: String,
    val payableAmount: Int,
    val penalty: Int,
    val waiver: Int,
    val netAmount: Int
) {
    // Compatibility alias for existing UI code.
    // netAmount comes directly from the database via FastAPI.
    val totalAmount: Int
        get() = netAmount
}