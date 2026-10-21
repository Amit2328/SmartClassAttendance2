package com.example.smartclassattendance

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {

    companion object {

        private const val DATABASE_NAME = "SmartAttendance.db"
        private const val DATABASE_VERSION = 4

        // STUDENTS TABLE
        private const val STUDENTS_TABLE = "students"

        private const val ID = "id"
        private const val ROLL_NO = "roll_no"
        private const val REGISTRATION_NO = "registration_no"
        private const val STUDENT_NAME = "student_name"
        private const val COURSE_BRANCH = "course_branch"

        // ATTENDANCE TABLE
        private const val ATTENDANCE_TABLE = "attendance"

        private const val ATTENDANCE_ID = "id"
        private const val ATTENDANCE_ROLL_NO = "roll_no"
        private const val ATTENDANCE_REGISTRATION_NO = "registration_no"
        private const val ATTENDANCE_SUBJECT = "subject"
        private const val ATTENDANCE_DATE = "attendance_date"
        private const val ATTENDANCE_TIME = "attendance_time"
        private const val ATTENDANCE_STATUS = "status"
        private const val ATTENDANCE_LATITUDE = "latitude"
        private const val ATTENDANCE_LONGITUDE = "longitude"
    }

    // =====================================================
    // CREATE DATABASE
    // =====================================================

    override fun onCreate(db: SQLiteDatabase) {

        val createStudentsTable = """
            CREATE TABLE $STUDENTS_TABLE (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $ROLL_NO INTEGER NOT NULL UNIQUE,
                $REGISTRATION_NO TEXT NOT NULL UNIQUE,
                $STUDENT_NAME TEXT NOT NULL,
                $COURSE_BRANCH TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createStudentsTable)

        val createAttendanceTable = """
            CREATE TABLE $ATTENDANCE_TABLE (
                $ATTENDANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $ATTENDANCE_ROLL_NO INTEGER NOT NULL,
                $ATTENDANCE_REGISTRATION_NO TEXT NOT NULL,
                $ATTENDANCE_SUBJECT TEXT NOT NULL,
                $ATTENDANCE_DATE TEXT NOT NULL,
                $ATTENDANCE_TIME TEXT NOT NULL,
                $ATTENDANCE_STATUS TEXT NOT NULL,
                $ATTENDANCE_LATITUDE REAL,
                $ATTENDANCE_LONGITUDE REAL,

                UNIQUE(
                    $ATTENDANCE_ROLL_NO,
                    $ATTENDANCE_DATE,
                    $ATTENDANCE_SUBJECT
                )
            )
        """.trimIndent()

        db.execSQL(createAttendanceTable)
    }

    // =====================================================
    // DATABASE UPGRADE
    // =====================================================

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL("DROP TABLE IF EXISTS $ATTENDANCE_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $STUDENTS_TABLE")

        onCreate(db)
    }

    // =====================================================
    // REGISTER STUDENT
    // =====================================================

    fun registerStudent(
        rollNo: Int,
        registrationNo: String,
        studentName: String,
        courseBranch: String
    ): Boolean {

        if (isRollNumberExists(rollNo)) {
            return false
        }

        if (isRegistrationNumberExists(registrationNo)) {
            return false
        }

        val db = writableDatabase
        val values = ContentValues()

        values.put(ROLL_NO, rollNo)
        values.put(REGISTRATION_NO, registrationNo)
        values.put(STUDENT_NAME, studentName)
        values.put(COURSE_BRANCH, courseBranch)

        return try {

            val result = db.insert(
                STUDENTS_TABLE,
                null,
                values
            )

            result != -1L

        } catch (e: Exception) {

            false
        }
    }

    // =====================================================
    // CHECK ROLL NUMBER
    // =====================================================

    fun isRollNumberExists(
        rollNo: Int
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            STUDENTS_TABLE,
            arrayOf(ID),
            "$ROLL_NO = ?",
            arrayOf(rollNo.toString()),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =====================================================
    // CHECK REGISTRATION NUMBER
    // =====================================================

    fun isRegistrationNumberExists(
        registrationNo: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            STUDENTS_TABLE,
            arrayOf(ID),
            "$REGISTRATION_NO = ?",
            arrayOf(registrationNo),
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =====================================================
    // GET ALL STUDENTS
    // =====================================================

    fun getAllStudents(): List<Student> {

        val studentList = mutableListOf<Student>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                $ROLL_NO,
                $REGISTRATION_NO,
                $STUDENT_NAME,
                $COURSE_BRANCH
            FROM $STUDENTS_TABLE
            ORDER BY $ROLL_NO
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {

            do {

                val rollNo = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ROLL_NO)
                )

                val registrationNo = cursor.getString(
                    cursor.getColumnIndexOrThrow(REGISTRATION_NO)
                )

                val studentName = cursor.getString(
                    cursor.getColumnIndexOrThrow(STUDENT_NAME)
                )

                val courseBranch = cursor.getString(
                    cursor.getColumnIndexOrThrow(COURSE_BRANCH)
                )

                studentList.add(
                    Student(
                        rollNo = rollNo,
                        registrationNo = registrationNo,
                        studentName = studentName,
                        courseBranch = courseBranch
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()

        return studentList
    }

    // =====================================================
    // TOTAL STUDENTS
    // =====================================================

    fun getStudentCount(): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $STUDENTS_TABLE",
            null
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()

        return count
    }

    // =====================================================
    // SAME SUBJECT CHECK
    // =====================================================

    fun isAttendanceAlreadyMarked(
        rollNo: Int,
        subject: String,
        date: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.query(
            ATTENDANCE_TABLE,
            arrayOf(ATTENDANCE_ID),

            "$ATTENDANCE_ROLL_NO = ? AND " +
                    "$ATTENDANCE_DATE = ? AND " +
                    "$ATTENDANCE_SUBJECT = ?",

            arrayOf(
                rollNo.toString(),
                date,
                subject
            ),

            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()

        cursor.close()

        return exists
    }

    // =====================================================
    // GET LAST ATTENDANCE TIME OF TODAY
    // =====================================================

    fun getLastAttendanceTime(
        rollNo: Int,
        date: String
    ): Long? {

        val db = readableDatabase

        val cursor = db.query(
            ATTENDANCE_TABLE,

            arrayOf(
                ATTENDANCE_TIME
            ),

            "$ATTENDANCE_ROLL_NO = ? AND " +
                    "$ATTENDANCE_DATE = ?",

            arrayOf(
                rollNo.toString(),
                date
            ),

            null,
            null,

            "$ATTENDANCE_ID DESC",

            "1"
        )

        if (!cursor.moveToFirst()) {

            cursor.close()

            return null
        }

        val timeString = cursor.getString(
            cursor.getColumnIndexOrThrow(
                ATTENDANCE_TIME
            )
        )

        cursor.close()

        return try {

            val formatter = SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            )

            formatter.parse(timeString)?.time

        } catch (e: Exception) {

            null
        }
    }

    // =====================================================
    // CHECK 1 HOUR RULE
    // =====================================================

    fun canMarkNextAttendance(
        rollNo: Int,
        date: String
    ): Boolean {

        val lastAttendanceTime = getLastAttendanceTime(
            rollNo,
            date
        )

        // First attendance of the day
        if (lastAttendanceTime == null) {
            return true
        }

        val formatter = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )

        val currentTimeMillis = try {

            formatter.parse(
                formatter.format(Date())
            )?.time ?: return false

        } catch (e: Exception) {

            return false
        }

        val difference =
            currentTimeMillis - lastAttendanceTime

        val oneHour =
            60 * 60 * 1000L

        return difference >= oneHour
    }

    // =====================================================
    // REMAINING MINUTES
    // =====================================================

    fun getRemainingMinutes(
        rollNo: Int,
        date: String
    ): Long {

        val lastAttendanceTime =
            getLastAttendanceTime(
                rollNo,
                date
            ) ?: return 0

        val formatter = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )

        val currentTimeMillis = try {

            formatter.parse(
                formatter.format(Date())
            )?.time ?: return 0

        } catch (e: Exception) {

            return 0
        }

        val oneHour =
            60 * 60 * 1000L

        val difference =
            currentTimeMillis - lastAttendanceTime

        val remaining =
            oneHour - difference

        return if (remaining > 0) {

            (remaining / 60000L) + 1

        } else {

            0
        }
    }

    // =====================================================
    // SAVE ATTENDANCE
    // =====================================================

    fun saveAttendance(
        rollNo: Int,
        registrationNo: String,
        subject: String,
        date: String,
        time: String,
        status: String,
        latitude: Double?,
        longitude: Double?
    ): Boolean {

        // Same subject cannot be marked twice
        if (
            isAttendanceAlreadyMarked(
                rollNo,
                subject,
                date
            )
        ) {
            return false
        }

        // Different subject requires 1 hour gap
        if (
            !canMarkNextAttendance(
                rollNo,
                date
            )
        ) {
            return false
        }

        val db = writableDatabase

        val values = ContentValues()

        values.put(
            ATTENDANCE_ROLL_NO,
            rollNo
        )

        values.put(
            ATTENDANCE_REGISTRATION_NO,
            registrationNo
        )

        values.put(
            ATTENDANCE_SUBJECT,
            subject
        )

        values.put(
            ATTENDANCE_DATE,
            date
        )

        values.put(
            ATTENDANCE_TIME,
            time
        )

        values.put(
            ATTENDANCE_STATUS,
            status
        )

        if (latitude != null) {
            values.put(
                ATTENDANCE_LATITUDE,
                latitude
            )
        }

        if (longitude != null) {
            values.put(
                ATTENDANCE_LONGITUDE,
                longitude
            )
        }

        return try {

            val result = db.insert(
                ATTENDANCE_TABLE,
                null,
                values
            )

            result != -1L

        } catch (e: Exception) {

            false
        }
    }

    // =====================================================
    // TOTAL PRESENT
    // =====================================================

    fun getTotalPresent(): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT COUNT(*)
            FROM $ATTENDANCE_TABLE
            WHERE $ATTENDANCE_STATUS = 'Present'
            """.trimIndent(),
            null
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()

        return count
    }

    // =====================================================
    // TOTAL ABSENT
    // =====================================================

    fun getTotalAbsent(): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT COUNT(*)
            FROM $ATTENDANCE_TABLE
            WHERE $ATTENDANCE_STATUS = 'Absent'
            """.trimIndent(),
            null
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()

        return count
    }

    // =====================================================
    // TOTAL ATTENDANCE
    // =====================================================

    fun getTotalAttendance(): Int {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT COUNT(*)
            FROM $ATTENDANCE_TABLE
            """.trimIndent(),
            null
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()

        return count
    }

    // =====================================================
    // GET ALL ATTENDANCE
    // =====================================================

    fun getAllAttendance(): List<Attendance> {
        val attendanceList = mutableListOf<Attendance>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                $ATTENDANCE_ID,
                $ATTENDANCE_ROLL_NO,
                $ATTENDANCE_REGISTRATION_NO,
                $ATTENDANCE_SUBJECT,
                $ATTENDANCE_DATE,
                $ATTENDANCE_TIME,
                $ATTENDANCE_STATUS,
                $ATTENDANCE_LATITUDE,
                $ATTENDANCE_LONGITUDE
            FROM $ATTENDANCE_TABLE
            ORDER BY $ATTENDANCE_DATE DESC, $ATTENDANCE_TIME DESC
            """.trimIndent(),
            null
        )

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_ID)
                )
                val rollNo = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_ROLL_NO)
                )
                val registrationNo = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_REGISTRATION_NO)
                )
                val subject = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_SUBJECT)
                )
                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_DATE)
                )
                val time = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_TIME)
                )
                val status = cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_STATUS)
                )
                val latitude = if (
                    cursor.isNull(cursor.getColumnIndexOrThrow(ATTENDANCE_LATITUDE))
                ) "" else cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_LATITUDE)
                )
                val longitude = if (
                    cursor.isNull(cursor.getColumnIndexOrThrow(ATTENDANCE_LONGITUDE))
                ) "" else cursor.getString(
                    cursor.getColumnIndexOrThrow(ATTENDANCE_LONGITUDE)
                )

                attendanceList.add(
                    Attendance(
                        id = id,
                        roll_no = rollNo,
                        registration_no = registrationNo,
                        subject = subject,
                        attendance_date = date,
                        attendance_time = time,
                        status = status,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
        } finally {
            cursor.close()
        }

        return attendanceList
    }
    // =====================================================
    // SUBJECT-WISE DAILY REPORT
    // =====================================================

    fun getSubjectWiseDailyReport(): List<SubjectDailyReport> {

        val reportList = mutableListOf<SubjectDailyReport>()

        val db = readableDatabase

        val totalStudents = getStudentCount()

        val cursor = db.rawQuery(
            """
            SELECT
                $ATTENDANCE_DATE,
                $ATTENDANCE_SUBJECT,
                COUNT(*) AS present_count
            FROM $ATTENDANCE_TABLE
            WHERE $ATTENDANCE_STATUS = 'Present'
            GROUP BY
                $ATTENDANCE_DATE,
                $ATTENDANCE_SUBJECT
            ORDER BY
                $ATTENDANCE_DATE DESC,
                $ATTENDANCE_SUBJECT ASC
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {

            do {

                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        ATTENDANCE_DATE
                    )
                )

                val subject = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        ATTENDANCE_SUBJECT
                    )
                )

                val present = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "present_count"
                    )
                )

                val absent =
                    totalStudents - present

                reportList.add(
                    SubjectDailyReport(
                        date = date,
                        subject = subject,
                        present = present,
                        absent = absent,
                        total = totalStudents
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()

        return reportList
    }

    // =====================================================
    // DAILY REPORT
    // =====================================================

    fun getDailyReport(): List<DailyAttendanceReport> {

        val reportList = mutableListOf<DailyAttendanceReport>()

        val db = readableDatabase

        val totalStudents = getStudentCount()

        val cursor = db.rawQuery(
            """
            SELECT
                $ATTENDANCE_DATE,
                COUNT(
                    CASE
                        WHEN $ATTENDANCE_STATUS = 'Present'
                        THEN 1
                    END
                ) AS present_count
            FROM $ATTENDANCE_TABLE
            GROUP BY $ATTENDANCE_DATE
            ORDER BY $ATTENDANCE_DATE DESC
            """.trimIndent(),
            null
        )

        if (cursor.moveToFirst()) {

            do {

                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        ATTENDANCE_DATE
                    )
                )

                val present = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "present_count"
                    )
                )

                val absent =
                    totalStudents - present

                reportList.add(
                    DailyAttendanceReport(
                        date = date,
                        present = present,
                        absent = absent,
                        total = totalStudents
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()

        return reportList
    }
}


