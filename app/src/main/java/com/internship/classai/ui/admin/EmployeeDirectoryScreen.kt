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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.internship.classai.data.model.Employee
import com.internship.classai.data.model.School
import com.internship.classai.data.model.AdminStatusUpdate
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.theme.AppColors

@Composable
fun EmployeeDirectoryScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var schools by remember { mutableStateOf<List<School>>(emptyList()) }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    suspend fun loadData() {
        employees = RetrofitClient.apiService.getEmployees()
        schools = RetrofitClient.apiService.getSchools().filter { it.isActive == 1 }
    }

    LaunchedEffect(Unit) {
        try {
            loadData()
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
            onMenuClick = { navController.popBackStack() }
        )

        ScreenHeader(
            title = "Employees",
            subtitle = "Manage employee accounts"
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                key = { employee -> employee.id }
            ) { employee ->
                EmployeeCard(
                    employee = employee,
                    loading = loading,
                    onEdit = { selectedEmployee = employee },
                    onStatusChange = { newStatus ->
                        scope.launch {
                            loading = true
                            try {
                                val response = RetrofitClient.apiService.updateEmployeeStatus(
                                    employee.id,
                                    AdminStatusUpdate(newStatus)
                                )
                                val success = response["success"] as? Boolean ?: false
                                if (success) {
                                    loadData()
                                } else {
                                    Toast.makeText(
                                        context,
                                        response["message"]?.toString() ?: "Failed to update status",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Failed to update employee status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                loading = false
                            }
                        }
                    }
                )
            }
        }
    }

    selectedEmployee?.let { employee ->
        EditEmployeeDialog(
            employee = employee,
            schools = schools,
            onDismiss = { selectedEmployee = null },
            onSave = { updated ->
                scope.launch {
                    loading = true
                    try {
                        val response = RetrofitClient.apiService.updateEmployee(
                            employee.id,
                            updated
                        )
                        val success = response["success"] as? Boolean ?: false
                        if (success) {
                            loadData()
                            Toast.makeText(
                                context,
                                "Employee updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedEmployee = null
                        } else {
                            Toast.makeText(
                                context,
                                response["message"]?.toString() ?: "Failed to update employee",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Failed to update employee",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        loading = false
                    }
                }
            }
        )
    }
}

@Composable
private fun EmployeeCard(
    employee: Employee,
    loading: Boolean,
    onEdit: () -> Unit,
    onStatusChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "User ID: ${employee.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Phone: ${employee.mobile}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "School: ${employee.schoolName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (employee.isActive == 1) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (employee.isActive == 1) AppColors.PrimaryEnd else AppColors.TextSecondary
                )
            }

            IconButton(
                onClick = onEdit,
                enabled = !loading
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit employee",
                    tint = AppColors.PrimaryEnd
                )
            }

            Switch(
                checked = employee.isActive == 1,
                onCheckedChange = { checked ->
                    if (!loading) onStatusChange(if (checked) 1 else 0)
                },
                enabled = !loading
            )
        }
    }
}

@Composable
private fun EditEmployeeDialog(
    employee: Employee,
    schools: List<School>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit
) {
    var fullName by remember(employee.id) { mutableStateOf(employee.fullName) }
    var mobile by remember(employee.id) { mutableStateOf(employee.mobile) }
    var userId by remember(employee.id) { mutableStateOf(employee.userId) }
    var password by remember(employee.id) { mutableStateOf("") }
    var selectedSchool by remember(employee.id) {
        mutableStateOf(
            schools.firstOrNull { it.id == employee.schoolId }
                ?: schools.firstOrNull()
        )
    }
    var schoolMenuExpanded by remember(employee.id) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Employee") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { value ->
                        mobile = value.filter { it.isDigit() }.take(10)
                    },
                    label = { Text("Mobile") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    OutlinedButton(
                        onClick = { schoolMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedSchool?.schoolName ?: "Select School")
                    }

                    DropdownMenu(
                        expanded = schoolMenuExpanded,
                        onDismissRequest = { schoolMenuExpanded = false }
                    ) {
                        schools.forEach { school ->
                            DropdownMenuItem(
                                text = { Text(school.schoolName) },
                                onClick = {
                                    selectedSchool = school
                                    schoolMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val school = selectedSchool ?: return@Button
                    val request = mutableMapOf<String, Any?>(
                        "schoolId" to school.id,
                        "fullName" to fullName.trim(),
                        "mobile" to mobile,
                        "userId" to userId.trim(),
                        "password" to password.ifBlank { null }
                    )
                    onSave(request)
                },
                enabled = fullName.isNotBlank()
                        && mobile.length == 10
                        && userId.isNotBlank()
                        && selectedSchool != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
