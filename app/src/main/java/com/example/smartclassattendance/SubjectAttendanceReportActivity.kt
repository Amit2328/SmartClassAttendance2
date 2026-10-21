package com.example.smartclassattendance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.Locale

data class SubjectStudentRow(
    val rollNo: Int,
    val studentName: String,
    val dailyStatus: Map<String, String>,
    val present: Int,
    val absent: Int,
    val total: Int,
    val percentage: Double
)

class SubjectAttendanceReportActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private val subjects = listOf(
        "Adv. Java",
        "DC & CN",
        "SE",
        "IoT"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DatabaseHelper(this)

        setContent {
            SubjectAttendanceReportScreen()
        }
    }

    @Composable
    fun SubjectAttendanceReportScreen() {

        var selectedSubject by remember {
            mutableStateOf(subjects.first())
        }

        var expanded by remember {
            mutableStateOf(false)
        }

        var rows by remember {
            mutableStateOf(
                loadSubjectReport(selectedSubject)
            )
        }

        var dates by remember {
            mutableStateOf(
                getSubjectDates(selectedSubject)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp)
        ) {

            Text(
                text = "SUBJECT ATTENDANCE REPORT",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Text(
                text = "Select Subject",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            // ==============================
            // SUBJECT DROPDOWN
            // ==============================

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = {
                        expanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = selectedSubject,
                        fontSize = 16.sp
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    subjects.forEach { subject ->

                        DropdownMenuItem(
                            text = {
                                Text(subject)
                            },
                            onClick = {

                                selectedSubject = subject

                                expanded = false

                                rows =
                                    loadSubjectReport(
                                        subject
                                    )

                                dates =
                                    getSubjectDates(
                                        subject
                                    )
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = selectedSubject,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (rows.isEmpty()) {

                Text(
                    text = "No attendance data available for $selectedSubject",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

            } else {

                // ==============================
                // ATTENDANCE SHEET
                // ==============================

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    )
                ) {

                    Column {

                        // HEADER
                        Row(
                            modifier = Modifier.height(55.dp)
                        ) {

                            Cell(
                                text = "Roll No.",
                                width = 85.dp,
                                header = true
                            )

                            Cell(
                                text = "Student Name",
                                width = 150.dp,
                                header = true
                            )

                            dates.forEach { date ->

                                Cell(
                                    text = formatDate(date),
                                    width = 120.dp,
                                    header = true
                                )
                            }

                            Cell(
                                text = "Present",
                                width = 90.dp,
                                header = true
                            )

                            Cell(
                                text = "Absent",
                                width = 90.dp,
                                header = true
                            )

                            Cell(
                                text = "Total",
                                width = 80.dp,
                                header = true
                            )

                            Cell(
                                text = "Attendance %",
                                width = 120.dp,
                                header = true
                            )
                        }

                        // STUDENT DATA
                        rows.forEach { row ->

                            Row(
                                modifier = Modifier.height(55.dp)
                            ) {

                                Cell(
                                    text = row.rollNo.toString(),
                                    width = 85.dp
                                )

                                Cell(
                                    text = row.studentName,
                                    width = 150.dp
                                )

                                dates.forEach { date ->

                                    val status =
                                        row.dailyStatus[date]
                                            ?: "Absent"

                                    Cell(
                                        text = status,
                                        width = 120.dp,
                                        textColor =
                                            if (
                                                status == "Present"
                                            ) {
                                                Color(0xFF16A34A)
                                            } else {
                                                Color.Red
                                            }
                                    )
                                }

                                Cell(
                                    text = row.present.toString(),
                                    width = 90.dp,
                                    textColor =
                                        Color(0xFF16A34A)
                                )

                                Cell(
                                    text = row.absent.toString(),
                                    width = 90.dp,
                                    textColor = Color.Red
                                )

                                Cell(
                                    text = row.total.toString(),
                                    width = 80.dp
                                )

                                Cell(
                                    text = String.format(
                                        Locale.getDefault(),
                                        "%.2f%%",
                                        row.percentage
                                    ),
                                    width = 120.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ==============================
            // EXPORT
            // ==============================

            Button(
                onClick = {

                    exportSubjectCSV(
                        selectedSubject,
                        rows,
                        dates
                    )

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "EXPORT $selectedSubject CSV",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ==============================
            // REFRESH
            // ==============================

            Button(
                onClick = {

                    rows =
                        loadSubjectReport(
                            selectedSubject
                        )

                    dates =
                        getSubjectDates(
                            selectedSubject
                        )

                    Toast.makeText(
                        this@SubjectAttendanceReportActivity,
                        "Report Refreshed",
                        Toast.LENGTH_SHORT
                    ).show()

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "REFRESH",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            // ==============================
            // BACK
            // ==============================

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
                    fontSize = 16.sp
                )
            }
        }
    }

    // =====================================================
    // LOAD SUBJECT REPORT
    // =====================================================

    private fun loadSubjectReport(
        subject: String
    ): List<SubjectStudentRow> {

        val students =
            databaseHelper.getAllStudents()

        val attendance =
            databaseHelper.getAllAttendance()

        val subjectAttendance =
            attendance.filter {

                it.subject.equals(
                    subject,
                    ignoreCase = true
                )
            }

        val dates =
            subjectAttendance
                .map { it.attendance_date }
                .distinct()
                .sorted()

        val result =
            mutableListOf<SubjectStudentRow>()

        students.forEach { student ->

            val statusMap =
                mutableMapOf<String, String>()

            var present = 0
            var absent = 0

            dates.forEach { date ->

                val record =
                    subjectAttendance.find {

                        it.roll_no == student.rollNo.toString() &&
                                it.attendance_date == date
                    }

                if (
                    record != null &&
                    record.status.equals(
                        "Present",
                        ignoreCase = true
                    )
                ) {

                    statusMap[date] = "Present"
                    present++

                } else {

                    statusMap[date] = "Absent"
                    absent++
                }
            }

            val total =
                present + absent

            val percentage =
                if (total > 0) {

                    present.toDouble() /
                            total.toDouble() *
                            100.0

                } else {

                    0.0
                }

            result.add(
                SubjectStudentRow(
                    rollNo = student.rollNo,
                    studentName = student.studentName,
                    dailyStatus = statusMap,
                    present = present,
                    absent = absent,
                    total = total,
                    percentage = percentage
                )
            )
        }

        return result
    }

    // =====================================================
    // GET SUBJECT DATES
    // =====================================================

    private fun getSubjectDates(
        subject: String
    ): List<String> {

        return databaseHelper
            .getAllAttendance()
            .filter {

                it.subject.equals(
                    subject,
                    ignoreCase = true
                )
            }
            .map {
                it.attendance_date
            }
            .distinct()
            .sorted()
    }

    // =====================================================
    // FORMAT DATE
    // =====================================================

    private fun formatDate(
        date: String
    ): String {

        return try {

            val input =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val output =
                SimpleDateFormat(
                    "dd MMM",
                    Locale.getDefault()
                )

            output.format(
                input.parse(date)!!
            )

        } catch (e: Exception) {

            date
        }
    }

    // =====================================================
    // CELL
    // =====================================================

    @Composable
    fun Cell(
        text: String,
        width: Dp,
        header: Boolean = false,
        textColor: Color = Color.Black
    ) {

        Card(
            modifier = Modifier
                .width(width)
                .fillMaxHeight()
                .padding(1.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = text,
                    fontSize =
                        if (header) {
                            12.sp
                        } else {
                            12.sp
                        },
                    fontWeight =
                        if (header) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = textColor
                )
            }
        }
    }

    // =====================================================
    // EXPORT SUBJECT CSV
    // =====================================================

    private var exportContent = ""

    private fun exportSubjectCSV(
        subject: String,
        rows: List<SubjectStudentRow>,
        dates: List<String>
    ) {

        if (rows.isEmpty()) {

            Toast.makeText(
                this,
                "No attendance data to export",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val csv =
            StringBuilder()

        csv.append("Subject,")
        csv.append(
            escapeCSV(subject)
        )
        csv.append("\n\n")

        csv.append(
            "Roll No.,Student Name"
        )

        dates.forEach { date ->

            csv.append(",")

            csv.append(
                formatDate(date)
            )
        }

        csv.append(",Total Present")
        csv.append(",Total Absent")
        csv.append(",Total")
        csv.append(",Attendance %")
        csv.append("\n")

        rows.forEach { row ->

            csv.append(
                row.rollNo
            )

            csv.append(",")

            csv.append(
                escapeCSV(
                    row.studentName
                )
            )

            dates.forEach { date ->

                csv.append(",")

                csv.append(
                    row.dailyStatus[date]
                        ?: "Absent"
                )
            }

            csv.append(",")
            csv.append(row.present)

            csv.append(",")
            csv.append(row.absent)

            csv.append(",")
            csv.append(row.total)

            csv.append(",")

            csv.append(
                String.format(
                    Locale.getDefault(),
                    "%.2f%%",
                    row.percentage
                )
            )

            csv.append("\n")
        }

        exportContent =
            csv.toString()

        val intent =
            Intent(
                Intent.ACTION_CREATE_DOCUMENT
            ).apply {

                addCategory(
                    Intent.CATEGORY_OPENABLE
                )

                type = "text/csv"

                putExtra(
                    Intent.EXTRA_TITLE,
                    "${subject.replace(" ", "_")}_Attendance.csv"
                )
            }

        startActivityForResult(
            intent,
            EXPORT_REQUEST_CODE
        )
    }

    // =====================================================
    // SAVE EXPORTED FILE
    // =====================================================

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode ==
            EXPORT_REQUEST_CODE &&
            resultCode ==
            RESULT_OK &&
            data?.data != null
        ) {

            try {

                contentResolver
                    .openOutputStream(
                        data.data!!
                    )
                    ?.use { output ->

                        output.write(
                            exportContent
                                .toByteArray()
                        )
                    }

                Toast.makeText(
                    this,
                    "Subject report exported successfully",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Export failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun escapeCSV(
        value: String
    ): String {

        return if (
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n")
        ) {

            "\"" +
                    value.replace(
                        "\"",
                        "\"\""
                    ) +
                    "\""

        } else {

            value
        }
    }

    companion object {

        private const val EXPORT_REQUEST_CODE = 2001
    }

    override fun onDestroy() {

        databaseHelper.close()

        super.onDestroy()
    }
}

