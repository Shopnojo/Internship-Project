package com.internship.classai.data.model

data class Student(

    val id: Int,

    val studentId: String,

    val name: String,

    val classId: Int,

    val sectionId: Int,

    val image: String? = null

)