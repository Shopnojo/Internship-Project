package com.internship.classai.data.model

data class Due(

    val id: Int,

    val studentId: Int,

    val month: String,

    val dueDate: String,

    val payableAmount: Int,

    val penalty: Int,

    val waiver: Int

) {

    val totalAmount: Int
        get() = payableAmount + penalty - waiver

}