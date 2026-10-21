package com.example.smartclassattendance

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


// ========================================
// SINGLE STUDENT RESPONSE
// ========================================

data class StudentResponse(
    val success: Boolean,
    val student: StudentData?,
    val message: String?
)


// ========================================
// ALL STUDENTS RESPONSE
// ========================================

data class StudentsResponse(
    val success: Boolean,
    val students: List<StudentData>?,
    val message: String?
)


// ========================================
// STUDENT DATA
// ========================================

data class StudentData(
    val roll_no: Int,
    val registration_no: String?,
    val student_name: String?,
    val course_branch: String?
)


// ========================================
// REGISTER STUDENT REQUEST
// ========================================

data class RegisterStudentRequest(
    val roll_no: Int,
    val registration_no: String,
    val student_name: String,
    val course_branch: String
)


// ========================================
// REGISTER STUDENT RESPONSE
// ========================================

data class RegisterStudentResponse(
    val success: Boolean,
    val message: String?
)


// ========================================
// MARK ATTENDANCE REQUEST
// ========================================

data class MarkAttendanceRequest(
    val roll_no: Int,
    val registration_no: String,
    val subject: String,
    val attendance_date: String,
    val attendance_time: String,
    val status: String,
    val latitude: Double,
    val longitude: Double
)


// ========================================
// MARK ATTENDANCE RESPONSE
// ========================================

data class MarkAttendanceResponse(
    val success: Boolean,
    val message: String?
)


// ========================================
// API SERVICE
// ========================================

interface ApiService {

    // Get one student

    @GET("get_student.php")
    fun getStudent(
        @Query("roll_no") rollNo: Int
    ): Call<StudentResponse>


    // Get all students

    @GET("get_students.php")
    fun getStudents(): Call<StudentsResponse>


    // Register student

    @POST("register_student.php")
    fun registerStudent(
        @Body request: RegisterStudentRequest
    ): Call<RegisterStudentResponse>


    // Mark attendance

    @POST("mark_attendance.php")
    fun markAttendance(
        @Body request: MarkAttendanceRequest
    ): Call<MarkAttendanceResponse>
}