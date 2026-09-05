package com.internship.classai.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.internship.classai.navigation.Routes

@Composable
fun LoginTypeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ClassAI",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        Button(
            onClick = {
                navController.navigate("${Routes.LOGIN}?type=admin")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in as Admin")
        }

        Button(
            onClick = {
                navController.navigate("${Routes.LOGIN}?type=employee")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Sign in as Employee")
        }
    }
}