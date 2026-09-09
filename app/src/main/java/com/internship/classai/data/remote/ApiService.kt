package com.internship.classai.data.remote

import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.Due
import com.internship.classai.data.model.EmployeeCreate
import com.internship.classai.data.model.PaymentRequest
import com.internship.classai.data.model.PaymentResponse
import com.internship.classai.data.model.School
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import com.internship.classai.data.model.Employee
import com.internship.classai.data.model.AdminCreate
import com.internship.classai.data.model.LoginRequest
import com.internship.classai.data.model.LoginResponse

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("students")
    suspend fun getStudents(): List<Student>

    @GET("classes")
    suspend fun getClasses(): List<ClassItem>

    @GET("sections")
    suspend fun getSections(): List<SectionItem>

    @GET("dues/{student_id}")
    suspend fun getStudentDues(
        @Path("student_id") studentId: Int
    ): List<Due>

    @POST("payments")
    suspend fun makePayment(
        @Body request: PaymentRequest
    ): PaymentResponse

    @GET("schools")
    suspend fun getSchools(): List<School>

    @GET("employees")
    suspend fun getEmployees(): List<Employee>

    @POST("employees")
    suspend fun createEmployee(
        @Body request: EmployeeCreate
    ): Map<String, Any>

    @POST("admins")
    suspend fun createAdmin(
        @Body request: AdminCreate
    ): Map<String, Any>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}