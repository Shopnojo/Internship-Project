package com.internship.classai.data.remote

import com.internship.classai.data.model.Admin
import com.internship.classai.data.model.AdminCreate
import com.internship.classai.data.model.AdminStatusUpdate
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.Due
import com.internship.classai.data.model.Employee
import com.internship.classai.data.model.EmployeeCreate
import com.internship.classai.data.model.EmployeeUpdate
import com.internship.classai.data.model.LoginRequest
import com.internship.classai.data.model.LoginResponse
import com.internship.classai.data.model.PaymentRequest
import com.internship.classai.data.model.PaymentResponse
import com.internship.classai.data.model.School
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    // Existing parent-website payment endpoint.
    // The website itself creates the receipt and stores it in its own
    // public/assets/receipts directory. We intentionally do not modify it.
    @Headers(
        "Origin: https://classai.fixmyneed.in",
        "Referer: https://classai.fixmyneed.in/offline-payment"
    )
    @FormUrlEncoded
    @POST
    suspend fun submitParentPayment(
        @Url url: String,
        @Field("payment_id") paymentId: Int,
        @Field("payment_date") paymentDate: String,
        @Field("payment_mode") paymentMode: String,
        @Field("remarks") remarks: String
    ): Response<ResponseBody>

    // Read the payment back from the shared database after the parent website
    // has completed its own payment/receipt generation flow.
    @GET("payments/{payment_id}")
    suspend fun getConfirmedPayment(
        @Path("payment_id") paymentId: Int
    ): PaymentResponse

    @GET("schools")
    suspend fun getSchools(): List<School>

    @PATCH("schools/{school_id}/status")
    suspend fun updateSchoolStatus(
        @Path("school_id") schoolId: Int,
        @Body request: AdminStatusUpdate
    ): Map<String, Any>

    @GET("employees")
    suspend fun getEmployees(): List<Employee>

    @POST("employees")
    suspend fun createEmployee(
        @Body request: EmployeeCreate
    ): Map<String, Any>

    @PATCH("employees/{employee_id}")
    suspend fun updateEmployee(
        @Path("employee_id") employeeId: Int,
        @Body request: EmployeeUpdate
    ): Map<String, Any>

    @PATCH("employees/{employee_id}/status")
    suspend fun updateEmployeeStatus(
        @Path("employee_id") employeeId: Int,
        @Body request: AdminStatusUpdate
    ): Map<String, Any>

    @GET("admins")
    suspend fun getAdmins(): List<Admin>

    @POST("admins")
    suspend fun createAdmin(
        @Body request: AdminCreate
    ): Map<String, Any>

    @PATCH("admins/{admin_id}")
    suspend fun updateAdminStatus(
        @Path("admin_id") adminId: Int,
        @Body request: AdminStatusUpdate
    ): Map<String, Any>

    @GET("profile")
    suspend fun getProfile(
        @Query("user_id") userId: String,
        @Query("role") role: String
    ): Map<String, Any?>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}
