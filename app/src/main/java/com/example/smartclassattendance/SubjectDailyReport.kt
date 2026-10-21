package com.example.smartclassattendance

data class SubjectDailyReport(
    val date: String,
    val subject: String,
    val present: Int,
    val absent: Int,
    val total: Int
)