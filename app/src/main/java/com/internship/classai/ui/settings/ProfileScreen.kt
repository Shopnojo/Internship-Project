package com.internship.classai.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.login.LoginSession
import com.internship.classai.ui.theme.AppColors

@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val session = LoginSession(context)
    val userId = session.getUserId()
    val roleKey = if (session.isAdmin()) "admin" else "employee"

    var profile by remember { mutableStateOf<Map<String, Any?>>(emptyMap()) }

    LaunchedEffect(userId, roleKey) {
        if (userId.isNotBlank()) {
            try {
                val response = RetrofitClient.apiService.getProfile(
                    userId = userId,
                    role = roleKey
                )
                if (response["success"] as? Boolean == true) {
                    profile = response
                } else {
                    Toast.makeText(
                        context,
                        response["message"]?.toString() ?: "Failed to load profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to load profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val displayName = profile["fullName"]?.toString().orEmpty().ifBlank { userId.ifBlank { "User" } }
    val displayUserId = profile["userId"]?.toString().orEmpty().ifBlank { userId }
    val mobile = profile["mobile"]?.toString().orEmpty().ifBlank { "Not available" }
    val school = profile["schoolName"]?.toString().orEmpty().ifBlank {
        if (roleKey == "admin") "Not applicable" else "Not available"
    }
    val role = profile["role"]?.toString().orEmpty().ifBlank {
        if (roleKey == "admin") "Administrator" else "Employee"
    }
    val accountActive = (profile["isActive"] as? Number)?.toInt()
        ?: profile["isActive"]?.toString()?.toIntOrNull()
        ?: 0
    val schoolActive = (profile["schoolIsActive"] as? Number)?.toInt()
        ?: profile["schoolIsActive"]?.toString()?.toIntOrNull()
        ?: 1
    val status = if (accountActive == 1 && schoolActive == 1) "Active" else "Inactive"

    Scaffold(
        topBar = {
            AppToolbar(
                onMenuClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "View your account information",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AppColors.Background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = AppColors.PrimaryEnd
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = displayName,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = role,
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Account Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Full Name",
                        value = displayName
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "User ID",
                        value = displayUserId
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone",
                        value = mobile
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "School",
                        value = school
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Role",
                        value = role
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Account Status",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (status == "Active") AppColors.PrimaryEnd
                                else AppColors.TextSecondary
                            )
                    )

                    Text(
                        text = status,
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = AppColors.PrimaryEnd
        )

        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        }
    }
}
