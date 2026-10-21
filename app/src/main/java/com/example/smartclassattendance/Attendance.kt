package com.example.smartclassattendance

data class Attendance(
    val id: String,
    val roll_no: String,
    val registration_no: String,
    val subject: String,
    val attendance_date: String,
    val attendance_time: String,
    val status: String,
    val latitude: String,
    val longitude: String
)