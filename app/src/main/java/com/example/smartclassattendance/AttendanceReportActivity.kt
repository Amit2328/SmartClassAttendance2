package com.example.smartclassattendance

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

data class StudentDailyRow(
    val rollNo: Int,
    val studentName: String,
    val dailyStatus: Map<String, String>,
    val present: Int,
    val absent: Int,
    val total: Int,
    val percentage: Double
)

class AttendanceReportActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DatabaseHelper(this)

        setContent {
            AttendanceReportScreen()
        }
    }

    @Composable
    fun AttendanceReportScreen() {

        var rows by remember {
            mutableStateOf(loadStudentDailyReport())
        }

        var dates by remember {
            mutableStateOf(getAttendanceDates())
        }

        var subjectReports by remember {
            mutableStateOf(
                databaseHelper.getSubjectWiseDailyReport()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = "ATTENDANCE REPORT",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Text(
                text = "Student-wise Daily Attendance Sheet",
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    top = 5.dp,
                    bottom = 15.dp
                )
            )

            // =================================================
            // DAILY STUDENT SHEET
            // =================================================

            Text(
                text = "DAILY ATTENDANCE SHEET",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (rows.isEmpty()) {

                Text(
                    text = "No attendance data available",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

            } else {

                Row(
                    modifier = Modifier
                        .horizontalScroll(
                            rememberScrollState()
                        )
                ) {

                    Column {

                        // HEADER
                        Row(
                            modifier = Modifier
                                .height(55.dp)
                        ) {

                            SheetCell(
                                "Roll No.",
                                90.dp,
                                true
                            )

                            SheetCell(
                                "Student Name",
                                150.dp,
                                true
                            )

                            dates.forEach { date ->

                                SheetCell(
                                    formatDate(date),
                                    120.dp,
                                    true
                                )
                            }

                            SheetCell(
                                "Present",
                                90.dp,
                                true
                            )

                            SheetCell(
                                "Absent",
                                90.dp,
                                true
                            )

                            SheetCell(
                                "Total",
                                80.dp,
                                true
                            )

                            SheetCell(
                                "Attendance %",
                                120.dp,
                                true
                            )
                        }

                        // STUDENT ROWS
                        rows.forEach { row ->

                            Row(
                                modifier = Modifier
                                    .height(55.dp)
                            ) {

                                SheetCell(
                                    row.rollNo.toString(),
                                    90.dp
                                )

                                SheetCell(
                                    row.studentName,
                                    150.dp
                                )

                                dates.forEach { date ->

                                    val status =
                                        row.dailyStatus[date]
                                            ?: "Absent"

                                    SheetCell(
                                        status,
                                        120.dp,
                                        false,
                                        if (
                                            status == "Present"
                                        ) {
                                            Color(0xFF16A34A)
                                        } else {
                                            Color.Red
                                        }
                                    )
                                }

                                SheetCell(
                                    row.present.toString(),
                                    90.dp,
                                    false,
                                    Color(0xFF16A34A)
                                )

                                SheetCell(
                                    row.absent.toString(),
                                    90.dp,
                                    false,
                                    Color.Red
                                )

                                SheetCell(
                                    row.total.toString(),
                                    80.dp
                                )

                                SheetCell(
                                    String.format(
                                        Locale.getDefault(),
                                        "%.2f%%",
                                        row.percentage
                                    ),
                                    120.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // =================================================
            // SUBJECT-WISE DAILY
            // =================================================

            Text(
                text = "SUBJECT-WISE DAILY REPORT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (subjectReports.isEmpty()) {

                Text(
                    text = "No subject-wise attendance available",
                    color = Color.Gray
                )

            } else {

                Row(
                    modifier = Modifier
                        .horizontalScroll(
                            rememberScrollState()
                        )
                ) {

                    Column {

                        Row(
                            modifier = Modifier.height(55.dp)
                        ) {

                            SheetCell(
                                "Date",
                                120.dp,
                                true
                            )

                            SheetCell(
                                "Subject",
                                150.dp,
                                true
                            )

                            SheetCell(
                                "Present",
                                100.dp,
                                true
                            )

                            SheetCell(
                                "Absent",
                                100.dp,
                                true
                            )

                            SheetCell(
                                "Total",
                                90.dp,
                                true
                            )
                        }

                        subjectReports.forEach { report ->

                            Row(
                                modifier = Modifier.height(55.dp)
                            ) {

                                SheetCell(
                                    report.date,
                                    120.dp
                                )

                                SheetCell(
                                    report.subject,
                                    150.dp
                                )

                                SheetCell(
                                    report.present.toString(),
                                    100.dp,
                                    false,
                                    Color(0xFF16A34A)
                                )

                                SheetCell(
                                    report.absent.toString(),
                                    100.dp,
                                    false,
                                    Color.Red
                                )

                                SheetCell(
                                    report.total.toString(),
                                    90.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =================================================
            // SUMMARY
            // =================================================

            val totalPresent =
                databaseHelper.getTotalPresent()

            val totalAbsent =
                databaseHelper.getTotalAbsent()

            val totalAttendance =
                databaseHelper.getTotalAttendance()

            Text(
                text = "OVERALL SUMMARY",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A)
            )

            Spacer(modifier = Modifier.height(10.dp))

            SummaryCard(
                "Total Present",
                totalPresent.toString(),
                Color(0xFF16A34A)
            )

            SummaryCard(
                "Total Absent",
                totalAbsent.toString(),
                Color.Red
            )

            SummaryCard(
                "Total Attendance",
                totalAttendance.toString(),
                Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // =================================================
            // EXPORT
            // =================================================

            Button(
                onClick = {

                    exportCSV(
                        rows,
                        dates
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "EXPORT ATTENDANCE CSV",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =================================================
            // REFRESH
            // =================================================

            Button(
                onClick = {

                    rows =
                        loadStudentDailyReport()

                    dates =
                        getAttendanceDates()

                    subjectReports =
                        databaseHelper
                            .getSubjectWiseDailyReport()

                    Toast.makeText(
                        this@AttendanceReportActivity,
                        "Report Refreshed",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "REFRESH REPORT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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
    // LOAD STUDENT DAILY REPORT
    // =====================================================

    private fun loadStudentDailyReport(): List<StudentDailyRow> {

        val students =
            databaseHelper.getAllStudents()

        val attendance =
            databaseHelper.getAllAttendance()

        val dates =
            attendance
                .map { it.attendance_date }
                .distinct()
                .sorted()

        val rows =
            mutableListOf<StudentDailyRow>()

        students.forEach { student ->

            val statusMap =
                mutableMapOf<String, String>()

            var present = 0
            var absent = 0

            dates.forEach { date ->

                val records =
                    attendance.filter {

                        it.roll_no == student.rollNo.toString() &&
                                it.attendance_date == date
                    }

                if (records.isEmpty()) {

                    statusMap[date] = "Absent"

                    absent++

                } else {

                    val hasPresent =
                        records.any {
                            it.status.equals(
                                "Present",
                                ignoreCase = true
                            )
                        }

                    if (hasPresent) {

                        statusMap[date] = "Present"

                        present++

                    } else {

                        statusMap[date] = "Absent"

                        absent++
                    }
                }
            }

            val total =
                present + absent

            val percentage =
                if (total > 0) {
                    (present.toDouble() / total) * 100
                } else {
                    0.0
                }

            rows.add(
                StudentDailyRow(
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

        return rows
    }

    // =====================================================
    // GET DATES
    // =====================================================

    private fun getAttendanceDates(): List<String> {

        return databaseHelper
            .getAllAttendance()
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

            val parsed =
                input.parse(date)

            output.format(parsed!!)

        } catch (e: Exception) {

            date
        }
    }

    // =====================================================
    // SHEET CELL
    // =====================================================

    @Composable
    fun SheetCell(
        text: String,
        width: androidx.compose.ui.unit.Dp,
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
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = text,
                    fontSize = if (header) {
                        13.sp
                    } else {
                        12.sp
                    },
                    fontWeight = if (header) {
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
    // SUMMARY CARD
    // =====================================================

    @Composable
    fun SummaryCard(
        title: String,
        value: String,
        color: Color
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = color
                )
            }
        }
    }

    // =====================================================
    // EXPORT CSV
    // =====================================================

    private fun exportCSV(
        rows: List<StudentDailyRow>,
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

        val csv = StringBuilder()

        csv.append("Roll No.,Student Name")

        dates.forEach { date ->

            csv.append(",")
            csv.append(formatDate(date))
        }

        csv.append(",Total Present")
        csv.append(",Total Absent")
        csv.append(",Total")
        csv.append(",Attendance %")

        csv.append("\n")

        rows.forEach { row ->

            csv.append(row.rollNo)
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

        val fileName =
            "Attendance_Report.csv"

        val intent =
            Intent(Intent.ACTION_CREATE_DOCUMENT).apply {

                addCategory(
                    Intent.CATEGORY_OPENABLE
                )

                type = "text/csv"

                putExtra(
                    Intent.EXTRA_TITLE,
                    fileName
                )
            }

        exportContent = csv.toString()

        startActivityForResult(
            intent,
            EXPORT_REQUEST_CODE
        )
    }

    private var exportContent: String = ""

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: android.content.Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == EXPORT_REQUEST_CODE &&
            resultCode == RESULT_OK &&
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
                    "Attendance exported successfully",
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

        private const val EXPORT_REQUEST_CODE = 1001
    }

    override fun onDestroy() {

        databaseHelper.close()

        super.onDestroy()
    }
}


