package com.example.smartclassattendance

import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {

        var username by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "SMART CLASS ATTENDANCE",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF16A34A),
                modifier = Modifier.padding(
                    bottom = 35.dp
                )
            )

            OutlinedTextField(
                value = username,

                onValueChange = {
                    username = it
                },

                label = {
                    Text("Username")
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true
            )

            OutlinedTextField(
                value = password,

                onValueChange = {
                    password = it
                },

                label = {
                    Text("Password")
                },

                visualTransformation =
                    PasswordVisualTransformation(),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),

                singleLine = true
            )

            Button(
                onClick = {

                    if (
                        username.trim() == "admin" &&
                        password == "1234"
                    ) {

                        Toast.makeText(
                            this@MainActivity,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@MainActivity,
                            AdminDashboardActivity::class.java
                        )

                        startActivity(intent)

                    } else {

                        Toast.makeText(
                            this@MainActivity,
                            "Invalid Username or Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(top = 5.dp)
            ) {

                Text(
                    text = "LOGIN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}