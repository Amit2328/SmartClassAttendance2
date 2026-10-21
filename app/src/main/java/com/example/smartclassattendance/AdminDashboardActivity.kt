package com.example.smartclassattendance

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AdminDashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AdminDashboardScreen()
        }
    }

    @Composable
    fun AdminDashboardScreen() {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 25.dp,
                    vertical = 30.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "ADMIN DASHBOARD",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Text(
                text = "Smart Class Attendance",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 35.dp
                )
            )

            // =========================
            // STUDENT REGISTRATION
            // =========================

            DashboardButton(
                text = "Student Registration"
            ) {

                val intent = Intent(
                    this@AdminDashboardActivity,
                    StudentRegistrationActivity::class.java
                )

                startActivity(intent)
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // TAKE ATTENDANCE
            // =========================

            DashboardButton(
                text = "Take Attendance"
            ) {

                val intent = Intent(
                    this@AdminDashboardActivity,
                    TakeAttendanceActivity::class.java
                )

                startActivity(intent)
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // VIEW ATTENDANCE
            // =========================

            DashboardButton(
                text = "View Attendance"
            ) {

                val intent = Intent(
                    this@AdminDashboardActivity,
                    ViewAttendanceActivity::class.java
                )

                startActivity(intent)
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =========================
            // ATTENDANCE REPORT
            // =========================

            DashboardButton(
                text = "Attendance Report"
            ) {

                val intent = Intent(
                    this@AdminDashboardActivity,
                    AttendanceReportActivity::class.java
                )

                startActivity(intent)
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

// Subject-wise Attendance Report
            DashboardButton("Subject-wise Report") {

                val intent = Intent(
                    this@AdminDashboardActivity,
                    SubjectAttendanceReportActivity::class.java
                )

                startActivity(intent)
            }
            // =========================
            // LOGOUT
            // =========================

            Button(
                onClick = {
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "LOGOUT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // =========================
    // DASHBOARD BUTTON
    // =========================

    @Composable
    fun DashboardButton(
        text: String,
        onClick: () -> Unit
    ) {

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {

            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}