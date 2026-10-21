package com.example.smartclassattendance

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class TakeAttendanceActivity : ComponentActivity() {

    private val classroomLatitude = 25.000000
    private val classroomLongitude = 85.000000
    private val allowedRadius = 50f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Location permission
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }

        setContent {
            TakeAttendanceScreen()
        }
    }

    @Composable
    fun TakeAttendanceScreen() {

        // =========================================
        // ROLL NUMBER
        // =========================================

        var rollNo by remember {
            mutableStateOf("")
        }

        // =========================================
        // STUDENT DATA
        // =========================================

        var student by remember {
            mutableStateOf<StudentData?>(null)
        }

        var loadingStudent by remember {
            mutableStateOf(false)
        }

        // =========================================
        // SUBJECT
        // =========================================

        var selectedSubject by remember {
            mutableStateOf("")
        }

        var subjectExpanded by remember {
            mutableStateOf(false)
        }

        val subjects = listOf(
            "Adv. Java",
            "DC & CN",
            "SE",
            "IoT"
        )

        // =========================================
        // LOCATION
        // =========================================

        var locationVerified by remember {
            mutableStateOf(false)
        }

        var locationMessage by remember {
            mutableStateOf("Location not checked")
        }

        // =========================================
        // ATTENDANCE
        // =========================================

        var attendanceMessage by remember {
            mutableStateOf("")
        }

        var markingAttendance by remember {
            mutableStateOf(false)
        }

        // =========================================
        // DATE & TIME
        // =========================================

        val currentDate = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).format(Date())

        val currentTime = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        // =========================================
        // MAIN UI
        // =========================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // =====================================
            // TITLE
            // =====================================

            Text(
                text = "TAKE ATTENDANCE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            // =====================================
            // ROLL NUMBER
            // =====================================

            OutlinedTextField(
                value = rollNo,
                onValueChange = {

                    rollNo = it

                    // New roll number means
                    // previous student is cleared

                    student = null
                    attendanceMessage = ""
                    locationVerified = false
                    locationMessage = "Location not checked"
                },
                label = {
                    Text("Enter Roll Number")
                },
                placeholder = {
                    Text("Example: 1")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================
            // FIND STUDENT BUTTON
            // =====================================

            Button(
                enabled = !loadingStudent,
                onClick = {

                    val rollNumber =
                        rollNo.trim().toIntOrNull()

                    if (rollNumber == null || rollNumber <= 0) {

                        Toast.makeText(
                            this@TakeAttendanceActivity,
                            "Please enter a valid Roll Number",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    loadingStudent = true
                    student = null
                    attendanceMessage = ""

                    // =================================
                    // GET STUDENT FROM CENTRAL SERVER
                    // =================================

                    RetrofitClient.api
                        .getStudent(rollNumber)
                        .enqueue(
                            object :
                                Callback<StudentResponse> {

                                override fun onResponse(
                                    call: Call<StudentResponse>,
                                    response: Response<StudentResponse>
                                ) {

                                    loadingStudent = false

                                    if (
                                        response.isSuccessful &&
                                        response.body()?.success == true &&
                                        response.body()?.student != null
                                    ) {

                                        student =
                                            response.body()!!.student

                                        Toast.makeText(
                                            this@TakeAttendanceActivity,
                                            "Student found ✓",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        student = null

                                        val message =
                                            response.body()?.message
                                                ?: "Student not found"

                                        Toast.makeText(
                                            this@TakeAttendanceActivity,
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<StudentResponse>,
                                    t: Throwable
                                ) {

                                    loadingStudent = false
                                    student = null

                                    Toast.makeText(
                                        this@TakeAttendanceActivity,
                                        "Server connection failed: ${t.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text =
                        if (loadingStudent)
                            "CHECKING..."
                        else
                            "CHECK STUDENT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            // =====================================
            // STUDENT INFORMATION
            // =====================================

            if (student != null) {

                val currentStudent =
                    student!!

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text =
                                "Roll Number: " +
                                        currentStudent.roll_no,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Registration Number: " +
                                        (
                                                currentStudent.registration_no
                                                    ?: "Not registered"
                                                )
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Student Name: " +
                                        (
                                                currentStudent.student_name
                                                    ?: "Not registered"
                                                )
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Course / Branch: " +
                                        (
                                                currentStudent.course_branch
                                                    ?: "Not registered"
                                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(15.dp)
                )
            }

            // =====================================
            // SUBJECT
            // =====================================

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    enabled = student != null,
                    onClick = {
                        subjectExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            if (selectedSubject.isEmpty())
                                "Select Subject"
                            else
                                selectedSubject
                    )
                }

                DropdownMenu(
                    expanded = subjectExpanded,
                    onDismissRequest = {
                        subjectExpanded = false
                    }
                ) {

                    subjects.forEach { subjectName ->

                        DropdownMenuItem(
                            text = {
                                Text(subjectName)
                            },
                            onClick = {

                                selectedSubject =
                                    subjectName

                                subjectExpanded =
                                    false

                                attendanceMessage = ""
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================
            // DATE
            // =====================================

            Text(
                text = "Date: $currentDate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // =====================================
            // TIME
            // =====================================

            Text(
                text = "Time: $currentTime",
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================
            // CHECK LOCATION
            // =====================================

            Button(
                enabled = student != null,
                onClick = {

                    checkLocation { inside, distance ->

                        locationVerified = inside

                        locationMessage =
                            if (inside) {

                                "Location Verified ✓\n" +
                                        "Distance: " +
                                        "${distance.toInt()} meters"

                            } else {

                                "Outside Classroom ✗\n" +
                                        "Distance: " +
                                        "${distance.toInt()} meters"
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "CHECK LOCATION",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================
            // LOCATION MESSAGE
            // =====================================

            Text(
                text = locationMessage,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color =
                    if (locationVerified)
                        Color(0xFF16A34A)
                    else
                        Color.Red
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // =====================================
            // ATTENDANCE MESSAGE
            // =====================================

            if (attendanceMessage.isNotEmpty()) {

                Text(
                    text = attendanceMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }

            // =====================================
            // MARK ATTENDANCE
            // =====================================

            Button(
                enabled =
                    student != null &&
                            selectedSubject.isNotEmpty() &&
                            locationVerified &&
                            !markingAttendance,

                onClick = {

                    val currentStudent =
                        student!!

                    val registrationNumber =
                        currentStudent.registration_no

                    // =================================
                    // REGISTRATION CHECK
                    // =================================

                    if (registrationNumber.isNullOrBlank()) {

                        Toast.makeText(
                            this@TakeAttendanceActivity,
                            "Registration data not available",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    // =================================
                    // ATTENDANCE REQUEST
                    // =================================

                    val request =
                        MarkAttendanceRequest(

                            roll_no =
                                currentStudent.roll_no,

                            registration_no =
                                registrationNumber,

                            subject =
                                selectedSubject,

                            attendance_date =
                                currentDate,

                            attendance_time =
                                currentTime,

                            status =
                                "Present",

                            latitude =
                                classroomLatitude,

                            longitude =
                                classroomLongitude
                        )

                    markingAttendance = true

                    // =================================
                    // SEND TO SERVER
                    // =================================

                    RetrofitClient.api
                        .markAttendance(request)
                        .enqueue(
                            object :
                                Callback<MarkAttendanceResponse> {

                                override fun onResponse(
                                    call:
                                    Call<MarkAttendanceResponse>,

                                    response:
                                    Response<MarkAttendanceResponse>
                                ) {

                                    markingAttendance =
                                        false

                                    if (
                                        response.isSuccessful &&
                                        response.body()?.success == true
                                    ) {

                                        attendanceMessage =
                                            "Attendance Marked Successfully ✓"

                                        Toast.makeText(
                                            this@TakeAttendanceActivity,
                                            "Attendance Marked Successfully ✓",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    } else {

                                        val message =
                                            response.body()?.message
                                                ?: "Attendance could not be marked"

                                        attendanceMessage =
                                            message

                                        Toast.makeText(
                                            this@TakeAttendanceActivity,
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call:
                                    Call<MarkAttendanceResponse>,

                                    t: Throwable
                                ) {

                                    markingAttendance =
                                        false

                                    attendanceMessage =
                                        "Server connection failed"

                                    Toast.makeText(
                                        this@TakeAttendanceActivity,
                                        "Server connection failed: ${t.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {

                Text(
                    text =
                        if (markingAttendance)
                            "MARKING..."
                        else
                            "MARK ATTENDANCE",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =====================================
            // BACK
            // =====================================

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
                    fontSize = 17.sp
                )
            }
        }
    }

    // =================================================
    // LOCATION CHECK
    // =================================================

    private fun checkLocation(
        result: (Boolean, Float) -> Unit
    ) {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                this,
                "Location permission required",
                Toast.LENGTH_SHORT
            ).show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )

            return
        }

        val locationManager =
            getSystemService(
                LOCATION_SERVICE
            ) as LocationManager

        val listener =
            object : LocationListener {

                override fun onLocationChanged(
                    location: Location
                ) {

                    val distance =
                        FloatArray(1)

                    Location.distanceBetween(
                        location.latitude,
                        location.longitude,
                        classroomLatitude,
                        classroomLongitude,
                        distance
                    )

                    val inside =
                        distance[0] <= allowedRadius

                    result(
                        inside,
                        distance[0]
                    )

                    locationManager.removeUpdates(
                        this
                    )
                }

                override fun onProviderDisabled(
                    provider: String
                ) {

                    Toast.makeText(
                        this@TakeAttendanceActivity,
                        "Please turn ON location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        try {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                0f,
                listener,
                Looper.getMainLooper()
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Unable to get current location",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}