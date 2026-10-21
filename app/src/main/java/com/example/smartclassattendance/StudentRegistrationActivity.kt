package com.example.smartclassattendance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentRegistrationActivity : ComponentActivity() {

    @Composable
    fun StudentRegistrationScreen() {

        var rollNo by remember { mutableStateOf("") }
        var registrationNo by remember { mutableStateOf("") }
        var studentName by remember { mutableStateOf("") }
        var courseBranch by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "STUDENT REGISTRATION",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Text(
                text = "Register Student",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 30.dp
                )
            )

            OutlinedTextField(
                value = rollNo,
                onValueChange = { rollNo = it },
                label = { Text("Roll Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = registrationNo,
                onValueChange = { registrationNo = it },
                label = { Text("Registration Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Student Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OutlinedTextField(
                value = courseBranch,
                onValueChange = { courseBranch = it },
                label = { Text("Course / Branch") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Button(
                onClick = {

                    if (
                        rollNo.isBlank() ||
                        registrationNo.isBlank() ||
                        studentName.isBlank() ||
                        courseBranch.isBlank()
                    ) {
                        Toast.makeText(
                            this@StudentRegistrationActivity,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val rollNumber = rollNo.toIntOrNull()

                    if (rollNumber == null) {
                        Toast.makeText(
                            this@StudentRegistrationActivity,
                            "Roll Number must be a number",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val request = RegisterStudentRequest(
                        roll_no = rollNumber,
                        registration_no = registrationNo.trim(),
                        student_name = studentName.trim(),
                        course_branch = courseBranch.trim()
                    )

                    Toast.makeText(
                        this@StudentRegistrationActivity,
                        "Registering...",
                        Toast.LENGTH_SHORT
                    ).show()

                    RetrofitClient.api
                        .registerStudent(request)
                        .enqueue(object : Callback<RegisterStudentResponse> {

                            override fun onResponse(
                                call: Call<RegisterStudentResponse>,
                                response: Response<RegisterStudentResponse>
                            ) {

                                if (
                                    response.isSuccessful &&
                                    response.body()?.success == true
                                ) {

                                    Toast.makeText(
                                        this@StudentRegistrationActivity,
                                        "Student Registered Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    rollNo = ""
                                    registrationNo = ""
                                    studentName = ""
                                    courseBranch = ""

                                } else {

                                    val errorMessage =
                                        response.body()?.message
                                            ?: response.errorBody()?.string()
                                            ?: "Registration failed"

                                    Toast.makeText(
                                        this@StudentRegistrationActivity,
                                        "Server Error:\n$errorMessage",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<RegisterStudentResponse>,
                                t: Throwable
                            ) {

                                Toast.makeText(
                                    this@StudentRegistrationActivity,
                                    "ERROR: ${t.javaClass.simpleName}\n${t.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(top = 5.dp)
            ) {

                Text(
                    text = "REGISTER STUDENT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(top = 12.dp)
            ) {

                Text(
                    text = "BACK",
                    fontSize = 17.sp
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudentRegistrationScreen()
        }
    }
}