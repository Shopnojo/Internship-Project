package com.internship.classai.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.data.model.Employee
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.theme.AppColors

@Composable
fun EmployeeDirectoryScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    var employees by remember {
        mutableStateOf<List<Employee>>(emptyList())
    }

    LaunchedEffect(Unit) {
        try {
            employees = RetrofitClient.apiService.getEmployees()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to load employees",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {

        AppToolbar(
            onMenuClick = {
                navController.popBackStack()
            }
        )

        ScreenHeader(
            title = "Employees",
            subtitle = "Manage employee accounts"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
        ) {
            items(
                items = employees,
                key = { employee ->
                    employee.id
                }
            ) { employee ->

                EmployeeCard(
                    employee = employee
                )
            }
        }
    }
}

@Composable
private fun EmployeeCard(
    employee: Employee
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Employee",
                tint = AppColors.PrimaryEnd
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "User ID: ${employee.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Phone: ${employee.mobile}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "School: ${employee.schoolName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Switch(
                checked = employee.isActive == 1,
                onCheckedChange = {
                    // Toggle will be connected to FastAPI in the next step.
                }
            )
        }
    }
}