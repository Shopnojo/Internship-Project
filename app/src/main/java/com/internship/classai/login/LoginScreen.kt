package com.internship.classai.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.internship.classai.navigation.Routes

@Composable
fun LoginScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry
) {
    val loginType = backStackEntry
        .arguments
        ?.getString("type")
        ?: "employee"

    val title = if (loginType == "admin") {
        "Sign in as Admin"
    } else {
        "Sign in as Employee"
    }

    var userId by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = userId,
            onValueChange = {
                userId = it
            },
            label = {
                Text("User ID")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                // Authentication will be connected to FastAPI next.
                // For now, this only moves to the existing app.
                navController.navigate(Routes.STUDENTS) {
                    popUpTo(Routes.LOGIN) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("SIGN IN")
        }
    }
}