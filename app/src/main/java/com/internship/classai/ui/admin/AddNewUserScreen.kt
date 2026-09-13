package com.internship.classai.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.data.model.AdminCreate
import com.internship.classai.data.model.EmployeeCreate
import com.internship.classai.data.model.School
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun AddNewUserScreen(
    navController: NavHostController
) {
    var isAdmin by remember {
        mutableStateOf(true)
    }

    /*
     * Common user fields
     */
    var fullName by remember {
        mutableStateOf("")
    }

    var userId by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    /*
     * School selection
     */
    var schools by remember {
        mutableStateOf<List<School>>(emptyList())
    }

    var selectedSchoolId by remember {
        mutableStateOf<Int?>(null)
    }

    var selectedSchoolName by remember {
        mutableStateOf("")
    }

    var schoolMenuExpanded by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    /*
     * Load active schools whenever Employee mode is selected.
     */
    LaunchedEffect(isAdmin) {
        if (!isAdmin) {
            try {
                schools = RetrofitClient.apiService
                    .getSchools()
                    .filter { it.isActive == 1 }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to load schools",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {

        /*
         * Existing ClassAI toolbar.
         */
        AppToolbar(
            onMenuClick = {
                navController.popBackStack()
            }
        )

        /*
         * Main Add User card
         */
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Card
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                /*
                 * Header
                 */
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = AppColors.PrimaryEnd,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            tint = AppColors.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )

                    Column {
                        Text(
                            text = if (isAdmin) {
                                "Add School Admin"
                            } else {
                                "Add Employee"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = if (isAdmin) {
                                "Grant management access with secure credentials"
                            } else {
                                "Create a new employee account"
                            },
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                /*
                 * Admin / Employee selector
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = AppColors.Background,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = {
                            isAdmin = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdmin) {
                                AppColors.White
                            } else {
                                AppColors.Background
                            },
                            contentColor = AppColors.TextPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isAdmin) {
                                1.dp
                            } else {
                                0.dp
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )

                        Spacer(
                            modifier = Modifier.size(5.dp)
                        )

                        Text(
                            text = "Add Admin",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            isAdmin = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAdmin) {
                                AppColors.White
                            } else {
                                AppColors.Background
                            },
                            contentColor = AppColors.TextPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (!isAdmin) {
                                1.dp
                            } else {
                                0.dp
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )

                        Spacer(
                            modifier = Modifier.size(5.dp)
                        )

                        Text(
                            text = "Add Employee",
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                /*
                 * Employee School
                 */
                if (!isAdmin) {
                    FieldLabel(
                        text = "School"
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedSchoolName,
                            onValueChange = {},
                            readOnly = true,
                            enabled = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    schoolMenuExpanded = true
                                },
                            placeholder = {
                                Text(
                                    text = "Select school",
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Select school"
                                )
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    schoolMenuExpanded = true
                                }
                        )

                        DropdownMenu(
                            expanded = schoolMenuExpanded,
                            onDismissRequest = {
                                schoolMenuExpanded = false
                            }
                        ) {
                            if (schools.isEmpty()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "No active schools available"
                                        )
                                    },
                                    onClick = {
                                        schoolMenuExpanded = false
                                    }
                                )
                            } else {
                                schools.forEach { school ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = school.schoolName
                                            )
                                        },
                                        onClick = {
                                            selectedSchoolId = school.id
                                            selectedSchoolName = school.schoolName
                                            schoolMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    /*
                     * Employee Full Name
                     */
                    FieldLabel(
                        text = "Full Name"
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Enter employee name",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {

                    /*
                     * Admin Full Name
                     */
                    FieldLabel(
                        text = "Full Name"
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Enter admin name",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                /*
                 * Phone
                 *
                 * Only digits are accepted.
                 * Maximum length is 10 digits.
                 */
                FieldLabel(
                    text = "Phone Number"
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->
                        if (
                            newValue.all { char -> char.isDigit() } &&
                            newValue.length <= 10
                        ) {
                            phone = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter 10-digit phone number",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                /*
                 * User ID
                 */
                FieldLabel(
                    text = "User ID"
                )

                OutlinedTextField(
                    value = userId,
                    onValueChange = {
                        userId = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = if (isAdmin) {
                                "Enter admin user ID"
                            } else {
                                "Enter employee user ID"
                            },
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                /*
                 * Password
                 */
                FieldLabel(
                    text = "Password"
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter secure password",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                /*
                 * Add User button
                 */
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isAdmin) {
                                    "Please enter admin name"
                                } else {
                                    "Please enter employee name"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (phone.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please enter phone number",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (!phone.matches(Regex("\\d{1,10}"))) {
                            Toast.makeText(
                                context,
                                "Phone number must contain only digits and be at most 10 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (userId.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please enter User ID",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (password.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please enter password",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (!isAdmin && selectedSchoolId == null) {
                            Toast.makeText(
                                context,
                                "Please select a school",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (isLoading) {
                            return@Button
                        }

                        coroutineScope.launch {
                            isLoading = true

                            try {
                                if (isAdmin) {

                                    val response =
                                        RetrofitClient.apiService.createAdmin(
                                            AdminCreate(
                                                fullName = fullName.trim(),
                                                mobile = phone.trim(),
                                                userId = userId.trim(),
                                                password = password
                                            )
                                        )

                                    val success =
                                        response["success"] as? Boolean ?: false

                                    val message =
                                        response["message"]?.toString()
                                            ?: "Unknown server response"

                                    Toast.makeText(
                                        context,
                                        message,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    if (success) {
                                        fullName = ""
                                        userId = ""
                                        phone = ""
                                        password = ""
                                    }

                                } else {

                                    val response =
                                        RetrofitClient.apiService.createEmployee(
                                            EmployeeCreate(
                                                schoolId = selectedSchoolId!!,
                                                fullName = fullName.trim(),
                                                mobile = phone.trim(),
                                                userId = userId.trim(),
                                                password = password
                                            )
                                        )

                                    val success =
                                        response["success"] as? Boolean ?: false

                                    val message =
                                        response["message"]?.toString()
                                            ?: "Unknown server response"

                                    Toast.makeText(
                                        context,
                                        message,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    if (success) {
                                        fullName = ""
                                        userId = ""
                                        phone = ""
                                        password = ""
                                        selectedSchoolId = null
                                        selectedSchoolName = ""
                                    }
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    if (isAdmin) {
                                        "Failed to create admin: ${e.message}"
                                    } else {
                                        "Failed to create employee: ${e.message}"
                                    },
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Black,
                        contentColor = AppColors.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(
                        modifier = Modifier.size(6.dp)
                    )

                    Text(
                        text = if (isLoading) {
                            "Creating..."
                        } else if (isAdmin) {
                            "Add Admin"
                        } else {
                            "Add Employee"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(
    text: String
) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.TextPrimary
    )
}