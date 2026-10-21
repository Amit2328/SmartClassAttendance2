package com.example.smartclassattendance

data class DailyAttendanceReport(
    val date: String,
    val present: Int,
    val absent: Int,
    val total: Int
)