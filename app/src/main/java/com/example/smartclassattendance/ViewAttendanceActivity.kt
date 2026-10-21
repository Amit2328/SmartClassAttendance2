package com.example.smartclassattendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ViewAttendanceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ViewAttendanceScreen()
        }
    }

    @Composable
    fun ViewAttendanceScreen() {

        // Temporary empty list.
        // Server/MySQL attendance will be connected next.
        var attendanceList by remember {
            mutableStateOf(emptyList<Attendance>())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Text(
                text = "VIEW ATTENDANCE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            if (attendanceList.isEmpty()) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No attendance records found",
                        fontSize = 17.sp,
                        color = Color.Gray
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(attendanceList) { attendance ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(15.dp)
                            ) {

                                Text(
                                    text = "Roll No: ${attendance.roll_no}",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Registration No: ${attendance.registration_no}"
                                )

                                Text(
                                    text = "Subject: ${attendance.subject}"
                                )

                                Text(
                                    text = "Date: ${attendance.attendance_date}"
                                )

                                Text(
                                    text = "Time: ${attendance.attendance_time}"
                                )

                                Text(
                                    text = "Status: ${attendance.status}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (
                                        attendance.status == "Present"
                                    ) {
                                        Color(0xFF16A34A)
                                    } else {
                                        Color.Red
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    // Server API connection will be added here.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "REFRESH",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Button(
                onClick = {
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "BACK",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
